package unit;

import it.polimi.deib.rkm.DslParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DslParserTest {

    @Test
    void testExample1() {
        String dsl = "MINE GRAPH RULE example1 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item) " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals("example1", parsed.name);
        assertEquals("person", parsed.anchor);
        assertEquals("Person", parsed.anchorLabel);
        assertEquals(0.1, parsed.support);
        assertEquals(0.1, parsed.confidence);
        
        assertEquals(1, parsed.serializedBody.size());
        Map<String, Object> bodyPattern = parsed.serializedBody.get(0);
        assertEquals(1L, bodyPattern.get("numMin"));
        assertEquals(1L, bodyPattern.get("numMax"));
        List<Map<String, String>> bodyTail = (List<Map<String, String>>) bodyPattern.get("patternTail");
        assertEquals(1, bodyTail.size());
        assertEquals("BUY", bodyTail.get(0).get("relationshipType"));
        assertEquals("b", bodyTail.get(0).get("nodeVariable"));
        assertEquals("Item", bodyTail.get(0).get("nodeLabel"));
    }

    @Test
    void testExample2() {
        String dsl = "MINE GRAPH RULE example2 " +
                "GROUPING ON (person:Person) WHERE person.city = 'Rome' " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item) " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals("person.city = 'Rome'", parsed.anchorWhereClause);
    }

    @Test
    void testExample3() {
        String dsl = "MINE GRAPH RULE example3 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS 1..1 (person)-[:BUY]->(b1:Item), (person)-[:FOLLOW]->(b2:Person) " +
                "HEAD AS 1..1 (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals(2, parsed.serializedBody.size());
        assertEquals("FOLLOW", ((List<Map<String, String>>)parsed.serializedBody.get(1).get("patternTail")).get(0).get("relationshipType"));
    }

    @Test
    void testExample4() {
        String dsl = "MINE GRAPH RULE example4 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item)-[:OF]->(cb:Category) " +
                "HEAD AS (person)-[:BUY]->(h:Item)-[:OF]->(ch:Category) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        List<Map<String, String>> bodyTail = (List<Map<String, String>>) parsed.serializedBody.get(0).get("patternTail");
        assertEquals(2, bodyTail.size());
        assertEquals("BUY", bodyTail.get(0).get("relationshipType"));
        assertEquals("OF", bodyTail.get(1).get("relationshipType"));
        assertEquals("cb", bodyTail.get(1).get("nodeVariable"));
    }

    @Test
    void testExample5() {
        String dsl = "MINE GRAPH RULE example5 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item)-[:OF]->(cb:Category) " +
                "HEAD AS (person)-[:BUY]->(h:Item)-[:OF]->(ch:Category) " +
                "IGNORE h, b " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals(List.of("h", "b"), parsed.ignore);
    }

    @Test
    void testExample6() {
        String dsl = "MINE GRAPH RULE example6 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[]->{1,1}(b:Item) " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        List<Map<String, String>> bodyTail = (List<Map<String, String>>) parsed.serializedBody.get(0).get("patternTail");
        assertEquals("any", bodyTail.get(0).get("type"));
        assertEquals("1", bodyTail.get(0).get("relationshipLength"));
    }

    @Test
    void testExample7() {
        String dsl = "MINE GRAPH RULE example7 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY>=2]->(b:Item) " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        List<Map<String, String>> bodyTail = (List<Map<String, String>>) parsed.serializedBody.get(0).get("patternTail");
        assertEquals("count", bodyTail.get(0).get("type"));
        assertEquals("2", bodyTail.get(0).get("minValue"));
    }

    @Test
    void testExample8() {
        String dsl = "MINE GRAPH RULE example8 " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item) " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "WHERE h.price > 40 AND b.price < h.price " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";
        
        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals(2, parsed.where.size());
        
        Map<String, String> w1 = parsed.where.get(0);
        assertEquals("h", w1.get("variable"));
        assertEquals("price", w1.get("variableProperty"));
        assertEquals(">", w1.get("operand"));
        assertEquals("40", w1.get("constantValue"));

        Map<String, String> w2 = parsed.where.get(1);
        assertEquals("b", w2.get("variable"));
        assertEquals("price", w2.get("variableProperty"));
        assertEquals("<", w2.get("operand"));
        assertEquals("h", w2.get("otherVariable"));
        assertEquals("price", w2.get("otherVariableProperty"));
    }

    @Test
    void testPatternAlias() {
        String dsl = "MINE GRAPH RULE aliasTest " +
                "GROUPING ON (person:Person) " +
                "DEFINING BODY AS (person)-[:BUY]->(b:Item) AS myBuyPattern " +
                "HEAD AS (person)-[:BUY]->(h:Item) " +
                "EXTRACTING RULES WITH SUPPORT > 0.1 AND CONFIDENCE > 0.1";

        DslParser.ParsedDsl parsed = DslParser.parse(dsl);
        assertEquals(1, parsed.serializedBody.size());
        Map<String, Object> bodyPattern = parsed.serializedBody.get(0);
        assertEquals("myBuyPattern", bodyPattern.get("patternAlias"));
    }
}