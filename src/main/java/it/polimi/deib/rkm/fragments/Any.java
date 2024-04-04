package it.polimi.deib.rkm.fragments;

import java.util.Map;

public class Any implements TailFragment {
    private String type;
    private String relationshipType;
    private String relationshipLength;
    private String nodeLabel;
    private String nodeVariable;

    public Any(Map<String, String> serializedFragment){
        this.type = serializedFragment.get("type");
        this.relationshipType = serializedFragment.get("relationshipType");
        this.relationshipLength = serializedFragment.get("relationshipLength");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
    }
    public String toCypher(int iterationNumber){
        if (iterationNumber == 0) {
            return "-[*" + relationshipLength + "]->(" + nodeVariable + ":" + nodeLabel + ")";
        }else{
            return "-[*" + relationshipLength + "]->(" + nodeVariable + iterationNumber + ":" + nodeLabel + ")";

        }
    }

    public String getCypherWithDefinition(String prefix, int iterationNumber) {
        if (iterationNumber == 0) {
            return nodeVariable + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return nodeVariable + iterationNumber + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }

    /**
     * Returns the RETURN variables for the fragment
     * Example: "n.id as prefix_relationshipType_nIterationNumber,
     * Example2: "product.id as head0_Buy_product"
     * Example3: "product1.id as head0_Buy_product1"
     * @param prefix the prefix to be used for the return variables
     * @param iterationNumber the iteration number of the fragment
     * @return the return variables for the fragment
     */
    public String getReturnVariables(String prefix, int iterationNumber){
        StringBuilder sb = new StringBuilder();
        sb.append(nodeVariable).append(".id as ")
                .append(prefix).append("_")
                .append(relationshipType).append("_")
                .append(nodeVariable).append(iterationNumber);
        return sb.toString();
    }

    /**
     * Return the variable name for the fragment
     * @param prefix
     * @param iterationNumber
     * @return
     */
    public String getReturnVariable(String prefix, int iterationNumber){
        if (iterationNumber == 0) {
            return prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }
}
