//package unit;
//
//import it.polimi.deib.rkm.Item;
//import it.polimi.deib.rkm.ItemPath;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//public class ItemTest {
//    /**
//     * Create a single path item -[buy:BUY]-(b:Book)-[of:OF]-(g:Genre) with numMin and numMax cardinality
//     * @param numMin minimum number of occurrences of the path
//     * @param numMax maximum number of occurrences of the path
//     * @return an @Item object like -[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//     */
//    Item itemSinglePath(int numMin, int numMax, ItemPath.ItemType mode){
//        return new Item(
//            Map.of("num_min", (long) numMin,
//                    "num_max", (long) numMax,
//                    "item_path", List.of(
//                            Map.of("type", "normal",
//                                    "rel_type", "BUY",
//                                    "rel_alias", "buy",
//                                    "end_node", "Book",
//                                    "end_node_alias", "b"),
//                            Map.of("type", "normal",
//                                    "rel_type", "OF",
//                                    "rel_alias", "of",
//                                    "end_node", "Genre",
//                                    "end_node_alias", "g")
//                    )
//            ), mode);
//    };
//
//    @Test
//    void shouldGenerateQueryMatch(){
//        Item i = itemSinglePath(1, 1, ItemPath.ItemType.HEAD);
//        String actual =  i.toCypherMatch();
//        String expected = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
//        assertThat(actual).isEqualTo(expected);
//    }
//    @Test
//    void shouldGenerateQueryReturn(){
//
//        Item i = itemSinglePath(1, 1, ItemPath.ItemType.HEAD);
//        String actualEmpty =  i.toCypherReturn("");
//        String expectedEmpty = """
//            MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//            RETURN size(collect(DISTINCT alias)) as suppcount, b.id as head_BUY_Book, g.id as head_OF_Genre""";
//        assertThat(actualEmpty).isEqualTo(expectedEmpty);
//
//        String actual=  i.toCypherReturn("body_BUY_Book");
//        String expected = """
//        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//        RETURN size(collect(DISTINCT alias)) as suppcount, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre""";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldGenerateQueryWith(){
//        Item i = itemSinglePath(1, 1, ItemPath.ItemType.HEAD);
//        String actualEmpty =  i.toCypherWith("");
//        String expectedEmpty = """
//        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//        WITH alias, b.id as head_BUY_Book, g.id as head_OF_Genre""";
//        assertThat(actualEmpty).isEqualTo(expectedEmpty);
//        String actual=  i.toCypherWith("body_BUY_Book");
//        String expected = """
//        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//        WITH alias, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre""";
//        assertThat(actual).isEqualTo(expected);
//    }
//
//    @Test
//    void shouldGenerateCombinationQueryMatch(){
//        List<Map<String, String>> itemPathSerialized = List.of(
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
//        Map<String, Object> itemSerialized = Map.of("num_min", 1L,
//        "num_max", 3L,
//        "item_path", itemPathSerialized);
//    }
//
//    @Test
//    void shouldGenerateCombinationQueryReturn(){
//    }
//
//}
