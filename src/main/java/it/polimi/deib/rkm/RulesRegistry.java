package it.polimi.deib.rkm;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.util.HashSet;
import java.util.Set;

public class RulesRegistry {
    private Table rules;
    private Table bodies;
    private Table results;
    private Long transactionCount;
    private QueryNode treeOfQueries;
    private Set<QueryNode> visitedQueries;

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

            Result result = tx.execute("MATCH (" + query.getAnchor() + ":" + query.getAnchorLabel() + ") "
                    + query.getAnchorWhereClause()
                    + " RETURN count("+ query.getAnchor() +") AS count");
            if(result.hasNext()) {
                transactionCount = (Long) result.next().get("count");
                tx.commit();
            }
        }
    }

    public void retrieveBodies(GraphDatabaseService db){

    }
    public void retrieveRules(GraphDatabaseService db) {
        retrieveRulesHelper(db, treeOfQueries);
    }

    private void retrieveRulesHelper(GraphDatabaseService db, QueryNode queryNode){
//        try(Transaction tx = db.beginTx()){
//            Result result = tx.execute(queryNode.getRuleInCypher());
//            while(result.hasNext()){
//                // TODO: Add rule to rules table
//            }
//            tx.commit();
//        }
        System.out.println(queryNode.getRuleInCypher());
        System.out.println();

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

}
