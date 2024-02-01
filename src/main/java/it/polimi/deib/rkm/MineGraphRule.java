package it.polimi.deib.rkm;

import java.util.ArrayList;
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

    @Procedure(name = "rkm.mineGraphRule", mode=Mode.READ)
    @Description("Graph Association Rule Mining for Neo4j")
    public Stream<AssociationRule.AssociationRuleRecord> mineGraphRule(
            // Input parameters here
            @Name("alias")      String alias,
            @Name("alias_node") String alias_node,
            @Name("item_head")   List<Map<String, Object>> item_head,
            @Name("item_body")   List<String> item_body,
            @Name("support")    Number support,
            @Name("confidence") Number confidence
            ) {
        // Procedure logic here

        // Create queries and retrieve data from Neo4j
        TransactionsRegistry tr = new TransactionsRegistry();
        // 1. Count number of transactions of alias_node
        Long transactions = tr.get_number_of_transactions(db, alias_node);

        // 2. Count item-sets head
//        Item itemhead = new Item((Map<String, Object>) item_head);

        // 3. Count item-sets body

        // 4. Compute confidence

        // 5. Compute support

        // Fill one AssociationRule as mockup return value
        ArrayList<String> foo = new ArrayList<>();
        foo.add("a");
        ArrayList<String> bar = new ArrayList<>();
        foo.add("b");
        return Stream.of(new AssociationRule(foo, bar, 0.5, 0.5)
                .toRecord());
    }

}


