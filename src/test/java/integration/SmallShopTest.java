package integration;

import it.polimi.deib.rkm.MineGraphRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Value;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SmallShopTest {

    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() throws IOException {

        var sw = new StringWriter();
        try (var in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/small_shop.cypher")))) {
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
    void countAllNodesAndRelationships() {

        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
        ) {
            // language=cypher
            var no_of_people = session.run("""
                match (n) return count(n) as people
                """)
                    .stream()
                    .map(r -> r.get("people"))
                    .map(Value::asInt)
                    .toList();

            // language=cypher
            var no_of_relations = session.run("""
                match ()-[r]->()
                return count(r) as relations
                """)
                    .stream()
                    .map(r -> r.get("relations"))
                    .map(Value::asInt)
                    .toList();

            assertThat(no_of_people.get(0)).isEqualTo(60);
            assertThat(no_of_relations.get(0)).isEqualTo(100);

        }
    }
}