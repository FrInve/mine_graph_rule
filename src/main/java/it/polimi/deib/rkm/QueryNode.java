package it.polimi.deib.rkm;

import it.polimi.deib.rkm.exceptions.ExceedingPatternNumberException;
import it.polimi.deib.rkm.filtering.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryNode {

    private final String anchor;
    private final String anchorType;
    private final String anchorWhereClause;
    private final PatternSet body;
    private final PatternSet head;
    private final double support;
    private final List<Where> where;
    private final Set<String> ignore;
    private final double confidence;
    private final List<QueryNode> children;
    private boolean halt;

    public QueryNode(Query model) {
        this.anchor = model.getAnchor();
        this.anchorType = model.getAnchorLabel();
        this.anchorWhereClause = model.getAnchorWhereClause();
        this.body = model.getBody();
        this.head = model.getHead();
        this.where = model.getWhere();
        this.ignore = model.getIgnore();
        this.children = new ArrayList<>();
        this.halt = false;
        this.support = model.getSupport();
        this.confidence = model.getConfidence();
        // Set previous fragments for each tail
        body.setPreviousFragments();
        head.setPreviousFragments();
    }

    private QueryNode(QueryNode father, PatternSet body, PatternSet head){
        this.anchor = father.getAnchor();
        this.anchorType = father.getAnchorType();
        this.anchorWhereClause = father.getAnchorWhereClause();
        this.body = body;
        this.head = head;
        this.where = father.getWhere();
        this.ignore = father.getIgnore();
        this.children = new ArrayList<>();
        this.halt = false;
        this.support = father.getSupport();
        this.confidence = father.getConfidence();
    }

    public String getAnchor(){
        return this.anchor;
    }

    public String getAnchorType(){
        return this.anchorType;
    }

    public String getAnchorWhereClause(){
        return this.anchorWhereClause;
    }

    public double getSupport(){
        return this.support;
    }

    public double getConfidence(){
        return this.confidence;
    }

    public Set<String> getIgnore(){
        return this.ignore;
    }

    public List<Where> getWhere() { return this.where; }
    public boolean getHalt(){
        return this.halt;
    }

    public void setHalt(boolean value){
        this.halt = value;
    }

    private int getVariableCardinalityInRule(String variable){
        return Math.max(body.getVariableCardinality(variable), head.getVariableCardinality(variable));
    }

    private int getVariableCardinalityInBody(String variable){
        return body.getVariableCardinality(variable);
    }
    private String generateWhereClauseForRule() {
        where.forEach(w -> w.setVariableCardinality(this.getVariableCardinalityInRule(w.getVariable())));
        where.forEach(w -> w.setOtherVariableCardinality(this.getVariableCardinalityInRule(w.getOtherVariable())));
        Stream<String> whereStream = where.stream().map(Where::getWhereClause);
        // Add count fragments WHERE clauses like: size([(a)-[countPath:Type]->(b) | countPath]) >= min
        whereStream = Stream.concat(whereStream, body.getFragmentsWhereClauses());
        whereStream = Stream.concat(whereStream, head.getFragmentsWhereClauses()).filter(Objects::nonNull);

        String whereContent;
//        if(whereStream.count() > 1){
            whereContent = whereStream.collect(Collectors.joining(" AND "));
//        } else {
//            whereContent = whereStream.findFirst().orElse("");
//        }
        // Decide if the WHERE clause should be added
        if(whereContent.isEmpty()){
            return "";
        }
        return "WHERE " + whereContent;
    }
    private String generateWhereClauseForBody() {
        where.forEach(w -> w.setVariableCardinality(this.getVariableCardinalityInBody(w.getVariable())));
        where.forEach(w -> w.setOtherVariableCardinality(this.getVariableCardinalityInBody(w.getOtherVariable())));
        String whereContent = where.stream().filter(w->w.existVariable() && w.existOtherVariable()).map(Where::getWhereClause).collect(Collectors.joining(" AND "));
        if(whereContent.isEmpty()){
            return "";
        }
        return "WHERE " + whereContent;
    }

    public String getRuleInCypher(Long transactionsCount){
        StringBuilder sb = new StringBuilder();
        sb.append("MATCH (")
                .append(anchor).append(":").append(anchorType).append(")\n");
                    if(!anchorWhereClause.isEmpty()){
                        sb.append("WHERE ");
                        sb.append(anchorWhereClause)
                                .append("\n");
                    }
                sb.append("WITH ").append(anchor).append(" AS anchor\n");

        sb.append("MATCH ")
                .append(body.getMatchClause("anchor"))
                .append(head.getMatchClause("anchor"))
                .delete(sb.length() - 2, sb.length()).append("\n");
        sb.append(generateWhereClauseForRule()) // WHERE CLAUSE FOR RULE
                .append("\n");
        sb.append("WITH count(DISTINCT ").append("anchor").append(") as suppcount, ")
                .append(body.getWithVariables(ignore))
                .append(head.getWithVariables(ignore))//.append(", ")
                .delete(sb.length() - 2, sb.length()).append("\n");

        sb.append("WHERE suppcount > ").append(transactionsCount * support).append("\n");

        sb.append("RETURN suppcount, ")
                .append(body.getReturnVariables(ignore))
                .append(head.getReturnVariables(ignore))//.append(", ")
                .delete(sb.length() - 2, sb.length());//.append("\n");
        return sb.toString();
    }

    public String getBodyInCypher(Long transactionsCount){
        StringBuilder sb = new StringBuilder();

        sb.append("MATCH (")
                .append(anchor).append(":").append(anchorType).append(")\n");
        if(!anchorWhereClause.isEmpty()){
            sb.append("WHERE ");
            sb.append(anchorWhereClause)
                    .append("\n");
        }
        sb.append("WITH ").append(anchor).append(" AS anchor\n");

        sb.append("MATCH ")
                .append(body.getMatchClause("anchor"))
                .delete(sb.length() - 2, sb.length()).append("\n");
        sb.append(generateWhereClauseForBody())     // WHERE CLAUSE FOR BODY
                .append("\n");
        sb.append("WITH count(DISTINCT ").append("anchor").append(") as suppcount, ")
                .append(body.getWithVariables(ignore))
                .delete(sb.length() - 2, sb.length()).append("\n");

        sb.append("WHERE suppcount > ").append((int) Math.floor(transactionsCount * support)).append("\n");

        sb.append("RETURN suppcount, ")
                .append(body.getReturnVariables(ignore))
                .delete(sb.length() - 2, sb.length());//.append("\n");
        return sb.toString();
    }

    public void generateChildren(){
//        if(this.halt){
//            return;
//        }
        for(int i=0; i<this.body.getPatterns().size(); i++){
            try{
                PatternSet newBody = this.body.cloneAndReplacePattern(i, this.body.getPatterns().get(i).incrementAndCopy());
                QueryNode child = new QueryNode(this, newBody, this.head);
                child.setHalt(this.getHalt());
                this.children.add(child);}
            catch(ExceedingPatternNumberException ignored){
            }
        }
        for(int i=0; i<this.head.getPatterns().size(); i++){
            try {
                PatternSet newHead = this.head.cloneAndReplacePattern(i, this.head.getPatterns().get(i).incrementAndCopy());
                QueryNode child = new QueryNode(this, this.body, newHead);
                child.setHalt(this.getHalt());
                this.children.add(child);
            }
            catch(ExceedingPatternNumberException ignored){
            }
        }
    }

    public List<QueryNode> getChildren(){
        return this.children;
    }

    @Override
    public int hashCode() {
        return this.getRuleInCypher(0L).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QueryNode other = (QueryNode) obj;
        return this.getRuleInCypher(0L).hashCode() == other.getRuleInCypher(0L).hashCode();
    }
}
