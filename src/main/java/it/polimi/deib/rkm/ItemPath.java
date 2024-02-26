package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public class ItemPath {


    private final String prefixAlias;

    public enum ItemType {HEAD, BODY}

    private final List<Map<String, String>> path;


    public ItemPath(List<Map<String, String>> item_path, ItemType itemMode){
        this.path = item_path;
        switch (itemMode) {
            case HEAD -> this.prefixAlias = "head";
            case BODY -> this.prefixAlias = "body";
            default -> this.prefixAlias = "";
        }
    }

    public String toCypher(int size) {
        StringBuilder query = new StringBuilder();
        for (Map<String, String> element : this.path) {
            switch (element.get("type")) {
                case "normal" -> query.append(toCypherNormal(element, size));
            }
        } // Add here rel_type count-any-shortest
        return query.toString();
    }

    public String toCypher(){
        return this.toCypher(0);
    }

    public String toCypherNormal(Map<String, String> element) {
//        return "-[" +
//                element.get("rel_alias") +
//                ":" +
//                element.get("rel_type") +
//                "]-(" +
//                element.get("end_node_alias") +
//                ":" +
//                element.get("end_node") +
//                ")";
        return this.toCypherNormal(element, 0);
    }

    public String toCypherNormal(Map<String, String> element, int i) {
        if(i==0){
            return "-[" +
                    element.get("rel_alias") +
                    ":" +
                    element.get("rel_type") +
                    "]-(" +
                    element.get("end_node_alias") +
                    ":" +
                    element.get("end_node") +
                    ")";
        } else {
            return "-[" +
                    element.get("rel_alias") +
                    i +
                    ":" +
                    element.get("rel_type") +
                    "]-(" +
                    element.get("end_node_alias") +
                    i +
                    ":" +
                    element.get("end_node") +
                    ")";
        }
    }
    public String getStringVariable(){
        return this.getStringVariable(0);
    }
    public String getStringVariable(int sizeNumber) {
        StringBuilder stringVariables = new StringBuilder();
        for (Map<String, String> element : this.path) {
            switch (element.get("type")) {
//                case "normal" -> stringVariables.append(element.get("end_node_alias"))
//                        .append(".id as ")
//                        .append(this.prefixAlias)
//                        .append("_")
//                        .append(element.get("rel_type"))
//                        .append("_")
//                        .append(element.get("end_node"))
//                        .append(", ");
                case "normal" -> {
                    stringVariables.append("apoc.coll.dropDuplicateNeighbors(apoc.coll.sort([");
                    for(int i=0; i<=sizeNumber; i++){
                        stringVariables.append(element.get("end_node_alias"));
                        if(i!=0){
                            stringVariables.append(i);
                        }
                        stringVariables.append(".id");
                        if(i!=sizeNumber){
                            stringVariables.append(", ");
                        }
                    }
                    stringVariables.append("])) as ")
                            .append(this.prefixAlias).append("_")
                            .append(element.get("rel_type")).append("_")
                            .append(element.get("end_node")).append(", ");
                }
            }
        } // Add here rel_type count-any-shortest
        String variables = stringVariables.toString();
        return variables.substring(0, variables.length() - 2);
    }

    public String getStringAlias() {
        StringBuilder stringVariables = new StringBuilder();
        for (Map<String, String> element : this.path) {
            switch (element.get("type")) {
                case "normal" -> stringVariables.append(this.prefixAlias)
                        .append("_")
                        .append(element.get("rel_type"))
                        .append("_")
                        .append(element.get("end_node"))
                        .append(", ");
            }
        } // Add here rel_type count-any-shortest
        String variables = stringVariables.toString();
        return variables.substring(0, variables.length() - 2);
    }

}
