package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import tech.tablesaw.api.*;

import java.util.*;
import java.util.stream.Stream;

public class RulesRegistry {
    private Table rules;
    private final Table bodies;
    private Table results;
    private Long transactionCount;
    private QueryNode treeOfQueries;
    private final Set<QueryNode> visitedQueries;

    private Map<String, String> columnDictionary;

    public RulesRegistry(){
        this.rules = Table.create("rules");
        this.bodies = Table.create("bodies");
        this.transactionCount = 0L;
        this.visitedQueries = new HashSet<>();
        this.columnDictionary = new HashMap<>();
    }

    public void initTreeOfQueries(Query query){
        this.treeOfQueries = new QueryNode(query);

        query.getRuleColumnNames().forEach(column -> {
            if(column.equals("suppcount")){
                this.rules.addColumns(LongColumn.create(column));
            } else {
                this.rules.addColumns(StringColumn.create(column));
            }
        });

        query.getBodyColumnNames().forEach(column -> {
            if(column.equals("suppcount")){
                this.bodies.addColumns(LongColumn.create(column));
            } else {
                this.bodies.addColumns(StringColumn.create(column));
            }
        });
        this.columnDictionary.putAll(query.getColumnsNewNames());
    }

    public void retrieveTransactionsCount(GraphDatabaseService db, Query query){
        try(Transaction tx = db.beginTx()){

            StringBuilder sb = new StringBuilder();
            sb.append("MATCH (").append(query.getAnchor()).append(":").append(query.getAnchorLabel()).append(") ");
            if(!query.getAnchorWhereClause().isEmpty()){
                    sb.append("WHERE ").append(query.getAnchorWhereClause());
            }
            sb.append(" RETURN count(").append(query.getAnchor()).append(") AS count");

            Result result = tx.execute(sb.toString());
//            Result result = tx.execute("MATCH (" + query.getAnchor() + ":" + query.getAnchorLabel() + ") "
//                    + "WHERE " + query.getAnchorWhereClause()
//                    + " RETURN count("+ query.getAnchor() +") AS count");
            if(result.hasNext()) {
                transactionCount = (Long) result.next().get("count");
                tx.commit();
            }
        }
    }

    public void retrieveBodies(GraphDatabaseService db){
        visitedQueries.forEach(queryNode -> {
            if(queryNode.getHalt()){
                return;
            }
            List<Map<String, Object>> bodies = new ArrayList<>();
            try(Transaction tx = db.beginTx()){
                Result result = tx.execute(queryNode.getBodyInCypher(transactionCount));
                while(result.hasNext()){
                    Map<String, Object> row = result.next();
                    HashMap<String, Object> materializedRow = new HashMap<>();
                    for (String key : result.columns()) {
                        materializedRow.put(key, row.get(key));
                    }
                    bodies.add(materializedRow);
                }
                tx.commit();
            }
            if(!bodies.isEmpty()) {
                for (Map<String, Object> body : bodies) {
                    Row row = this.bodies.appendRow();
                    for (String key : body.keySet()) {
                        if(key.equals("suppcount")){
                            row.setLong(key, (Long) body.get(key));
                        } else {
                            row.setString(key, (String) body.get(key));
                        }
                    }
                }
            }
        });
    }
    public void retrieveRules(GraphDatabaseService db) {
        retrieveRulesHelper(db, treeOfQueries);
    }

