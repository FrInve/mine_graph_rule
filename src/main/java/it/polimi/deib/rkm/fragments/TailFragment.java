package it.polimi.deib.rkm.fragments;

public interface TailFragment {
    String toCypher(int iterationNumber);
    String getCypherReturnDefinition(String prefix, int iterationNumber);

    String getReturnVariables(String prefix, int iterationNumber);

    String getReturnVariable(String prefix, int iterationNumber);
}
