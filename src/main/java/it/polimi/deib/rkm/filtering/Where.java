package it.polimi.deib.rkm.filtering;

public interface Where {
    String getVariable();
    String getOtherVariable();
    void setVariableCardinality(int cardinality);
    void setOtherVariableCardinality(int cardinality);
    String getWhereClause();
}
