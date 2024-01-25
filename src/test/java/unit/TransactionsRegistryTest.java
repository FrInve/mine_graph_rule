package unit;


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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.neo4j.driver.Values.parameters;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionsRegistryTest {
    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() throws IOException {

        var sw = new StringWriter();
        try (var in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/small_shop.cypher")))) {
            in.transferTo(sw);
            sw.flush();
        }

        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                .withProcedure(it.polimi.deib.rkm.TransactionsRegistry.class)
                .withFixture(sw.toString())
                .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }

    @Test
    void shouldCountTransactionsArticle(){

        Long expected = 40L;

        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
                ){
            var numberOfArticles = session.run("""
                    CALL rkm.asm.getNumberOfTransactions($alias_node)
                    """, parameters("alias_node", "Article"))
                    .stream()
                    .map(r -> r.get("count"))
                    .map(Value::asLong)
                    .toList();

            var actual = numberOfArticles.get(0);

            assertThat(actual).isEqualTo(expected);
        }

    }
}