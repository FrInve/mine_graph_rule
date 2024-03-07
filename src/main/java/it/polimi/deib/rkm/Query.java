package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public class Query {
    private final String anchor;
    private final String anchorLabel;
    private final String anchorWhereClause;
    private final PatternSet body;
    private final PatternSet head;
    private final double support;
    private final double confidence;

    public Query(
            String anchor,
            String anchorLabel,
            String anchorWhereClause,
            List<Map<String, Object>> serializedHead,
            List<Map<String, Object>> serializedBody,
            double support,
            double confidence
    ) {
        this.anchor = anchor;
        this.anchorLabel = anchorLabel;
        this.anchorWhereClause = anchorWhereClause;
        this.head = new Head(serializedHead);
        this.body = new Body(serializedBody);
        this.support = support;
        this.confidence = confidence;
    }

    public String getAnchor(){
        return this.anchor;
    }

    public String getAnchorLabel(){
        return this.anchorLabel;
    }

    public String getAnchorWhereClause(){
        return this.anchorWhereClause;
    }

    public PatternSet getBody(){
        return this.body;
    }

    public PatternSet getHead(){
        return this.head;
    }

    public List<String> getRuleColumnNames(){
        return List.of("a1","a2","suppcount");
    }

    public List<String> getBodyColumnNames(){
        return List.of("a1","a2","suppcount");
    }
}