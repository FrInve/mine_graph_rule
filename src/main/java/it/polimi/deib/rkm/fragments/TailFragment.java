package it.polimi.deib.rkm.fragments;

public interface TailFragment {
    String toCypher(int iterationNumber);
    String getCypherReturnDefinition(String prefix, int iterationNumber);
}
