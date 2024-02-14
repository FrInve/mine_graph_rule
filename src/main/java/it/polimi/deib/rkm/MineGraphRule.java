package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.*;

public class MineGraphRule {

//    @Context
//    public Transaction tx;
    @Context
    public GraphDatabaseService db;

    @Procedure(name = "apoc.mgr.mineGraphRule", mode=Mode.READ)
    @Description("Graph Association Rule Mining for Neo4j")
    public Stream<AssociationRule.Record> mineGraphRule(
            // Input parameters here
            @Name("alias")      String alias,
            @Name("alias_node") String alias_node,
            @Name("item_head")   List<Map<String, Object>> item_head,
            @Name("item_body")   List<Map<String, Object>> item_body,
            @Name("support")    Number support,
            @Name("confidence") Number confidence
            ) {
        // Procedure logic here
        Query query = new Query(alias, alias_node, item_head, item_body, support.doubleValue(), confidence.doubleValue());
        // Create queries and retrieve data from Neo4j
        TransactionsRegistry tr = new TransactionsRegistry();
        // 1. Count number of transactions of alias_node
        Long transactions = tr.get_number_of_transactions(db, alias_node);

        // 2. Retrieve and count bodies
        tr.retrieveBodies(db, query.toCypherForBody());

        // 3. Retrieve and count rules (head and body)
        tr.retrieveRules(db, query.toCypherForRule());

        // 4. Join rules with bodies
        tr.combineRules();

        // 5. Compute confidence and support
        tr.computeMetrics(transactions);

        // Filter by support and confidence
        tr.filterBySupportAndConfidence(support.doubleValue(), confidence.doubleValue());

        // Fill one AssociationRule as mockup return value

//        List<Map<String, Object>> head = new ArrayList<>();
//        head.add(Map.of("type", "normal",
//                "rel_type", "BUY",
//                "rel_alias", "buy",
//                "end_node", "Book",
//                "end_node_alias", "b"));
//        return Stream.of(new AssociationRule(head, head, 0.5, 0.5)
//                .toRecord());
        return tr.getResults();
    }

}


