package it.polimi.deib.rkm.fragments;

import java.util.Map;

public class Count implements TailFragment {
    private final String relationshipType;
    private final String nodeLabel;
    private final String nodeVariable;
    private int minValue;
    private TailFragment previousFragment;

    public Count(Map<String, String> serializedFragment){
        this.relationshipType = serializedFragment.get("relationshipType");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
        try {
            this.minValue = Integer.parseInt(serializedFragment.get("minValue"));
        } catch (NullPointerException e){
            this.minValue = 1;
        }
    }
    @Override
    public String toCypher(int iterationNumber) {
        if (iterationNumber == 0) {
            return "-[:" + relationshipType + "]->(" + nodeVariable + ":" + nodeLabel + ")";
        }else{
            return "-[:" + relationshipType + "]->(" + nodeVariable + iterationNumber + ":" + nodeLabel + ")";

        }
    }

    @Override
    public String getCypherWithDefinition(String prefix, int iterationNumber) {
        if (iterationNumber == 0) {
            return nodeVariable + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return nodeVariable + iterationNumber + ".id as " + prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }

    @Override
    public String getNodeVariable() {
        return nodeVariable;
    }

    @Override
    public void setPreviousFragment(TailFragment previousFragment) {
        this.previousFragment = previousFragment;
    }

    @Override
    public String getReturnVariable(String prefix, int iterationNumber) {
        if (iterationNumber == 0) {
            return prefix + "_" + relationshipType + "_" + nodeLabel;
        }
        return prefix + "_" + relationshipType + "_" + nodeLabel + iterationNumber;
    }

    @Override
    public String getWhereClause(String prefix, int iterationNumber) {
        String startNodeVariable;
        if(previousFragment == null){
           startNodeVariable = "anchor";
        } else {
            startNodeVariable = previousFragment.getNodeVariable() +
                    (iterationNumber == 0 ? "" : iterationNumber);
        }
        String endNodeVariable = nodeVariable + (iterationNumber == 0 ? "" : iterationNumber);
        return "size([(" + startNodeVariable + ")-[countPath:" + relationshipType +
                "]->(" + endNodeVariable + ") | countPath]) >= " + minValue;
    }
}
