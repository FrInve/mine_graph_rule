package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;

public class QueryNode {

    private final String anchor;
    private final String anchorType;
    private final String anchorWhereClause;
    private final PatternSet body;
    private final PatternSet head;
    private final double support;
    private final double confidence;
    private final List<QueryNode> children;
    private final boolean halt;

    public QueryNode(Query model) {
        this.anchor = model.getAnchor();
        this.anchorType = model.getAnchorLabel();
        this.anchorWhereClause = model.getAnchorWhereClause();
        this.body = model.getBody();
        this.head = model.getHead();
        this.children = new ArrayList<>();
        this.halt = false;
        this.support = 0L;
        this.confidence = 0L;
    }

    private QueryNode(QueryNode father, PatternSet body, PatternSet head){
        this.anchor = father.getAnchor();
        this.anchorType = father.getAnchorType();
        this.anchorWhereClause = father.getAnchorWhereClause();
        this.body = body;
        this.head = head;
        this.children = new ArrayList<>();
        this.halt = false;
        this.support = 0L;
        this.confidence = 0L;
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

    public String getRuleInCypher(){
//        StringBuilder sb = new StringBuilder();
//        sb.append("HEAD: ");
//        for(Pattern pattern : this.head.getPatterns()){
//            sb.append(pattern.getNum()).append(" ");
//        }
//        sb.append("\tBODY: ");
//        for(Pattern pattern : this.body.getPatterns()){
//            sb.append(pattern.getNum()).append(" ");
//        }
//        return sb.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("MATCH ")
                .append(body.getMatchClause(anchor, anchorType))
                .append(head.getMatchClause(anchor, anchorType))
                .delete(sb.length() - 2, sb.length()).append("\n");
        if(!anchorWhereClause.isEmpty()){
            sb.append("WHERE ")
                .append(anchorWhereClause)
                .append("\n");
        }
        sb.append("RETURN count(DISTINCT ").append(anchor).append(") as suppcount, ")
                .append(body.getReturnVariables())
                .append(head.getReturnVariables())//.append(", ")
                .delete(sb.length() - 2, sb.length());//.append("\n");
        return sb.toString();
    }

    public String getBodyInCypher(){
        StringBuilder sb = new StringBuilder();
        sb.append("MATCH ")
                .append(body.getMatchClause(anchor, anchorType))
                .delete(sb.length() - 2, sb.length()).append("\n");
        if(!anchorWhereClause.isEmpty()){
            sb.append("WHERE ")
                    .append(anchorWhereClause)
                    .append("\n");
        }
        sb.append("RETURN count(DISTINCT ").append(anchor).append(") as suppcount, ")
                .append(body.getReturnVariables())
                .delete(sb.length() - 2, sb.length());//.append("\n");
        return sb.toString();
    }

    public void generateChildren(){
        if(this.halt){
            return;
        }
        for(int i=0; i<this.body.getPatterns().size(); i++){
            try{
                PatternSet newBody = this.body.cloneAndReplacePattern(i, this.body.getPatterns().get(i).incrementAndCopy());
                QueryNode child = new QueryNode(this, newBody, this.head);
                this.children.add(child);}
            catch(ExceedingPatternNumberException e){
                continue;
            }
        }
        for(int i=0; i<this.head.getPatterns().size(); i++){
            try {
                PatternSet newHead = this.head.cloneAndReplacePattern(i, this.head.getPatterns().get(i).incrementAndCopy());
                QueryNode child = new QueryNode(this, this.body, newHead);
                this.children.add(child);
            }
            catch(ExceedingPatternNumberException e){
                continue;
            }
        }
    }

    public List<QueryNode> getChildren(){
        return this.children;
    }

    @Override
    public int hashCode() {
        return this.getRuleInCypher().hashCode();
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
        return this.getRuleInCypher().hashCode() == other.getRuleInCypher().hashCode();
    }
}
