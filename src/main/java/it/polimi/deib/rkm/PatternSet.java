package it.polimi.deib.rkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public String getMatchClause(String anchor, String anchorType){
        StringBuilder sb = new StringBuilder();
        patterns.forEach(pattern -> sb.append(pattern.getMatchClause(anchor, anchorType)));
        return sb.toString();
    }

    public abstract String getReturnVariables();
}
