package it.polimi.deib.rkm.filtering;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImmediateWhere implements Where {
    private final String variable;
    private final String variableProperty;
    private int variableCardinality;
    private final String operand;
    private final String constantValue;
    public ImmediateWhere(Map<String, String> serializedWhere) {
       variable = serializedWhere.get("variable");
       variableProperty = serializedWhere.get("variableProperty");
       operand = serializedWhere.get("operand");
       constantValue = serializedWhere.get("constantValue");
    }
    @Override
    public void setVariableCardinality(int cardinality) {
        variableCardinality = cardinality;
    }

    @Override
    public void setOtherVariableCardinality(int cardinality){
        // do nothing
    }

    @Override
    public String getWhereClause(){
        return IntStream.range(0, variableCardinality)              // 0..cardinality-1
                .mapToObj(i -> i == 0 ? variable : variable + i)    // variable, variable1, variable2, ...
                .map(v -> v + "." + variableProperty)               // variable.variableProperty, ...
                .map(c -> c + " " + operand + " " + constantValue)  // variable.variableProperty operand constantValue, ...
                .collect(Collectors.joining(" AND "));     // variable.variableProperty operand constantValue AND ...
    }
}
