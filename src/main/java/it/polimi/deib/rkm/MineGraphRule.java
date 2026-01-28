package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.procedure.*;

public class MineGraphRule {

    @Context
    public GraphDatabaseService db;

    @Procedure(name = "apoc.mineGraphRule", mode=Mode.READ)
    @Description("Graph Association Rule Mining for Neo4j")
    public Stream<AssociationRule.Record> mineGraphRule(
            // Input parameters here
            @Name("anchor")      String anchor,
            @Name("anchorLabel") String anchorLabel,
            @Name("anchorWhereClause") String anchorWhereClause,
            @Name("serializedHead") List<Map<String, Object>> serializedHead,
            @Name("serializedBody") List<Map<String, Object>> serializedBody,
            @Name("where")       List<Map<String, String>> where,
            @Name("ignore")      List<String> ignore,
            @Name("support")    Number support,
            @Name("confidence") Number confidence,
            @Name("propertiesMap") Map<String, String> propertiesMap
            ) {
        Query query = new Query(
                anchor,
                anchorLabel,
                anchorWhereClause,
                serializedHead,
                serializedBody,
                where,
                ignore,
                support.doubleValue(),
                confidence.doubleValue(),
                propertiesMap);
        return runMining(query, support.doubleValue(), confidence.doubleValue());
    }

    @Procedure(name = "apoc.mineGraphRuleDsl", mode=Mode.READ)
    @Description("Graph Association Rule Mining for Neo4j using DSL")
    public Stream<AssociationRule.Record> mineGraphRuleDsl(
            @Name("dsl") String dsl,
            @Name("propertiesMap") Map<String, String> propertiesMap
    ) {
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        Query query = new Query(
                parsed.anchor,
                parsed.anchorLabel,
                parsed.anchorWhereClause,
                parsed.serializedHead,
                parsed.serializedBody,
                parsed.where,
                parsed.ignore,
                parsed.support,
                parsed.confidence,
                propertiesMap);
        return runMining(query, parsed.support, parsed.confidence);
    }

    private Stream<AssociationRule.Record> runMining(Query query, double support, double confidence) {
        // Create queries and retrieve data from Neo4j
        RulesRegistry rulesRegistry = new RulesRegistry();

        // Init RulesRegistry
        rulesRegistry.initTreeOfQueries(query);

        // 1. Count number of transactions of alias_node
        rulesRegistry.retrieveTransactionsCount(db, query);

        // 3. Retrieve and count rules (head and body)
        rulesRegistry.retrieveRules(db);

        // 2. Retrieve and count bodies
        rulesRegistry.retrieveBodies(db);

        rulesRegistry.removeTautologies();

        // 4. Join rules with bodies
        rulesRegistry.combineRules();

        // 5. Compute confidence and support
        rulesRegistry.computeMetrics();

        // Filter by support and confidence
        rulesRegistry.filterBySupportAndConfidence(support, confidence);

        return rulesRegistry.getResults();
    }
}


