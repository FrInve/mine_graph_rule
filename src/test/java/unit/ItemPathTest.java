package unit;

import it.polimi.deib.rkm.ItemPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemPathTest {

    @Test
    void shouldGeneratePath(){

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

        ItemPath ip = new ItemPath(itemList, ItemPath.ItemType.HEAD);
        String actual =  ip.toCypher();
        String expected = "-[buy:BUY]-(b:Book)-[of:OF]-(g:Genre)";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateVariables(){
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

        ItemPath ip = new ItemPath(itemList, ItemPath.ItemType.HEAD);
        String actual =  ip.getStringVariable();
        String expected = "b.id as head_BUY_Book, g.id as head_OF_Genre";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateAliasString(){
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

        ItemPath ip = new ItemPath(itemList, ItemPath.ItemType.HEAD);
        String actual =  ip.getStringAlias();
        String expected = "head_BUY_Book, head_OF_Genre";
        assertThat(actual).isEqualTo(expected);

    }
}
