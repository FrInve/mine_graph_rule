package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public class Item {


    private final ItemPath itemPath;
    private final int numMax;
    private final int numMin;


    public Item(Map<String, Object> item, ItemPath.ItemType itemMode) {
        this.numMax = ((Long) item.get("num_max")).intValue();
        this.numMin = ((Long) item.get("num_min")).intValue();
        this.itemPath = new ItemPath((List<Map<String, String>>) item.get("item_path"),
                itemMode);
    }

    public String toCypherMatch() {
        String query = "MATCH (alias)";
        query = query + this.itemPath.toCypher();
        return query;
    }

    public String toCypherReturn(String otherAlias) {
        if (otherAlias.isEmpty()) {
            return toCypherMatch() + "\nRETURN size(collect(DISTINCT alias)) as suppcount, " + this.itemPath.getStringVariable();}
        else {
            return toCypherMatch() + "\nRETURN size(collect(DISTINCT alias)) as suppcount, " + otherAlias + ", " + this.itemPath.getStringVariable();}
    }

    public String toCypherWith(String otherAlias) {
        if (otherAlias.isEmpty()) {
            return toCypherMatch() + "\nWITH alias, " + this.itemPath.getStringVariable();}
        else {
            return toCypherMatch() + "\nWITH alias, " + otherAlias + ", " + this.itemPath.getStringVariable();}
    }

    public String getAliasString(){
        return this.itemPath.getStringAlias();
    }
}
