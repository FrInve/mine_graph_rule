package it.polimi.deib.rkm;

import java.util.ArrayList;
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
        StringBuilder sb = new StringBuilder();
//        sb.append("WHERE ")
                sb.append(this.anchorWhereClause);
        return sb.toString();
    }

    public PatternSet getBody(){
        return this.body;
    }

    public PatternSet getHead(){
        return this.head;
    }

    public List<String> getRuleColumnNames(){
        List<String> columns = new ArrayList<>();
        columns.addAll(head.getColumnNames("head"));
        columns.addAll(body.getColumnNames("body"));
        columns.add("suppcount");
        return columns;
    }

    public List<String> getBodyColumnNames(){
        List<String> columns = new ArrayList<>(body.getColumnNames("body"));
        columns.add("suppcount");
        return columns;
    }

    public double getSupport() {
        return support;
    }

    public double getConfidence() {
        return confidence;
    }




}