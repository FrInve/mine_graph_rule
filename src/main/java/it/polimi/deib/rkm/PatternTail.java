package it.polimi.deib.rkm;

import it.polimi.deib.rkm.fragments.Normal;
import it.polimi.deib.rkm.fragments.Reverse;
import it.polimi.deib.rkm.fragments.Any;
import it.polimi.deib.rkm.fragments.AnyReverse;
import it.polimi.deib.rkm.fragments.TailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PatternTail {
    private final List<TailFragment> tail;

    public PatternTail(List<Map<String, String>> tail){
        this.tail = new ArrayList<>();
        for (Map<String, String> fragment : tail) {
            switch(fragment.get("type")) {
                case "normal" -> this.tail.add(new Normal(fragment));
                case "reverse" -> this.tail.add(new Reverse(fragment));
                case "any" -> this.tail.add(new Any(fragment));
                case "anyReverse" -> this.tail.add(new AnyReverse(fragment));
                default -> throw new IllegalArgumentException("Unknown fragment type: " + fragment.get("type"));
            }
        }
    }

    public String getMatchClause(String anchor, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(anchor).append(")");
//        sb.append("(").append(anchor).append(")");
        tail.forEach(fragment -> sb.append(fragment.toCypher(i)));
        sb.append(", ");
//        sb.delete(sb.length() - 2, sb.length());
        return sb.toString();
    }

    public String getWithVariables(String prefix, int i, Set<String> ignore){
        StringBuilder sb = new StringBuilder();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> sb.append(fragment.getCypherWithDefinition(prefix, i)).append(", "));
        return sb.toString();
    }

    public List<String> getColumnNames(String prefix, int i, Set<String> ignore) {
        List<String> columns = new ArrayList<>();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> columns.add(fragment.getReturnVariable(prefix, i)));
        return columns;
    }

    public String getReturnVariables(String prefix, int i, Set<String> ignore) {
        StringBuilder sb = new StringBuilder();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> sb.append(fragment.getReturnVariable(prefix, i)).append(", "));
        return sb.toString();
    }

    public boolean containsVariable(String variable){
        return tail.stream().anyMatch(fragment -> fragment.getNodeVariable().equals(variable));
    }
}
