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
        return "MATCH (n:" +
                this.aliasNode +
                ")\nWITH n as alias\n" +
                this.head.toCypherWith("") +
                "\n"+
                this.body.toCypherReturn(this.head.getAliasString());
    }

}