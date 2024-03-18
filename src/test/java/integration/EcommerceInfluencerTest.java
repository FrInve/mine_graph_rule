package integration;

import it.polimi.deib.rkm.MineGraphRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EcommerceInfluencerTest {

    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() throws IOException {

        var sw = new StringWriter();
        try (var in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/ecommerce_influencer.cypher")))) {
            in.transferTo(sw);
            sw.flush();
        }

        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withProcedure(MineGraphRule.class)
                .withFixture(sw.toString())
                .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }

    @Test
    void shouldMineSimpleAssociationRules(){
        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
        ) {
            /*
            MINE GRAPH RULE SimpleAssociationRules
            GROUPING ON person AS (:Person)
            DEFINING body AS 1...1 (person)-[:Buy]-(Item)
                     head AS 1...1 (person)-[:Buy]-(Item)
            EXTRACTING RULES WITH SUPPORT: 0.1, CONFIDENCE: 0.1
             */
            // language=cypher
            var rules = session.run("""
//                    CALL apoc.mgr.mineGraphRule("P", "Person","", [{num_min:1, num_max:1, item_path:[{type: "normal", rel_type: "Buy", rel_alias:"buy", end_node: "Item", end_node_alias:"b"}]}], [{num_min:1, num_max:1, item_path:[{type: "normal", rel_type: "Buy", rel_alias:"buy", end_node: "Item", end_node_alias:"b"}]}], 0.1, 0.1)
                    CALL apoc.mgr.mineGraphRule("P", "Person","", [{numMin:1, numMax:1, patternTail:[{type: "normal", relationshipType: "Buy", nodeLabel: "Item", nodeVariable:"h"}]}], [{numMin:1, numMax:1, patternTail:[{type: "normal", relationshipType: "Buy", nodeLabel: "Item", nodeVariable:"b"}]}], 0.1, 0.1)
                    """)
                    .stream().toList();

            rules = rules.stream().filter(r -> !r.get("head").get("head0_Buy_Item").equals(r.get("body").get("Buy_Item"))).toList();
            assertThat(rules).isNotEmpty();
            assertThat(rules.size()).isEqualTo(8);
        }
    }
    @Test
    void shouldMineHigherCardinalityAssociationRules(){
        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
        ) {
            /*
            MINE GRAPH RULE SimpleAssociationRules
            GROUPING ON person AS (:Person)
            DEFINING body AS 1...3 (person)-[:Buy]-(Item)
                     head AS 1...1 (person)-[:Buy]-(Item)
            EXTRACTING RULES WITH SUPPORT: 0.1, CONFIDENCE: 0.1
             */
            // language=cypher
            var rules = session.run("""
//                    CALL apoc.mgr.mineGraphRule("P", "Person","", [{num_min:1, num_max:1, item_path:[{type: "normal", rel_type: "Buy", rel_alias:"buy", end_node: "Item", end_node_alias:"b"}]}], [{num_min:1, num_max:1, item_path:[{type: "normal", rel_type: "Buy", rel_alias:"buy", end_node: "Item", end_node_alias:"b"}]}], 0.1, 0.1)
                    CALL apoc.mgr.mineGraphRule("P", "Person","", [{numMin:1, numMax:1, patternTail:[{type: "normal", relationshipType: "Buy", nodeLabel: "Item", nodeVariable:"h"}]}], [{numMin:1, numMax:3, patternTail:[{type: "normal", relationshipType: "Buy", nodeLabel: "Item", nodeVariable:"b"}]}], 0.1, 0.1)
                    """)
                    .stream().toList();

            rules = rules.stream().filter(r -> !r.get("head").get("head0_Buy_Item").equals(r.get("body").get("Buy_Item"))).toList();
            assertThat(rules).isNotEmpty();
            assertThat(rules.size()).isGreaterThan(8);
        }
    }
}