package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    public String getWithVariables(){
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> sb.append(this.getPatterns().get(i).getWithVariables("head" + i)));
        return sb.toString();
    }

    @Override
    public String getReturnVariables(){
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> sb.append(this.getPatterns().get(i).getReturnVariables("head" + i)));
        return sb.toString();
    }

    @Override
    public List<String> getColumnNames(String prefix) {
        List<String> columns = new ArrayList<>();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> columns.addAll(this.getPatterns().get(i).getColumnNames(prefix + i)));
        return columns;
    }
}
