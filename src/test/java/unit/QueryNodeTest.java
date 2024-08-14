package unit;

import it.polimi.deib.rkm.QueryNode;
import it.polimi.deib.rkm.Query;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


public class QueryNodeTest {

    Query generateQuery(){
        return new Query(
                "person",
                "Person",
                "anchorWhereClause",
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "count",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "headProduct",
                                "minValue", "2")))),
                List.of(Map.of("numMin", 1L,
                        "numMax", 3L,
                        "patternTail", List.of(Map.of("type", "count",
                                "relationshipType", "BUY",
                                "nodeLabel", "Product",
                                "nodeVariable", "bodyProduct",
                                "minValue", "2"), Map.of("type", "normal",
                                "relationshipType", "OF",
                                "nodeLabel", "Category",
                                "nodeVariable", "bodyCategory")))),
                List.of(Map.of("variable", "bodyProduct",
                        "variableProperty", "price",
                        "operand", "=",
                        "constantValue", "10"),
                        Map.of("variable", "headProduct",
                                "variableProperty", "price",
                                "operand", "=",
                                "constantValue", "10")),
                List.of("bodyProduct"),
                0.1,
                0.1
        );
    }

    //[{variable:"b", variableProperty:"price", operand:"<", otherVariable: "h", otherVariableProperty: "price"}]

    QueryNode generateQueryNode(){
        Query query = generateQuery();
        return new QueryNode(query);
    }

    @Test
    void testRuleQueryGeneration(){
        System.out.println("RULE\n");
        QueryNode queryNode = generateQueryNode();
        System.out.println(queryNode.getRuleInCypher((long) 100.00));
    }
    @Test
    void testBodyQueryGeneration(){
        System.out.println("BODY\n");
        QueryNode queryNode = generateQueryNode();
        System.out.println(queryNode.getBodyInCypher((long) 100.00));
    }
}