    private void retrieveRulesHelper(GraphDatabaseService db, QueryNode queryNode){
        List<Map<String, Object>> rules = new ArrayList<>();

        if(queryNode.getHalt()){
            return;
        }

        try(Transaction tx = db.beginTx()){
            Result result = tx.execute(queryNode.getRuleInCypher(transactionCount));
            while(result.hasNext()){
                Map<String, Object> row = result.next();
                HashMap<String, Object> materializedRow = new HashMap<>();
                for (String key : result.columns()) {
                    materializedRow.put(key, row.get(key));
                }
                rules.add(materializedRow);
            }
            tx.commit();
        }
        // Print for debug
//        System.out.println(queryNode.getRuleInCypher());
//        System.out.println();

        if (!rules.isEmpty()) {
            for (Map<String, Object> rule : rules) {
                Row row = this.rules.appendRow();
                for (String key : rule.keySet()) {
                    if (key.equals("suppcount")) {
                        row.setLong(key, (Long) rule.get(key));
                    } else {
                        row.setString(key, (String) rule.get(key));
                    }
                }
            }
        }

        // If the queryNode does not produce rules, halt
        if (rules.isEmpty()) {
            queryNode.setHalt(true);
            return;
        }
        // Generate children nodes
        queryNode.generateChildren();

        // Add queryNode to visitedQueries
        visitedQueries.add(queryNode);
        // Recursively call retrieveRulesHelper on children nodes
        for(QueryNode child : queryNode.getChildren()){
            if (!visitedQueries.contains(child)) {
                retrieveRulesHelper(db, child);
            }
        }
    }

    public void combineRules() {
        // Combine rules and bodies
        // Store the results in tableResults
        String[] bodyColumns = this.bodies.columnNames().stream()
                .filter(column -> column.contains("body"))
                .toArray(String[]::new);
        this.results = this.rules.joinOn(bodyColumns).inner(true, this.bodies);

    }

    public void removeTautologiesOld(){
        // Remove tautologies from table Results
        // A tautology is a rule where the head and body are the same
        // Check if tautologies are possible at schema level
        List<String> headColumns = this.rules.columnNames().stream()
                .filter(column -> column.contains("head"))
                .toList();
        List<String> bodyColumns = this.rules.columnNames().stream()
                .filter(column -> column.contains("body"))
                .toList();
        if(headColumns.get(0).replace("head", "body").equals(bodyColumns.get(0))) {
            this.rules = this.rules.where(
                    this.rules.stringColumn(headColumns.get(0))
                            .isNotEqualTo(this.rules.stringColumn(bodyColumns.get(0))));
        }
    }

    public void removeTautologies(){
        // Remove tautologies from table Results
        // A tautology is a rule where the head and body are the same
        // Check if tautologies are possible at schema level

        List<String> headColumns = this.rules.columnNames().stream()
                .filter(column -> column.contains("head"))
                .toList();
        List<String> bodyColumns = this.rules.columnNames().stream()
                .filter(column -> column.contains("body"))
                .toList();

        List<String> columnNames = new ArrayList<>();
        columnNames.addAll(headColumns);
        columnNames.addAll(bodyColumns);

        // reorder in alphabetical order the columns
        List<String> sortedColumnNames = columnNames.stream().sorted().toList();

        // group columns based on their itemset
        Map<String, List<String>> groupedColumns = new LinkedHashMap<>();

        for (String columnName : columnNames) {
            String[] parts = columnName.split("_");
            String cardinality = columnName.substring(columnName.length() - 1);
            if (Character.isDigit(cardinality.charAt(0))) {
                cardinality = cardinality;
            }
            else { cardinality = "";}
            String indexPart = parts[0] + cardinality; // head0, head01, head1 etc.
            groupedColumns.computeIfAbsent(indexPart, k -> new ArrayList<>()).add(columnName);
        }

        // create aggregated list
        List<Object> aggregatedList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedColumns.entrySet()) {
            List<String> columns = entry.getValue();
            if (columns.size() == 1) {
                aggregatedList.add(columns.get(0));
            } else {
                aggregatedList.add(columns);
            }
        }

        List<List<Object>> pairedColumns = createPairs(aggregatedList);

