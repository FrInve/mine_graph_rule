package unit;

import it.polimi.deib.rkm.Item;
import it.polimi.deib.rkm.ItemPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemTest {
    @Test
    void shouldGenerateQueryMatch(){
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
        Map<String, Object> itemSerialized = Map.of("num_min", 1L,
                "num_max", 1L,
                "item_path", itemPathSerialized);

        Item i = new Item(itemSerialized, ItemPath.ItemType.HEAD);
        String actual =  i.toCypherMatch(0);
        String expected = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateCombinationQueryMatch(){
        List<Map<String, String>> itemPathSerialized = List.of(
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
        Map<String, Object> itemSerialized = Map.of("num_min", 1L,
                "num_max", 3L,
                "item_path", itemPathSerialized);

        Item i = new Item(itemSerialized, ItemPath.ItemType.BODY);
        String actual =  i.toCypherMatch(2);
        String expected = "MATCH (alias)-[buy2:BUY]-(b2:Book)-[of2:OF]-(g2:Genre)";
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    void shouldGenerateSingleQueryReturn(){

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
        Map<String, Object> itemSerialized = Map.of("num_min", 1L,
                "num_max", 1L,
                "item_path", itemPathSerialized);

        Item i = new Item(itemSerialized, ItemPath.ItemType.BODY);
        String actualEmpty =  i.toCypherReturn("");
//        String expectedEmpty = """
//            MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//            RETURN size(collect(DISTINCT alias)) as suppcount, b.id as head_BUY_Book, g.id as head_OF_Genre""";
        String expectedEmpty = """
                        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
                        RETURN size(collect(DISTINCT alias)) as suppcount, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id])) as body_OF_Genre""";
                assertThat(actualEmpty).isEqualTo(expectedEmpty);

                String actual=  i.toCypherReturn("head_BUY_Book");
//                String expected = """
//        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
//        RETURN size(collect(DISTINCT alias)) as suppcount, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre""";
        String expected = """
                      MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
                      RETURN size(collect(DISTINCT alias)) as suppcount, head_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id])) as body_OF_Genre""";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateCombinationQueryReturn(){

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
        Map<String, Object> itemSerialized = Map.of("num_min", 2L,
                "num_max", 3L,
                "item_path", itemPathSerialized);

        Item i = new Item(itemSerialized, ItemPath.ItemType.BODY);
        String actualEmpty =  i.toCypherReturn("");
        String expectedEmpty = """
              MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
              MATCH (alias)-[buy1:BUY]-(b1:Book)-[of1:OF]-(g1:Genre)
              RETURN size(collect(DISTINCT alias)) as suppcount, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id, b1.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id, g1.id])) as body_OF_Genre
              UNION
              MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
              MATCH (alias)-[buy1:BUY]-(b1:Book)-[of1:OF]-(g1:Genre)
              MATCH (alias)-[buy2:BUY]-(b2:Book)-[of2:OF]-(g2:Genre)
              RETURN size(collect(DISTINCT alias)) as suppcount, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id, b1.id, b2.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id, g1.id, g2.id])) as body_OF_Genre""";
        assertThat(actualEmpty).isEqualTo(expectedEmpty);

        String actual=  i.toCypherReturn("head_BUY_Book");
        String expected = """
              MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
              MATCH (alias)-[buy1:BUY]-(b1:Book)-[of1:OF]-(g1:Genre)
              RETURN size(collect(DISTINCT alias)) as suppcount, head_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id, b1.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id, g1.id])) as body_OF_Genre
              UNION
              MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
              MATCH (alias)-[buy1:BUY]-(b1:Book)-[of1:OF]-(g1:Genre)
              MATCH (alias)-[buy2:BUY]-(b2:Book)-[of2:OF]-(g2:Genre)
              RETURN size(collect(DISTINCT alias)) as suppcount, head_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([b.id, b1.id, b2.id])) as body_BUY_Book, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([g.id, g1.id, g2.id])) as body_OF_Genre""";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateQueryWith(){
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
        Map<String, Object> itemSerialized = Map.of("num_min", 1L,
                "num_max", 1L,
                "item_path", itemPathSerialized);
        Item i = new Item(itemSerialized, ItemPath.ItemType.HEAD);
        String actualEmpty =  i.toCypherWith("", "p", "Person");
        String expectedEmpty = """
        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
        WITH alias, b.id as head_BUY_Book, g.id as head_OF_Genre""";
//        assertThat(actualEmpty).isEqualTo(expectedEmpty);
        String actual=  i.toCypherWith("body_BUY_Book", "p", "Person");
        String expected = """
        MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)
        WITH alias, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre""";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateCombinationQueryWith(){

    }



}
