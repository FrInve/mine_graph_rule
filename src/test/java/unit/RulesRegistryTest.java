package unit;

import it.polimi.deib.rkm.Query;
import it.polimi.deib.rkm.RulesRegistry;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RulesRegistryTest {


    Query generateQuery(){
        return new Query(
                "person",
                "Person",
                "anchorWhereClause",
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "normal",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "headProduct")))),
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "normal",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "bodyProduct")))),
                List.of(),
                List.of(),
                0.1,
                0.1
        );
    }

    /**
     * Generates a complete query
     * MINE GRAPH RULE completeQuery
     * GROUPING ON (person:Person)
     * DEFINING head as <1..3> person-[:BUY]-(headProduct:Product) AND <1..1> person-[:FROM]-(city:City)
     * DEFINING body as <1..3> person-[:BUY]-(bodyProduct:Product)-[:OF]-(bodyCategory:Category)
     * WHERE anchorWhereClause
     * EXTRACTING RULES WITH SUPPORT 0.1, CONFIDENCE 0.1
     * This query generates 9 queries.
     */
    Query generateCompleteQuery(){
        return new Query(
                "person",
                "Person",
                "anchorWhereClause",
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "normal",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "headProduct"))),
                        Map.of("numMin", 1L,
                                "numMax", 1L,
                                "patternTail", List.of(Map.of("type", "normal",
                                        "relationshipType", "FROM",
                                        "nodeLabel", "City",
                                        "nodeVariable", "city")))),
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "normal",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "bodyProduct"),
                                Map.of("type", "normal",
                                        "relationshipType", "OF",
                                        "nodeLabel", "Category",
                                        "nodeVariable", "bodyCategory")))),
                List.of(),
                List.of(),
                0.1,
                0.1
        );
    }

    @Test
    @Disabled
    void shouldGenerateQueries(){
        Query query = generateQuery();
        RulesRegistry rulesRegistry = new RulesRegistry();
        rulesRegistry.initTreeOfQueries(query);
        rulesRegistry.retrieveRules(null);
    }

    @Test
    @Disabled
    void shouldGenerateComplexRules(){
        Query query = generateCompleteQuery();
        RulesRegistry rulesRegistry = new RulesRegistry();
        rulesRegistry.initTreeOfQueries(query);
        rulesRegistry.retrieveRules(null);
    }

    @Test
    @Disabled
    void shouldGenerateComplexBodies(){
        Query query = generateCompleteQuery();
        RulesRegistry rulesRegistry = new RulesRegistry();
        rulesRegistry.initTreeOfQueries(query);
        rulesRegistry.retrieveBodies(null);
    }
}
