package it.polimi.deib.rkm.fragments;

public interface TailFragment {
    String toCypher(int iterationNumber);
    String getCypherWithDefinition(String prefix, int iterationNumber);
    String getNodeVariable();
    String getReturnVariables(String prefix, int iterationNumber);

    String getReturnVariable(String prefix, int iterationNumber);
}
