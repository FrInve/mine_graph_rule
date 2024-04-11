package it.polimi.deib.rkm;

import it.polimi.deib.rkm.exceptions.ExceedingPatternNumberException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Pattern {
    private final Long numMin;
    private final Long numMax;
    private final Long num;
    private final PatternTail patternTail;

    private Pattern(Long numMin, Long numMax, Long num, PatternTail patternTail){
        this.numMin = numMin;
        this.numMax = numMax;
        this.num = num;
        this.patternTail = patternTail;
    }
    @SuppressWarnings("unchecked")
    public Pattern(Map<String, Object> serializedPattern){
        this.numMin = (Long) serializedPattern.get("numMin");
        this.numMax = (Long) serializedPattern.get("numMax");
        this.num = 1L;
        this.patternTail = new PatternTail((List<Map<String,String>>) serializedPattern.get("patternTail"));
    }

    @Deprecated(forRemoval = true)
    public String getNum() {
        return num.toString();
    }

    public Pattern incrementAndCopy() throws ExceedingPatternNumberException {
        if(this.num >= this.numMax){
            throw new ExceedingPatternNumberException("Pattern number exceeds maximum");
        }
        return new Pattern(this.numMin, this.numMax, this.num + 1, this.patternTail);
    }

    public String getMatchClause(String anchor) {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.num.intValue()).forEach(i -> sb.append(patternTail.getMatchClause(anchor, i)));
        return sb.toString();
    }

    public String getWithVariables(String prefix, Set<String> ignore){
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, num.intValue())
                .forEach(i -> sb.append(patternTail.getWithVariables(prefix, i, ignore)));
        return sb.toString();
    }

    public List<String> getColumnNames(String prefix, Set<String> ignore) {
        List<String> columns = new ArrayList<>();
//        columns.addAll(patternTail.getColumnNames(prefix, numMax.intValue()));
        IntStream.range(0, numMax.intValue())
                .forEach(i -> columns.addAll(patternTail.getColumnNames(prefix, i, ignore)));
        return columns;
    }

    public Stream<String> getFragmentsWhereClauses(String prefix){
        return IntStream.range(0, num.intValue())
                .mapToObj(i -> patternTail.getFragmentsWhereClauses(prefix, i))
                .flatMap(Function.identity());
    }

    public String getReturnVariables(String prefix, Set<String> ignore) {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, num.intValue())
                .forEach(i -> sb.append(patternTail.getReturnVariables(prefix, i, ignore)));
        return sb.toString();
    }

    public int getVariableCardinality(String variable){
        if(this.patternTail.containsVariable(variable)){
            return this.num.intValue();
        }
        return 0;
    }

    public void setPreviousFragments() {
        patternTail.setPreviousFragments();
    }
}

