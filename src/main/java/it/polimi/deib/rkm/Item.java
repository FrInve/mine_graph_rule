package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {


    private final ItemPath itemPath;


    public Item(Map<String, Object> item, ItemPath.ItemType itemMode) {
        int numMax = (Integer) item.get("num_max");
        int numMin = (Integer) item.get("num_min");
        this.itemPath = new ItemPath((ArrayList<HashMap<String, String>>) item.get("item_path"),
                itemMode);
    }

    public String toCypherMatch() {
        String query = "MATCH (alias)";
        query = query + this.itemPath.toCypher();
        return query;
    }

    public String toCypherReturn(String otherAlias) {
        return toCypherMatch() + "\nRETURN alias, " + otherAlias + ", " + this.itemPath.getStringVariable();
    }

    public String toCypherWith(String otherAlias) {
        return toCypherMatch() + "\nWITH alias, " + otherAlias + ", " + this.itemPath.getStringVariable();
    }

    public String getAliasString(){
        return this.itemPath.getStringAlias();
    }
}
