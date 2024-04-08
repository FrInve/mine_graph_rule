package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PatternSet {
    private List<Pattern> patterns;

    public PatternSet(List<Map<String, Object>> serializedPatterns){
        this.patterns = new ArrayList<>();
        for(Map<String, Object> pattern : serializedPatterns){
            this.patterns.add(new Pattern(pattern));
        }
    }

    public PatternSet(){
        this.patterns = null;
    }

    public void setPatterns(List<Pattern> patterns){
        this.patterns = patterns;
    }

    public List<Pattern> getPatterns(){
        return patterns;
    }

    public abstract PatternSet cloneAndReplacePattern(int index, Pattern pattern);

    public String getMatchClause(String anchor){
        StringBuilder sb = new StringBuilder();
        patterns.forEach(pattern -> sb.append(pattern.getMatchClause(anchor)));
        return sb.toString();
    }

    public abstract String getWithVariables(Set<String> ignore);
    public abstract String getReturnVariables(Set<String> ignore);

    public abstract List<String> getColumnNames(String prefix, Set<String> ignore);

}


