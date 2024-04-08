package it.polimi.deib.rkm;

import it.polimi.deib.rkm.filtering.Where;
import it.polimi.deib.rkm.filtering.WhereFactory;

import java.util.*;

public class Query {
    private final String anchor;
    private final String anchorLabel;
    private final String anchorWhereClause;
    private final PatternSet body;
    private final PatternSet head;
    private final List<Where> where;
    private final Set<String> ignore;
    private final double support;
    private final double confidence;

    public Query(
            String anchor,
            String anchorLabel,
            String anchorWhereClause,
            List<Map<String, Object>> serializedHead,
            List<Map<String, Object>> serializedBody,
            List<Map<String, String>> where,
            List<String> ignore,
            double support,
            double confidence
    ) {
        this.anchor = anchor;
        this.anchorLabel = anchorLabel;
        this.anchorWhereClause = anchorWhereClause;
        this.head = new Head(serializedHead);
        this.body = new Body(serializedBody);
        this.where = new ArrayList<>();
        where.forEach(w -> this.where.add(WhereFactory.createWhere(w)));
        this.ignore = new HashSet<>();
        this.ignore.addAll(ignore);
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

    public Set<String> getIgnore(){
        return this.ignore;
    }

    public List<String> getRuleColumnNames(){
        List<String> columns = new ArrayList<>();
        columns.addAll(head.getColumnNames("head", ignore));
        columns.addAll(body.getColumnNames("body", ignore));
        columns.add("suppcount");
        return columns;
    }

    public List<String> getBodyColumnNames(){
        List<String> columns = new ArrayList<>(body.getColumnNames("body", ignore));
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