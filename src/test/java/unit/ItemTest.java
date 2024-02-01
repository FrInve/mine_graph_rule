package unit;

import it.polimi.deib.rkm.Item;
import it.polimi.deib.rkm.ItemPath;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemTest {
    @Test
    void shouldGenerateQueryMatch(){

        ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
        HashMap<String, String> itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "BUY");
        itemMap.put("rel_alias", "buy");
        itemMap.put("end_node", "Book");
        itemMap.put("end_node_alias", "b");
        itemList.add(itemMap);
        itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "OF");
        itemMap.put("rel_alias", "of");
        itemMap.put("end_node", "Genre");
        itemMap.put("end_node_alias", "g");
        itemList.add(itemMap);
        HashMap<String, Object> itemSetMap = new HashMap<>();
        itemSetMap.put("num_min", 1);
        itemSetMap.put("num_max", 1);
        itemSetMap.put("item_path", itemList);

        Item i = new Item(itemSetMap, ItemPath.ItemType.HEAD);
        String actual =  i.toCypherMatch();
        String expected = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
        assertThat(actual).isEqualTo(expected);
    }
    @Test
    void shouldGenerateQueryReturn(){

        ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
        HashMap<String, String> itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "BUY");
        itemMap.put("rel_alias", "buy");
        itemMap.put("end_node", "Book");
        itemMap.put("end_node_alias", "b");
        itemList.add(itemMap);
        itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "OF");
        itemMap.put("rel_alias", "of");
        itemMap.put("end_node", "Genre");
        itemMap.put("end_node_alias", "g");
        itemList.add(itemMap);
        HashMap<String, Object> itemSetMap = new HashMap<>();
        itemSetMap.put("num_min", 1);
        itemSetMap.put("num_max", 1);
        itemSetMap.put("item_path", itemList);

        Item i = new Item(itemSetMap, ItemPath.ItemType.HEAD);
        String actualEmpty =  i.toCypherReturn("");
        String expectedEmpty = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nRETURN len(collect(alias)) as suppcount, b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actualEmpty).isEqualTo(expectedEmpty);
        String actual=  i.toCypherReturn("body_BUY_Book");
        String expected = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nRETURN len(collect(alias)) as suppcount, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateQueryWith(){

        ArrayList<HashMap<String, String>> itemList = new ArrayList<>();
        HashMap<String, String> itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "BUY");
        itemMap.put("rel_alias", "buy");
        itemMap.put("end_node", "Book");
        itemMap.put("end_node_alias", "b");
        itemList.add(itemMap);
        itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "OF");
        itemMap.put("rel_alias", "of");
        itemMap.put("end_node", "Genre");
        itemMap.put("end_node_alias", "g");
        itemList.add(itemMap);
        HashMap<String, Object> itemSetMap = new HashMap<>();
        itemSetMap.put("num_min", 1);
        itemSetMap.put("num_max", 1);
        itemSetMap.put("item_path", itemList);

        Item i = new Item(itemSetMap, ItemPath.ItemType.HEAD);
        String actualEmpty =  i.toCypherWith("");
        String expectedEmpty = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nWITH alias, b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actualEmpty).isEqualTo(expectedEmpty);
        String actual=  i.toCypherWith("body_BUY_Book");
        String expected = "MATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nWITH alias, body_BUY_Book, b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actual).isEqualTo(expected);
    }

}
