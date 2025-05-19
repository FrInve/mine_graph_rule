package it.polimi.deib.rkm;

import it.polimi.deib.rkm.fragments.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
                case "count" -> this.tail.add(new Count(fragment));
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

    public String getWithVariables(String prefix, int i, Set<String> ignore, String patternAlias){
        StringBuilder sb = new StringBuilder();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> sb.append(fragment.getCypherWithDefinition(prefix, i, patternAlias)).append(", "));
        return sb.toString();
    }

    public List<String> getColumnNames(String prefix, int i, Set<String> ignore, String patternAlias) {
        List<String> columns = new ArrayList<>();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> columns.add(fragment.getReturnVariable(prefix, i, patternAlias)));
        return columns;
    }

    public Stream<String> getFragmentsWhereClauses(String prefix, int i){
        return tail.stream().map(f -> f.getWhereClause(prefix, i));
    }

    public String getReturnVariables(String prefix, int i, Set<String> ignore, String patternAlias) {
        StringBuilder sb = new StringBuilder();
        tail.stream().filter(fragment -> !ignore.contains(fragment.getNodeVariable()))
                .forEach(fragment -> sb.append(fragment.getReturnVariable(prefix, i, patternAlias)).append(", "));
        return sb.toString();
    }

    public boolean containsVariable(String variable){
        return tail.stream().anyMatch(fragment -> fragment.getNodeVariable().equals(variable));
    }

    public void setPreviousFragments(){
        for (int i = 0; i < tail.size(); i++) {
            if (i > 0) {
                tail.get(i).setPreviousFragment(tail.get(i - 1));
            }
        }
    }

    public String getWhereCountCondition(int startingPoint, String startNodePlaceholder, int i, Set<String> ignore){
        String whereCountCondition = "";
        for (int j = startingPoint; j < tail.size(); j++) {
            if (tail.get(j) instanceof Count & tail.get(j).checkWhereCountList(i)){
                if (!ignore.contains(tail.get(j).getNodeVariable())) {
                    String newWhereCountCondition =  "COUNT{" + tail.get(j).getWhereCountClause(i, startNodePlaceholder, tail.get(j).getNodeVariable()+ (i == 0 ? "" : i)) +
                            "}>=" + tail.get(j).getMinValue();
                    if (whereCountCondition.isEmpty()) {
                        whereCountCondition = newWhereCountCondition;
                    } else {
                        whereCountCondition = whereCountCondition + " AND " + newWhereCountCondition;
                    }
                }
                else {
                    String whereExistCondition = "";
                    String placeholder =  tail.get(j).getNodeVariable()  + (i == 0 ? "" : i)  + "Placeholder" + j;
                    if (j+1<tail.size()){
                        if (tail.get(j+1) instanceof Count){
                            whereExistCondition = "WHERE " + getWhereCountCondition(j+1, placeholder, i, ignore);
                        }
                        else {
                            whereExistCondition = "WHERE EXISTS{(" + placeholder + ")";
                            boolean ignoreStep = true;
                            for (int z = j + 1; z < tail.size() & ignoreStep; z++) {
                                if (!ignore.contains(tail.get(z).getNodeVariable())) {
                                    if (z+1>=tail.size()){
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + tail.get(z).getNodeVariable() + (i == 0 ? "" : i) + ")";
                                    } else if (tail.get(z + 1) instanceof Count) {
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + tail.get(z).getNodeVariable() + (i == 0 ? "" : i) + ")";
                                        whereExistCondition = whereExistCondition + " WHERE " + getWhereCountCondition(z + 1, tail.get(z).getNodeVariable() + (i == 0 ? "" : i), i, ignore);
                                    } else {
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + tail.get(z).getNodeVariable() + (i == 0 ? "" : i) + ")";
                                    }
                                    ignoreStep = false;
                                } else {
                                    if (z+1>= tail.size()){
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + tail.get(z).getNodeLabel() + ")";
                                    } else if (tail.get(z + 1) instanceof Count) {
                                        String placeholderExist = tail.get(z).getNodeVariable() + (i == 0 ? "" : i) + "PlaceholderInnested" + z;
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + placeholderExist + ":" + tail.get(z).getNodeLabel() + ")";
                                        whereExistCondition = whereExistCondition + " WHERE " + getWhereCountCondition(z + 1, placeholderExist, i, ignore);
                                        ignoreStep = false;
                                    } else {
                                        whereExistCondition = whereExistCondition + "-[:" + tail.get(z).getRelationshipType() +
                                                "]->(" + tail.get(z).getNodeLabel() + ")";
                                    }
                                }
                            }
                            whereExistCondition = whereExistCondition + "}";
                        }
                    }
                    String newWhereCountCondition = "COUNT{" + tail.get(j).getWhereCountClause(i, startNodePlaceholder, placeholder + ":" +
                            tail.get(j).getNodeLabel()) + " " + whereExistCondition +
                            "}>=" + tail.get(j).getMinValue();
                    if (whereCountCondition.isEmpty()) {
                        whereCountCondition = newWhereCountCondition;
                    } else {
                        whereCountCondition = whereCountCondition + " AND " + newWhereCountCondition;
                    }
                }
                tail.get(j).addIterationWhereCountList(i);
            }
        }

        return whereCountCondition;
    }

}
