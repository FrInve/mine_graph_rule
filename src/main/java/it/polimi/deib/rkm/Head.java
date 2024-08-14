package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Head extends PatternSet {

    public Head(List<Map<String, Object>> serializedHead) {
        super(serializedHead);
    }

    public Head(){
        super();
    }

    @Override
    public Head cloneAndReplacePattern(int index, Pattern pattern) {
        List<Pattern> newPatterns = new ArrayList<>(this.getPatterns());
        newPatterns.set(index, pattern);
        Head patternSet = new Head();
        patternSet.setPatterns(newPatterns);
        return patternSet;
    }

    @Override
    public String getWithVariables(Set<String> ignore){
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> sb.append(this.getPatterns().get(i).getWithVariables("head" + i, ignore)));
        return sb.toString();
    }

    @Override
    public String getReturnVariables(Set<String> ignore){
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> sb.append(this.getPatterns().get(i).getReturnVariables("head" + i, ignore)));
        return sb.toString();
    }

    @Override
    public Stream<String> getFragmentsWhereClauses(){
        return this.patterns.stream().flatMap(p -> p.getFragmentsWhereClauses("head"));
    }


    @Override
    public String getCountWhereClauses(Set<String> ignore){
        StringBuilder sb = new StringBuilder();
        this.patterns.forEach(p -> sb.append(p.getCountWhereClause(ignore)));
        return sb.toString();
    }

    @Override
    public List<String> getColumnNames(String prefix, Set<String> ignore) {
        List<String> columns = new ArrayList<>();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> columns.addAll(this.getPatterns().get(i).getColumnNames(prefix + i, ignore)));
        return columns;
    }
}
