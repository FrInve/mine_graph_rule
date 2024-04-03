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

    public String toCypher() {
        StringBuilder query = new StringBuilder();
        for (Map<String, String> element : this.path) {
            switch (element.get("type")) {
                case "normal" -> query.append(toCypherNormal(element));
                case "reverse" -> query.append(toCypherReverse(element));
                case "any" -> query.append(toCypherAny(element));
                case "anyReverse" -> query.append(toCypherAnyReverse(element));
            }
        } // Add here rel_type count-any-shortest
        return query.toString();
    }

    public String toCypherNormal(Map<String, String> element) {
        return "-[" +
                element.get("rel_alias") +
                ":" +
                element.get("rel_type") +
                "]->(" +
                element.get("end_node_alias") +
                ":" +
                element.get("end_node") +
                ")";
    }

    public String toCypherReverse(Map<String, String> element) {
        return "<-[" +
                element.get("rel_alias") +
                ":" +
                element.get("rel_type") +
                "]-(" +
                element.get("end_node_alias") +
                ":" +
                element.get("end_node") +
                ")";
    }

    public String toCypherAny(Map<String, String> element) {
        return "-[*" +
                element.get("rel_len") +
                "]->(" +
                element.get("end_node_alias") +
                ":" +
                element.get("end_node") +
                ")";
    }

    public String toCypherAnyReverse(Map<String, String> element) {
        return "<-[*" +
                element.get("rel_len") +
                "]-(" +
                element.get("end_node_alias") +
                ":" +
                element.get("end_node") +
                ")";
    }

    public String getStringVariable() {
        StringBuilder stringVariables = new StringBuilder();
        for (Map<String, String> element : this.path) {
            switch (element.get("type")) {
                case "normal" -> stringVariables.append(element.get("end_node_alias"))
                        .append(".id as ")
                        .append(this.prefixAlias)
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
