package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.procedure.*;
import tech.tablesaw.api.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TransactionsRegistry {

    private Table tableBody;
    private Table tableRule;
    private Table tableResults;



    public TransactionsRegistry(){
        this.tableBody = Table.create("body");
        this.tableRule = Table.create("rules");
    }

    public void retrieveBodies(GraphDatabaseService db, String query){
        // Retrieve bodies from db and store them somewhere
        ArrayList<Map<String, Object>> bodies = new ArrayList<>();
        boolean setColumns = false;
        try(Transaction tx = db.beginTx()){
            try( Result result = tx.execute(query)) {
                while (result.hasNext()) { // Iterate over the result rows

                    if(!setColumns){ // Set columns only once
                        for (String key : result.columns()) {
                            if(key.equals("suppcount")){
                                tableBody.addColumns(LongColumn.create(key));
                            } else {
                                tableBody.addColumns(StringColumn.create(key));
                            }
                        }
                        setColumns = true;
                    }

                    Map<String, Object> row = result.next();
                    HashMap<String, Object> materializedRow = new HashMap<>();

                    for (String key : result.columns()) { // Iterate over the columns of the row
                        materializedRow.put(key, row.get(key));
                    }
                    bodies.add(materializedRow);
                }
            }
        }
        if(!bodies.isEmpty()) {
            for (Map<String, Object> body : bodies) {
                Row row = tableBody.appendRow();
                for (String key : body.keySet()) {
                    if(key.equals("suppcount")){
                        row.setLong(key, (Long) body.get(key));
                    } else {
                        row.setString(key, (String) body.get(key));
                    }
                }
            }
        }
    }



    public void retrieveRules(GraphDatabaseService db, String query){
        // Retrieve rules from db and store them somewhere
        ArrayList<Map<String, Object>> rules = new ArrayList<>();
        boolean setColumns = false;
        try(Transaction tx = db.beginTx()){
            try( Result result = tx.execute(query)) {
                while (result.hasNext()) { // Iterate over the result rows

                    if(!setColumns){ // Set columns only once
                        for (String key : result.columns()) {
                            if(key.equals("suppcount")){
                                tableRule.addColumns(LongColumn.create(key));
                            } else {
                                tableRule.addColumns(StringColumn.create(key));
                            }
                        }
                        setColumns = true;
                    }

                    Map<String, Object> row = result.next();
                    HashMap<String, Object> materializedRow = new HashMap<>();

                    for (String key : result.columns()) { // Iterate over the columns of the row
                        materializedRow.put(key, row.get(key));
                    }
                    rules.add(materializedRow);
                }
            }
        }
        if(!rules.isEmpty()) {
            for (Map<String, Object> body : rules) {
                Row row = tableRule.appendRow();
                for (String key : body.keySet()) {
                    if(key.equals("suppcount")){
                        row.setLong(key, (Long) body.get(key));
                    } else {
                        row.setString(key, (String) body.get(key));
                    }
                }
            }
        }
    }

    public void combineRules(){
        // Combine rules and bodies
        // Store the results in tableResults
        String bodyColumn = "";
        for(String key : this.tableBody.columnNames()){
            if(key.contains("body")){
                bodyColumn = key;
                break;
            }
        }
        this.tableResults =
        this.tableResults = this.tableRule.joinOn(bodyColumn).inner(true, this.tableBody);
    }

    public void computeMetrics(Long totalTransactions){
        // Compute confidence and support
        // Store the results in tableResults
        // support = suppcount_rule / totalTransactions
        // confidence = suppcount_rule / suppcount_body
        this.tableResults.addColumns(
                this.tableResults.longColumn("suppcount")
                        .asDoubleColumn()
                        .divide(totalTransactions)
                        .setName("support"),
                this.tableResults.longColumn("suppcount")
                        .asDoubleColumn()
                        .divide(this.tableResults.longColumn("T2.suppcount").asDoubleColumn())
                        .setName("confidence_raw"));
        // Apply ceiling of one to confidence column
        this.tableResults.addColumns(
                this.tableResults.doubleColumn("confidence_raw")
                        .map((value) -> value > 1.0 ? 1.0 : value)
                        .setName("confidence"));
        // Drop confidence_raw column
        this.tableResults = this.tableResults.removeColumns("confidence_raw");
    }

    public void filterBySupportAndConfidence(double minSupport, double minConfidence){
            // Filter by support and confidence
            // Store the results in tableResults
            this.tableResults = this.tableResults.where(
                    this.tableResults.doubleColumn("support").isGreaterThanOrEqualTo(minSupport)
                            .and(this.tableResults.doubleColumn("confidence")
                                    .isGreaterThanOrEqualTo(minConfidence)));
        }

    public Stream<AssociationRule.AssociationRuleRecord> getResults(){
        return this.tableResults.stream().map(row->
            new AssociationRule.AssociationRuleRecord(
                    row.getString(1),
                    row.getString(2),
                    row.getDouble("support"),
                    row.getDouble("confidence"),
                    List.of(row.columnNames().get(1), row.columnNames().get(2)))
        );
    }

//    @Context
//    public GraphDatabaseService db;
//
//    @Procedure(name="rkm.asm.getNumberOfTransactions", mode=Mode.READ)
//    @Description("CALL rkm.asm.getNumberOfTransactions(alias_node)")
//    public Stream<TransactionsCount> getNumberOfTransactions(
//            @Name("alias_node") String alias_node
//    ){
//        TransactionsCount transactionsCount = new TransactionsCount(this.get_number_of_transactions(db, alias_node));
//        return Stream.of(transactionsCount);
//    }
//
//    public static class TransactionsCount {
//        public Long count;
//        public TransactionsCount(Long count){
//            this.count = count;
//        }
//    }


    // Internal method
    Long get_number_of_transactions(GraphDatabaseService db, String alias_node){
        Long no_of_transactions = 0L;
        try(Transaction tx = db.beginTx()){

            Result result = tx.execute("MATCH (n:" + alias_node + ") RETURN count(n) AS count");
            if(result.hasNext()) {
                no_of_transactions = (Long) result.next().get("count");
                tx.commit();
            }
        }
        return no_of_transactions;
    }
}
