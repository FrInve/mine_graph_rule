package it.polimi.deib.rkm;

import it.polimi.deib.rkm.fragments.Normal;
import it.polimi.deib.rkm.fragments.TailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatternTail {
    private List<TailFragment> tail;

    public PatternTail(List<Map<String, String>> tail){
        this.tail = new ArrayList<>();
        for (Map<String, String> fragment : tail) {
            switch(fragment.get("type")) {
                case "normal" -> this.tail.add(new Normal(fragment));
                default -> throw new IllegalArgumentException("Unknown fragment type: " + fragment.get("type"));
            }
        }
    }

    public String getMatchClause(String anchor, String anchorType, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(anchor).append(":").append(anchorType).append(")");
        tail.forEach(fragment -> sb.append(fragment.toCypher(i)));
        sb.append(", ");
//        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public String getReturnVariables(String prefix, int i){
        StringBuilder sb = new StringBuilder();
        tail.forEach(fragment -> sb.append(fragment.getCypherReturnDefinition(prefix, i)).append(", "));
        return sb.toString();
    }
}
