package it.polimi.deib.rkm.filtering;

public interface Where {
    void setVariableCardinality(int cardinality);
    void setOtherVariableCardinality(int cardinality);
    String getWhereClause();
}
