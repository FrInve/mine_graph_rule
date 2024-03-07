package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Body extends PatternSet {
    public Body(List<Map<String, Object>> serializedBody) {
        super(serializedBody);
    }

    public Body(){
        super();
    }

    @Override
    public Body cloneAndReplacePattern(int index, Pattern pattern) {
        List<Pattern> newPatterns = new ArrayList<>(this.getPatterns());
        newPatterns.set(index, pattern);
        Body patternSet = new Body();
        patternSet.setPatterns(newPatterns);
        return patternSet;
    }

    @Override
    public String getReturnVariables() {
        StringBuilder sb = new StringBuilder();
        IntStream.range(0, this.getPatterns().size())
                .forEach(i -> sb.append(this.getPatterns().get(i).getReturnVariables("body" + i)));
        return sb.toString();
    }
}