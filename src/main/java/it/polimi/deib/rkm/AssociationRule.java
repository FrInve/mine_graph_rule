package it.polimi.deib.rkm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssociationRule {
    private Map<String, Object> body;
    private Map<String, Object> head;
    private Double support;
    private Double confidence;

    private AssociationRule() {
        this.head = new HashMap<>();
        this.body = new HashMap<>();
        this.confidence = 0.0;
        this.support = 0.0;
    }

    private AssociationRule(AssociationRuleBuilder builder) {
        this.head = builder.head;
        this.body = builder.body;
        this.confidence = builder.confidence;
        this.support = builder.support;
    }

    public static class AssociationRuleBuilder {
        private Map<String, Object> head;
        private Map<String, Object> body;
        private Double support;
        private Double confidence;

        public AssociationRuleBuilder() {
            this.head = new HashMap<>();
            this.body = new HashMap<>();
            this.support = 0.0;
            this.confidence = 0.0;
        }

        public AssociationRuleBuilder setSupport(Double support) {
            this.support = support;
            return this;
        }

        public AssociationRuleBuilder setConfidence(Double confidence) {
            this.confidence = confidence;
            return this;
        }

        public AssociationRuleBuilder addHead(String itemName, String item){
            itemName = itemName.replace("head_", "");
            this.head.put(itemName, item);
            return this;
        }

        public AssociationRuleBuilder addBody(String itemName, String item){
            itemName = itemName.replace("body_", "");
            this.body.put(itemName, item);
            return this;
        }

        public AssociationRule build(){
            return new AssociationRule(this);
        }


    }

    public Record toRecord(){
        return new Record(this);
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
    public static class Record {
        public Map<String,Object> head;
        public Map<String,Object> body;
        public double confidence;
        public double support;

        public Record(AssociationRule rule){
            this.confidence = rule.confidence;
            this.support = rule.support;
            this.head = rule.head;
            this.body = rule.body;
        }

    }
}
