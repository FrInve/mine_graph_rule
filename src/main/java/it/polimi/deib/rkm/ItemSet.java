package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSet {
    private ArrayList<Item> patters;

    private Item item;

    public ItemSet(List<String> item_list) {

    }

    public ItemSet(Map<String, Object> single_item, ItemPath.ItemType itemMode) {
        this.item = new Item(single_item, itemMode);
    }

    public String toCypherReturn(String otherAlias) {
        return this.item.toCypherReturn(otherAlias);
    }

    public String toCypherWith(String otherAlias) {
        return this.item.toCypherWith(otherAlias);
    }

    public String getAliasString(){
        return this.item.getAliasString();
    }


    public void addOne(Item item){

    }

}
