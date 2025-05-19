package it.polimi.deib.rkm.fragments;

public interface TailFragment {
    default void setPreviousFragment(TailFragment previousFragment){
        // Do nothing
    }
    String toCypher(int iterationNumber);
    String getCypherWithDefinition(String prefix, int iterationNumber, String patternAlias);
    String getNodeVariable();
    String getNodeLabel();
    String getRelationshipType();
    String getReturnVariable(String prefix, int iterationNumber, String patternAlias);

    default String getWhereClause(String prefix, int iterationNumber) {
        return null;
    }

    default String getWhereCountClause(int iterationNumber, String startNodePlaceholder,String endNodePlaceholder) {
        return null;
    }

    default boolean checkWhereCountList(int number) {
        return false;
    }

    default void addIterationWhereCountList(int number){
        // Do nothing
    }

    default int getMinValue() {
        return 1;
    }
}
