package it.polimi.deib.rkm.filtering;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NonImmediateWhere implements Where{
    private final String variable;
    private final String variableProperty;
    private int variableCardinality;
    private final String otherVariable;
    private final String otherVariableProperty;
    private int otherVariableCardinality;
    private final String operand;
    public NonImmediateWhere(Map<String, String> serializedWhere) {
        variable = serializedWhere.get("variable");
        variableProperty = serializedWhere.get("variableProperty");
        otherVariable = serializedWhere.get("otherVariable");
        otherVariableProperty = serializedWhere.get("otherVariableProperty");
        operand = serializedWhere.get("operand");
    }

    @Override
    public String getVariable(){
        return variable;
    }
    @Override
    public String getOtherVariable(){
        return otherVariable;
    }

    @Override
    public void setVariableCardinality(int variableCardinality) {
        this.variableCardinality = variableCardinality;
    }

    @Override
    public void setOtherVariableCardinality(int otherVariableCardinality) {
        this.otherVariableCardinality = otherVariableCardinality;
    }

    @Override
    public String getWhereClause(){
        return IntStream.range(0, variableCardinality)              // 0..cardinality-1
                .mapToObj(i -> i == 0 ? variable : variable + i)    // variable, variable1, variable2, ...
                .map(v -> v + "." + variableProperty)               // variable.variableProperty, ...
                .flatMap(v1 -> getOtherVariableStream()
                        .map(v2 -> v1 + " " + operand + " " + v2 ))  // variable.variableProperty operand constantValue, ...
                .collect(Collectors.joining(" AND "));     // variable.variableProperty operand constantValue AND ...
    }

    private Stream<String> getOtherVariableStream(){
        return IntStream.range(0, otherVariableCardinality)              // 0..cardinality-1
                .mapToObj(i -> i == 0 ? otherVariable : otherVariable + i)    // variable, variable1, variable2, ...
                .map(v -> v + "." + otherVariableProperty);               // variable.variableProperty, ...
    }
}
