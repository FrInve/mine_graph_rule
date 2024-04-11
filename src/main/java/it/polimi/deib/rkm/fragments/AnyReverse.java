package it.polimi.deib.rkm.fragments;

import java.util.Map;

public class AnyReverse implements TailFragment {
    private final String relationshipType;
    private final String relationshipLength;
    private final String nodeLabel;
    private final String nodeVariable;

    public AnyReverse(Map<String, String> serializedFragment){
        this.relationshipType = serializedFragment.get("relationshipType");
        this.relationshipLength = serializedFragment.get("relationshipLength");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
    }

    public String getNodeVariable(){
        return nodeVariable;
    }

    public String toCypher(int iterationNumber){
        if (iterationNumber == 0) {
            return "<-[*" + relationshipLength + "]-(" + nodeVariable + ":" + nodeLabel + ")";
        }else{
            return "<-[*" + relationshipLength + "]-(" + nodeVariable + iterationNumber + ":" + nodeLabel + ")";

        }
    }

    public String getCypherWithDefinition(String prefix, int iterationNumber) {
        if (iterationNumber == 0) {
            return nodeVariable + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return nodeVariable + iterationNumber + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }

    /**
     * Return the variable name for the fragment
     * @param prefix head/body + pattern number
     * @param iterationNumber the iteration number of the fragment at the end of the variable
     * @return the CYPHER variable name for the fragment
     */
    public String getReturnVariable(String prefix, int iterationNumber){
        if (iterationNumber == 0) {
            return prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }
}
