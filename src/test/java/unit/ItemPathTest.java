//package unit;
//
//import it.polimi.deib.rkm.ItemPath;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class ItemPathTest {
//
//    @Test
//    void shouldGeneratePath(){
//
//        List<Map<String,String>> itemPathSerialized = List.of(
//                Map.of("type", "normal",
//                        "rel_type", "BUY",
//                        "rel_alias", "buy",
//                        "end_node", "Book",
//                        "end_node_alias", "b"),
//                Map.of("type", "normal",
//                        "rel_type", "OF",
//                        "rel_alias", "of",
//                        "end_node", "Genre",
//                        "end_node_alias", "g")
//        );
//
//        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
//        String actual =  ip.toCypher();
//        String expected = "-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldGenerateVariables(){
//        List<Map<String,String>> itemPathSerialized = List.of(
//                Map.of("type", "normal",
//                        "rel_type", "BUY",
//                        "rel_alias", "buy",
//                        "end_node", "Book",
//                        "end_node_alias", "b"),
//                Map.of("type", "normal",
//                        "rel_type", "OF",
//                        "rel_alias", "of",
//                        "end_node", "Genre",
//                        "end_node_alias", "g")
//        );
//
//        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
//        String actual =  ip.getStringVariable();
//        String expected = "b.id as head_BUY_Book, g.id as head_OF_Genre";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldGenerateAliasString(){
//
//        List<Map<String,String>> itemPathSerialized = List.of(
//                Map.of("type", "normal",
//                        "rel_type", "BUY",
//                        "rel_alias", "buy",
//                        "end_node", "Book",
//                        "end_node_alias", "b"),
//                Map.of("type", "normal",
//                        "rel_type", "OF",
//                        "rel_alias", "of",
//                        "end_node", "Genre",
//                        "end_node_alias", "g")
//        );
//
//        ItemPath ip = new ItemPath(itemPathSerialized, ItemPath.ItemType.HEAD);
//        String actual =  ip.getStringAlias();
//        String expected = "head_BUY_Book, head_OF_Genre";
//        assertThat(actual).isEqualTo(expected);
//
//    }
//}
