package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemPath {


    public static enum ItemType {HEAD, BODY};

    private final ArrayList<HashMap<String, String>> path;
    //private ItemType itemMode;


    public ItemPath(ArrayList<HashMap<String, String>> item_path, ItemType itemMode){
        this.path = item_path;
        //this.itemMode = itemMode;
    }

    public String toCypher() {
        StringBuilder query = new StringBuilder();
        for (HashMap<String, String> element : this.path) {
            if (element.get("rel_type").equals("normal")){
                //switch(this.type){
                  //  case ItemType.HEAD:
                query.append("-[")
                        .append(element.get("rel_alias"))
                        .append(":")
                        .append(element.get("rel_type"))
                        .append("]-(")
                        .append(element.get("end_node_alias"))
                        .append(":")
                        .append(element.get("end_node"))
                        .append(")");
                //break;
                   // case ItemType.BODY:

                      //  break;
                    //default:
                      //  throw new IllegalStateException("Unexpected value: " + this.type);
                }}
    //} // Add her rel_type count-any-shortest
        return query.toString();
    }

    public String getStringVariable() {
        StringBuilder query = new StringBuilder();
        for (HashMap<String, String> element : this.path) {
            if (element.get("rel_type").equals("normal"))
                query.append("-[")
                        .append(element.get("rel_alias"))
                        .append(":")
                        .append(element.get("rel_type"))
                        .append("]-(")
                        .append(element.get("end_node_alias"))
                        .append(":")
                        .append(element.get("end_node"))
                        .append(")");
        } // Add her rel_type count-any-shortest

    }
}
