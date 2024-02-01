package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.procedure.*;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TransactionsRegistry {

    private Table tableBody;
    private Table tableRule;



    public TransactionsRegistry(){
        this.tableBody = Table.create("body").addColumns(
                StringColumn.create("BodyItem0"),
                LongColumn.create("SuppCountBody")
        );
        this.tableRule = Table.create("rules").addColumns(
                StringColumn.create("HeadItem0"),
                StringColumn.create("BodyItem0"),
                LongColumn.create("SuppCountRule"),
                DoubleColumn.create("Supp")
        );
    }

    private void retrieveBodies(GraphDatabaseService db, String query){
        // Retrieve bodies from db and store them somewhere
        ArrayList<Map<String, Object>> bodies = new ArrayList<>();
        try(Transaction tx = db.beginTx()){
            try( Result result = tx.execute(query)) {
                while (result.hasNext()) {
                    Map<String, Object> row = result.next();
                    HashMap<String, Object> materializedRow = new HashMap<>();
                    for (String key : result.columns()) {
                        materializedRow.put(key, row.get(key));
                    }
                    bodies.add(materializedRow);
                }
            }
        }
 //       if(bodies.size()>0) {
        //           for (String key : bodies.get(0)) {
//
        //           }
        //     }
    }



    private void retrieveRules(GraphDatabaseService db, ItemSet itemBody, ItemSet itemHead){

    }

    @Context
    public GraphDatabaseService db;

    @Procedure(name="rkm.asm.getNumberOfTransactions", mode=Mode.READ)
    @Description("CALL rkm.asm.getNumberOfTransactions(alias_node)")
    public Stream<TransactionsCount> getNumberOfTransactions(
            @Name("alias_node") String alias_node
    ){
        TransactionsCount transactionsCount = new TransactionsCount(this.get_number_of_transactions(db, alias_node));
        return Stream.of(transactionsCount);
    }

    public static class TransactionsCount {
        public Long count;
        public TransactionsCount(Long count){
            this.count = count;
        }
    }


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
