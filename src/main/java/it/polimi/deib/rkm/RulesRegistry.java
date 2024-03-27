package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import tech.tablesaw.api.*;

import java.util.*;
import java.util.stream.Stream;

public class RulesRegistry {
    private final Table rules;
    private final Table bodies;
    private Table results;
    private Long transactionCount;
    private QueryNode treeOfQueries;
    private final Set<QueryNode> visitedQueries;

    public RulesRegistry(){
        this.rules = Table.create("rules");
        this.bodies = Table.create("bodies");
        this.transactionCount = 0L;
        this.visitedQueries = new HashSet<>();
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
//            tx.commit();  // This line was commented out in the original code
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
                this.results.doubleColumn("support").isGreaterThanOrEqualTo(minSupport)
                        .and(this.results.doubleColumn("confidence")
                                .isGreaterThanOrEqualTo(minConfidence)));
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

            headColumns.forEach(column -> builder.addHead(column, row.getString(column)));
            bodyColumns.forEach(column -> builder.addBody(column, row.getString(column)));

            return builder.build().toRecord();
        });
    }
}
