package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public class Query {
    private String alias;
    private String aliasNode;
    private ItemSet body;
    private ItemSet head;
    private double support;
    private double confidence;

    public Query(
            String alias,
            String aliasNode,
            List<Map<String, Object>> item_head,
            List<Map<String, Object>> item_body,
            double support,
            double confidence
    ) {
        this.alias = alias;
        this.aliasNode = aliasNode;
        this.head = new ItemSet(item_head, ItemPath.ItemType.HEAD);
        this.body = new ItemSet(item_body, ItemPath.ItemType.BODY);
        this.support = support;
        this.confidence = confidence;
    }
//    public Query(
//            String alias,
//            String aliasNode,
//            Map<String, Object> item_head,
//            Map<String, Object> item_body,
//            double support,
//            double confidence
//    ) {
//        this.alias = alias;
//        this.aliasNode = aliasNode;
//        this.head = new ItemSet(item_head, ItemPath.ItemType.HEAD);
//        this.body = new ItemSet(item_body, ItemPath.ItemType.BODY);
//        this.support = support;
//        this.confidence = confidence;
//    }

    public String toCypherForBody() {
        return "MATCH (n:" +
                this.aliasNode +
                ")\nWITH n as alias\n" +
                this.body.toCypherReturn("");
    }

    public String toCypherForRule() {
//        return "MATCH (n:" +
//                this.aliasNode +
//                ")\nWITH n as alias\n" +
//                this.head.toCypherWith("",this.alias,this.aliasNode) +
//                "\n"+
//                this.body.toCypherReturn(this.head.getAliasString());
        String head = this.head.toCypherWith("", this.alias, this.aliasNode);
        String body = this.body.toCypherReturn(this.head.getAliasString());
        // Split body after each UNION and add head to each part
        List<String> bodyParts = List.of(body.split("\nUNION\n"));

        StringBuilder result = new StringBuilder();
        result.append("MATCH (n:").append(this.aliasNode).append(")\nWITH n as alias\n");
        result.append(head).append("\n").append(bodyParts.get(0));
        for (String part : bodyParts.subList(1, bodyParts.size())) {
            result.append("\nUNION\n")
                    .append(head).append("\n").append(part).append("\n");
        }
        return result.toString();
    }

}