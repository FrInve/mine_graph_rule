package unit;

import it.polimi.deib.rkm.ItemPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemPathTest {

    @Test
    void shouldGeneratePath(){

        List<Map<String,String>> itemPathSerialized = List.of(
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "normal",
                        "rel_type", "OF",
                        "rel_alias", "of",
                        "end_node", "Genre",
                        "end_node_alias", "g")
        );

        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
        String actual =  ip.toCypher();
        String expected = "-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateVariables(){
        List<Map<String,String>> itemPathSerialized = List.of(
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "normal",
                        "rel_type", "OF",
                        "rel_alias", "of",
                        "end_node", "Genre",
                        "end_node_alias", "g")
        );

        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
        String actual =  ip.getStringVariable(1);
//        String expected = "b.id as head_BUY_Book, g.id as head_OF_Genre";
        String expected = "apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b])) as head_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g])) as head_OF_Genre";
        assertThat(actual).isEqualTo(expected);

        actual = ip.getStringVariable(2);
        expected = "apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b, b1])) as head_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g, g1])) as head_OF_Genre";
    }

    @Test
    void shouldGenerateAliasString(){

        List<Map<String,String>> itemPathSerialized = List.of(
                Map.of("type", "normal",
                        "rel_type", "BUY",
                        "rel_alias", "buy",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "normal",
                        "rel_type", "OF",
                        "rel_alias", "of",
                        "end_node", "Genre",
                        "end_node_alias", "g")
        );

        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
        String actual =  ip.getStringAlias();
        String expected = "head_BUY_Book, head_OF_Genre";
        assertThat(actual).isEqualTo(expected);

    }
}
