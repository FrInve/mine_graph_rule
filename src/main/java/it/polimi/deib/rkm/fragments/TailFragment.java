package it.polimi.deib.rkm.fragments;

public interface TailFragment {
    default void setPreviousFragment(TailFragment previousFragment){
        // Do nothing
    }
    String toCypher(int iterationNumber);
    String getCypherWithDefinition(String prefix, int iterationNumber);
    String getNodeVariable();
    String getReturnVariable(String prefix, int iterationNumber);

    default String getWhereClause(String prefix, int iterationNumber) {
        return null;
    }
}
