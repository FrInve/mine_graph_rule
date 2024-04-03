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
                Map.of("type", "reverse",
                        "rel_type", "WRITE",
                        "rel_alias", "write",
                        "end_node", "Author",
                        "end_node_alias", "a")
        );

        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
        String actual =  ip.toCypher();
        String expected = "-[buy:BUY]->(b:Book)<-[write:WRITE]-(a:Author)";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGeneratePathAny(){

        List<Map<String,String>> itemPathSerialized = List.of(
                Map.of("type", "any",
                        "rel_type", "ANY",
                        "rel_alias", "any",
                        "rel_len", "2",
                        "end_node", "Book",
                        "end_node_alias", "b"),
                Map.of("type", "anyReverse",
                        "rel_type", "ANY",
                        "rel_alias", "any",
                        "rel_len", "3",
                        "end_node", "Genre",
                        "end_node_alias", "g")
        );

        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
        String actual =  ip.toCypher();
        String expected = "-[*2]->(b:Book)<-[*3]-(g:Genre)";
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
        String actual =  ip.getStringVariable();
        String expected = "b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actual).isEqualTo(expected);
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
