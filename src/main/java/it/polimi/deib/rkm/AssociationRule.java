package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public final class AssociationRule {
    private List<Map<String, Object>> body;
    private List<Map<String, Object>> head;
    private double support;
    private double confidence;

    public AssociationRule(
            List<Map<String, Object>> head,
            List<Map<String, Object>> body,
            double support,
            double confidence
            ) {
        this.head = head;
        this.body = body;
        this.support = support;
        this.confidence = confidence;
    }

//    public AssociationRuleRecord toRecord(){
//        return new AssociationRuleRecord(
//                this.head.toString(),
//                this.body.toString(),
//                this.support,
//                this.confidence,
//                List.of());
//    }


    /**
     * This is the output record for our search procedure. All procedures
     * that return results return them as a Stream of Records, where the
     * records are defined like this one - customized to fit what the procedure
     * is returning.
     * <p>
     * These classes can only have public non-final fields, and the fields must
     * be one of the following types:
     *
     * <ul>
     *     <li>{String}</li>
     *     <li>{Long} or {@code long}</li>
     *     <li>{Double} or {@code double}</li>
     *     <li>{Number}</li>
     *     <li>{Boolean} or {@code boolean}</li>
     *     <li>{Node}</li>
     *     <li>{org.neo4j.graphdb.Relationship}</li>
     *     <li>{org.neo4j.graphdb.Path}</li>
     *     <li>{Map} with key {String} and value {Object}</li> - Keys dont have quotes in Cypher!
     *     <li>{List} of elements of any valid field type, including {List}</li>
     *     <li>{Object}, meaning any of the valid field types</li>
     * </ul>
     */
    public static class AssociationRuleRecord {
        public List<String> rule;
        public String head;
        public String body;
        public double confidence;
        public double support;

        public AssociationRuleRecord(String head, String body, double support, double confidence, List<String> rule){
            this.confidence = confidence;
            this.support = support;
            this.head = head;
            this.body = body;
            this.rule = rule;
        }

    }
}
