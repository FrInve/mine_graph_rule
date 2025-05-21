package it.polimi.deib.rkm.fragments;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Count implements TailFragment {
    private final String relationshipType;
    private final String nodeLabel;
    private final String nodeVariable;
    private final String nodeProperty;
    private int minValue;
    private TailFragment previousFragment;
    List<Integer> whereCheck = new ArrayList<>();

    public Count(Map<String, String> serializedFragment){
        this.relationshipType = serializedFragment.get("relationshipType");
        this.nodeLabel = serializedFragment.get("nodeLabel");
        this.nodeVariable = serializedFragment.get("nodeVariable");
        this.nodeProperty = serializedFragment.get("nodeProperty");
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
    public String getCypherWithDefinition(String prefix, int iterationNumber, String patternAlias) {
        String alias = prefix + "_" + relationshipType + "_" + nodeLabel;
        if (patternAlias!=null) {
            alias = patternAlias;
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


    @Override
    public String getNodeVariable() {
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

    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public void setPreviousFragment(TailFragment previousFragment) {
        this.previousFragment = previousFragment;
    }

    @Override
    public String getReturnVariable(String prefix, int iterationNumber, String patternAlias) {
        String alias = prefix + "_" + relationshipType + "_" + nodeLabel;
        if (patternAlias!=null) {
            alias = patternAlias;
        }
        if (iterationNumber == 0) {
            return alias;
        }
        return alias + iterationNumber;
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
        return "COUNT{(" + startNodeVariable + ")-[" + relationshipType +
                "]->(" + endNodeVariable + ")} >= " + minValue;
    }

    @Override
    public String getWhereCountClause(int iterationNumber, String startNodePlaceholder, String endNodePlaceholder) {
        String startNodeVariable;
        if(previousFragment == null & startNodePlaceholder == null){
            startNodeVariable = "anchor";
        } else if(startNodePlaceholder == null){
            startNodeVariable = previousFragment.getNodeVariable() +
                    (iterationNumber == 0 ? "" : iterationNumber);
        } else {
            startNodeVariable = startNodePlaceholder;
        }
        return "(" + startNodeVariable + ")-[:" + relationshipType + "]->(" + endNodePlaceholder + ")";
    }

    @Override
    public boolean checkWhereCountList(int number) {
        return !whereCheck.contains(number);
    }

    @Override
    public void addIterationWhereCountList(int number) {
        whereCheck.add(number);
    }

}
