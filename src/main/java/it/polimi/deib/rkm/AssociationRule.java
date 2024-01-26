package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;

public final class AssociationRule {
    private ItemSet body;
    private ItemSet head;
    private double support;
    private double confidence;

    public AssociationRule(
            List<String> item_head,
            List<String> item_body,
            double support,
            double confidence
            ) {
        this.head = new ItemSet(item_head);
        this.body = new ItemSet(item_body);
        this.support = support;
        this.confidence = confidence;
    }

    public AssociationRuleRecord toRecord(){
        return new AssociationRuleRecord(
                this.head.toString(),
                this.body.toString(),
                this.support,
                this.confidence);
    }


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
     *     <li>{@link String}</li>
     *     <li>{@link Long} or {@code long}</li>
     *     <li>{@link Double} or {@code double}</li>
     *     <li>{@link Number}</li>
     *     <li>{@link Boolean} or {@code boolean}</li>
     *     <li>{@link Node}</li>
     *     <li>{@link org.neo4j.graphdb.Relationship}</li>
     *     <li>{@link org.neo4j.graphdb.Path}</li>
     *     <li>{@link Map} with key {@link String} and value {@link Object}</li> - Keys dont have quotes in Cypher!
     *     <li>{@link List} of elements of any valid field type, including {@link List}</li>
     *     <li>{@link Object}, meaning any of the valid field types</li>
     * </ul>
     */
    public static class AssociationRuleRecord {
        public double confidence;
        public double support;
        public String head;
        public String body;

        public AssociationRuleRecord(String head, String body, double support, double confidence){
            this.confidence = confidence;
            this.support = support;
            this.head = head;
            this.body = body;
        }

    }
}
