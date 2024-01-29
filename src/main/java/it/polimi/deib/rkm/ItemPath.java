package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemPath {


    private final String prefixAlias;


    public static enum ItemType {HEAD, BODY};

    private final ArrayList<HashMap<String, String>> path;


    public ItemPath(ArrayList<HashMap<String, String>> item_path, ItemType itemMode){
        this.path = item_path;
        switch(itemMode){
            case HEAD:
                this.prefixAlias = "head";
            break;
            case BODY:
                this.prefixAlias = "body";
            break;
            default:
                this.prefixAlias = "";
        };
    }

    public String toCypher() {
        String query = "";
        for (HashMap<String, String> element : this.path) {
            switch(element.get("type")) {
                case "normal":
                    query += toCypherNormal(element);
                    break;
                default:
                    query += "";
            }} // Add her rel_type count-any-shortest
        return query;
    }

    public String toCypherNormal(HashMap<String, String> element) {
        return "-[" +
                element.get("rel_alias") +
                ":" +
                element.get("rel_type") +
                "]-(" +
                element.get("end_node_alias") +
                ":" +
                element.get("end_node") +
                ")";
    }

    public String getStringVariable() {
        StringBuilder stringVariables = new StringBuilder();
        for (HashMap<String, String> element : this.path) {
            switch(element.get("type")) {
                case "normal":
                    stringVariables.append(element.get("end_node_alias"))
                            .append(".id as ")
                            .append(this.prefixAlias)
                            .append("_")
                            .append(element.get("rel_alias"))
                            .append("_")
                            .append(element.get("end_node_alias"))
                            .append(", ");
                    break;
            }} // Add her rel_type count-any-shortest
        String variables = stringVariables.toString();
        return variables.substring(0, variables.length() - 2);
    }
}
