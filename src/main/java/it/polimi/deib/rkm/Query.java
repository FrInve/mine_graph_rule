package it.polimi.deib.rkm;

import java.util.List;

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
            List<String> item_head,
            List<String> item_body,
            double support,
            double confidence
    ) {
        this.alias = alias;
        this.aliasNode = aliasNode;
        this.head = new ItemSet(item_head);
        this.body = new ItemSet(item_body);
        this.support = support;
        this.confidence = confidence;
    }
    public String toCypherForBody() {
        return "MATCH (n: " +
                this.aliasNode +
                ")\nWITH m as alias\n" +
                this.body.toCypherReturn("");
    }

    public String toCypherForRule() {
        return "MATCH (n: " +
                this.aliasNode +
                ")\nWITH m as alias\n" +
                this.head.toCypherWith("") +
                "\n"+
                this.body.toCypherReturn(this.body.getAliasString());
    }

}