        for (List<Object> pair : pairedColumns) {
            if (pair.get(0) instanceof String) {
                String col1Name = (String) pair.get(0);
                String col2Name = (String) pair.get(1);
                this.rules = this.rules.where(this.rules.stringColumn(col1Name)
                        .isNotEqualTo(this.rules.stringColumn(col2Name)).or(this.rules.stringColumn(col1Name).isEqualTo("")));
            } else if (pair.get(0) instanceof List) {
                    List<String> colList1 = (List<String>) pair.get(0);
                    List<String> colList2 = (List<String>) pair.get(1);
                    var condition = this.rules.stringColumn(colList1.get(0))
                            .isNotEqualTo(this.rules.stringColumn(colList2.get(0)))
                            .or(this.rules.stringColumn(colList1.get(0)).isEqualTo(""));
                    for (int i = 1; i < colList1.size(); i++) {
                        condition = condition.and(this.rules.stringColumn(colList1.get(i))
                                .isNotEqualTo(this.rules.stringColumn(colList2.get(i)))
                                .or(this.rules.stringColumn(colList1.get(i)).isEqualTo("")));
                    }
                    this.rules = this.rules.where(condition);
            }
        }

    }

    public static List<List<Object>> createPairs(List<Object> columnList) {
        List<List<Object>> pairs = new ArrayList<>();

        for (int i = 0; i < columnList.size(); i++) {
            for (int j = i + 1; j < columnList.size(); j++) {
                Object col1 = columnList.get(i);
                Object col2 = columnList.get(j);

                if (haveSameStructure(col1, col2)) {
                    pairs.add(Arrays.asList(col1, col2));
                }
            }
        }

        return pairs;
    }

    public static boolean haveSameStructure(Object col1, Object col2) {
        if (col1 instanceof String && col2 instanceof String) {
            return getStructure((String) col1).equals(getStructure((String) col2));
        } else if (col1 instanceof List && col2 instanceof List) {
            List<String> list1 = (List<String>) col1;
            List<String> list2 = (List<String>) col2;

            if (list1.size() != list2.size()) {
                return false;
            }

            for (int i = 0; i < list1.size(); i++) {
                if (!getStructure(list1.get(i)).equals(getStructure(list2.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static String getStructure(String columnName) {
        String structure = columnName.replaceAll("\\d+", "");
        structure = structure.replaceAll("body", "head");
        return structure;
    }

    public void computeMetrics(){
        // Compute confidence and support
        // Store the results in tableResults
        // support = suppcount_rule / totalTransactions
        // confidence = suppcount_rule / suppcount_body
        this.results.addColumns(
                this.results.longColumn("suppcount")
                        .asDoubleColumn()
                        .divide(transactionCount)
                        .setName("support"),
                this.results.longColumn("suppcount")
                        .asDoubleColumn()
                        .divide(this.results.longColumn("T2.suppcount").asDoubleColumn())
                        .setName("confidence"));
    }

    public void filterBySupportAndConfidence(double minSupport, double minConfidence) {
        // Filter by support and confidence
        // Store the results in tableResults
        this.results = this.results.where(
//                this.results.doubleColumn("support").isGreaterThanOrEqualTo(minSupport)
//                        .and(this.results.doubleColumn("confidence")
//                                .isGreaterThanOrEqualTo(minConfidence)));
                this.results.doubleColumn("confidence").isGreaterThanOrEqualTo(minConfidence));
    }
    public Stream<AssociationRule.Record> getResults() {
        List<String> headColumns = this.results.columnNames().stream()
                .filter(column -> column.contains("head"))
                .toList();

        List<String> bodyColumns = this.results.columnNames().stream()
                .filter(column -> column.contains("body"))
                .toList();

        return this.results.stream().map(row -> {
            AssociationRule.AssociationRuleBuilder builder = new AssociationRule.AssociationRuleBuilder()
                    .setSupport(row.getDouble("support"))
                    .setConfidence(row.getDouble("confidence"));

            headColumns.forEach(column -> builder.addHead(column, row.getString(column), this.columnDictionary));
            bodyColumns.forEach(column -> builder.addBody(column, row.getString(column), this.columnDictionary));

            return builder.build().toRecord();
        });
    }
}
