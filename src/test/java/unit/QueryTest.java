package unit;

import it.polimi.deib.rkm.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryTest {

    @Test
    void shouldGenerateBody(){
        List<Map<String, Object>> itemList = new ArrayList<>();
        HashMap<String, Object> itemMap = new HashMap<>();
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
        HashMap<String, Object> itemSetMapBody = new HashMap<>();
        itemSetMapBody.put("num_min", 1L);
        itemSetMapBody.put("num_max", 1L);
        itemSetMapBody.put("item_path", itemList);
        HashMap<String, Object> itemSetMapHead = new HashMap<>();
        itemSetMapHead.put("num_min", 1L);
        itemSetMapHead.put("num_max", 1L);
        itemSetMapHead.put("item_path", itemList);

        List<Map<String, Object>> itemSetListBody = new ArrayList<>();
        List<Map<String, Object>> itemSetListHead = new ArrayList<>();

        itemSetListBody.add(itemSetMapBody);
        itemSetListHead.add(itemSetMapHead);

        Query query = new Query("P",
                "Person",
                itemSetListHead,
                itemSetListBody,
                0.5,
                0.5);

        String actual = query.toCypherForBody();
        String expected = "MATCH (n:Person)\nWITH n as alias\nMATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nRETURN size(collect(alias)) as suppcount, b.id as body_BUY_Book, g.id as body_OF_Genre";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateRule(){
        List<Map<String, Object>> itemListBody = new ArrayList<>();
        List<Map<String, Object>> itemListHead = new ArrayList<>();
        HashMap<String, Object> itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "BUY");
        itemMap.put("rel_alias", "buy");
        itemMap.put("end_node", "Book");
        itemMap.put("end_node_alias", "b");
        itemListBody.add(itemMap);
        itemListHead.add(itemMap);
        itemMap = new HashMap<>();
        itemMap.put("type", "normal");
        itemMap.put("rel_type", "OF");
        itemMap.put("rel_alias", "of");
        itemMap.put("end_node", "Genre");
        itemMap.put("end_node_alias", "g");
        itemListBody.add(itemMap);
        HashMap<String, Object> itemSetMapBody = new HashMap<>();
        itemSetMapBody.put("num_min", 1L);
        itemSetMapBody.put("num_max", 1L);
        itemSetMapBody.put("item_path", itemListBody);
        HashMap<String, Object> itemSetMapHead = new HashMap<>();
        itemSetMapHead.put("num_min", 1L);
        itemSetMapHead.put("num_max", 1L);
        itemSetMapHead.put("item_path", itemListHead);

        List<Map<String, Object>> itemSetListBody = new ArrayList<>();
        List<Map<String, Object>> itemSetListHead = new ArrayList<>();

        itemSetListBody.add(itemSetMapBody);
        itemSetListHead.add(itemSetMapHead);
        Query query = new Query("P",
                "Person",
                itemSetListHead,
                itemSetListBody,
                0.5,
                0.5);

        String actual = query.toCypherForRule();
        String expected = "MATCH (n:Person)\nWITH n as alias\nMATCH (alias)-[buy:BUY]-(b:Book)\nWITH alias, b.id as head_BUY_Book\nMATCH (alias)-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)\nRETURN size(collect(alias)) as suppcount, head_BUY_Book, b.id as body_BUY_Book, g.id as body_OF_Genre";
        assertThat(actual).isEqualTo(expected);

    }
}
