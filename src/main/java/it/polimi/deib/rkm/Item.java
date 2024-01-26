package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Item {


    public Item(Map<String, Object> item) {
        int numMax = (Integer) item.get("num_max");
        int numMin = (Integer) item.get("num_min");
        ArrayList<ItemPath> itemPath = new ItemPath(item.get("item_path"));
    }

    public void fill(){

    }
}
