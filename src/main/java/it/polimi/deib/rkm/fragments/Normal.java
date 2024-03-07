package it.polimi.deib.rkm.fragments;

import java.util.Map;

public class Normal implements TailFragment {
    private String type;
    private String relationshipType;
    private String nodeLabel;
    private String nodeVariable;

    public Normal(Map<String, String> serializedFragment){
        this.type = serializedFragment.get("type");
        this.relationshipType = serializedFragment.get("relationshipType");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
    }
    public String toCypher(int iterationNumber){
        if (iterationNumber == 0) {
            return "-[:" + relationshipType + "]-(" + nodeVariable + ":" + nodeLabel + ")";
        }else{
            return "-[:" + relationshipType + "]-(" + nodeVariable + iterationNumber + ":" + nodeLabel + ")";

        }
    }

    public String getCypherReturnDefinition(String prefix, int iterationNumber) {
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
}
