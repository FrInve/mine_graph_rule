package unit;

import it.polimi.deib.rkm.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryTest {

    @Test
    void shouldGenerateBody(){
        List<Map<String,String>> itemPathBodySerialized = List.of(      // Set path of body
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "normal",
                        "rel_type", "OF",
                        "rel_alias", "of",
                        "end_node", "Genre",
                        "end_node_alias", "g"));

        List<Map<String, Object>> itemSetBodySerialized = List.of(      // Set body (only one item)
                Map.of("num_min", 1L,
                        "num_max", 1L,
                        "item_path", itemPathBodySerialized));

        List<Map<String,String>> itemPathHeadSerialized = List.of(      // Set path of head
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"));

        List<Map<String, Object>> itemSetHeadSerialized = List.of(      // Set head (only one item)
                Map.of("num_min", 1L,
                        "num_max", 1L,
                        "item_path", itemPathHeadSerialized));

        Query query = new Query("P",
                "Person",
                itemSetHeadSerialized,
                itemSetBodySerialized,
                0.5,
                0.5);

        String actual = query.toCypherForBody();
        String expected = """
        MATCH (n:Person)
        WITH n as alias
        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
        RETURN size(collect(DISTINCT alias)) as suppcount, b.id as body_BUY_Book, g.id as body_OF_Genre""";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateRule(){
        List<Map<String,String>> itemPathBodySerialized = List.of(      // Set path of body
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "normal",
                        "rel_type", "OF",
                        "rel_alias", "of",
                        "end_node", "Genre",
                        "end_node_alias", "g"));

        List<Map<String, Object>> itemSetBodySerialized = List.of(      // Set body (only one item)
                Map.of("num_min", 1L,
                        "num_max", 1L,
                        "item_path", itemPathBodySerialized)
        );

        List<Map<String,String>> itemPathHeadSerialized = List.of(      // Set path of head
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"));

        List<Map<String, Object>> itemSetHeadSerialized = List.of(      // Set head (only one item)
                Map.of("num_min", 1L,
                        "num_max", 1L,
                        "item_path", itemPathHeadSerialized));

        Query query = new Query("P",
                "Person",
                itemSetHeadSerialized,
                itemSetBodySerialized,
                0.5,
                0.5);

        String actual = query.toCypherForRule();
        String expected = """
        MATCH (n:Person)
        WITH n as alias
        MATCH (alias)-[buy:BUY]-(b:Book)
        WITH alias, b.id as head_BUY_Book
        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
        RETURN size(collect(DISTINCT alias)) as suppcount, head_BUY_Book, b.id as body_BUY_Book, g.id as body_OF_Genre""";
        assertThat(actual).isEqualTo(expected);
    }
}
