package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.procedure.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class TransactionsRegistry {

    @Context
    public GraphDatabaseService db;

    @Procedure(name="rkm.asm.getNumberOfTransactions", mode=Mode.READ)
    @Description("CALL rkm.asm.getNumberOfTransactions(alias_node)")
    public Stream<Output> getNumberOfTransactions(
            @Name("alias_node") String alias_node
    ){
        Output output = new Output(this.get_number_of_transactions(db, alias_node));
        return Stream.of(output);
    }

    public static class Output{
        public Long out;
        public Output(Long output){
            this.out = output;
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
