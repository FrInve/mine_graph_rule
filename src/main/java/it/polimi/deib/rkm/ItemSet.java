package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSet {
    // TODO: Remove this field
    @Deprecated
    private ArrayList<Item> patters;

    private Item item;

    public ItemSet(List<Map<String, Object>> item_list, ItemPath.ItemType itemMode) {
        this.item = new Item(item_list.get(0), itemMode);

    }

    // TODO: Remove this constructor
    @Deprecated
    public ItemSet(Map<String, Object> single_item, ItemPath.ItemType itemMode) {
        this.item = new Item(single_item, itemMode);
    }

    public String toCypherReturn(String otherAlias) {
        return this.item.toCypherReturn(otherAlias);
    }

    public String toCypherWith(String otherAlias, String alias, String aliasNode) {
        return this.item.toCypherWith(otherAlias, alias, aliasNode);
    }

    public String getAliasString(){
        return this.item.getAliasString();
    }


    // TODO: Is this necessary? Or deprecated?
    public void addOne(Item item){

    }

}
