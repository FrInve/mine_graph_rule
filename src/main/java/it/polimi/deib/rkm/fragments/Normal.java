package it.polimi.deib.rkm.fragments;

import java.util.Map;
import java.util.Objects;

public class Normal implements TailFragment {
    private final String relationshipType;
    private final String nodeLabel;
    private final String nodeVariable;
    private final String nodeProperty;

    public Normal(Map<String, String> serializedFragment){
        this.relationshipType = serializedFragment.get("relationshipType");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
        this.nodeProperty = serializedFragment.get("nodeProperty");
    }

    public String getNodeVariable(){
        return nodeVariable;
    }

    @Override
    public String getNodeLabel() {
        return nodeLabel;
    }

    @Override
    public String getRelationshipType() {
        return relationshipType;
    }
    public String toCypher(int iterationNumber){
        if (iterationNumber == 0) {
            return "-[:" + relationshipType + "]->(" + nodeVariable + ":" + nodeLabel + ")";
        }else{
            return "-[:" + relationshipType + "]->(" + nodeVariable + iterationNumber + ":" + nodeLabel + ")";

        }
    }

    public String getCypherWithDefinition(String prefix, int iterationNumber, String patternAlias) {
        String alias = prefix + "_" + relationshipType + "_" + nodeLabel;
        if (patternAlias!=null) {
           alias = prefix + "_" + patternAlias;
        }
        if (!Objects.equals(nodeProperty, "")) {
            if (iterationNumber == 0) {
                return nodeVariable + "." + nodeProperty + " as " + alias;
            }
            return nodeVariable + iterationNumber + "." + nodeProperty + " as " + alias + iterationNumber;
        }
        if (iterationNumber == 0) {
            return "elementId(" + nodeVariable + ") as " + alias;
        }
        return "elementId(" + nodeVariable + iterationNumber + ") as " + prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }

    /**
     * Return the variable name for the fragment
     * @param prefix head/body + pattern number
     * @param iterationNumber the iteration number of the fragment at the end of the variable
     * @return the CYPHER variable name for the fragment
     */
    public String getReturnVariable(String prefix, int iterationNumber, String patternAlias){
        String alias = prefix + "_" + relationshipType + "_" + nodeLabel;
        if (patternAlias!=null) {
            alias = prefix + "_" + patternAlias;
        }
        if (iterationNumber == 0) {
            return alias;
        }
        return alias + iterationNumber;
    }
}
