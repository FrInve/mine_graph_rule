package it.polimi.deib.rkm;

import it.polimi.deib.rkm.filtering.Where;
import it.polimi.deib.rkm.filtering.WhereFactory;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;

import java.util.*;
import java.util.stream.Collectors;

import java.util.Map;

public class Query {
    private final String anchor;
    private final String anchorLabel;
    private final String anchorWhereClause;
    private final PatternSet body;
    private final PatternSet head;
    private final List<Where> where;
    private final Set<String> ignore;
    private final double support;
    private final double confidence;

    public Query(
            String anchor,
            String anchorLabel,
            String anchorWhereClause,
            List<Map<String, Object>> serializedHead,
            List<Map<String, Object>> serializedBody,
            List<Map<String, String>> where,
            List<String> ignore,
            double support,
            double confidence,
            Map<String, String> propertiesMap
    ) {
        this.anchor = anchor;
        this.anchorLabel = anchorLabel;
        this.anchorWhereClause = anchorWhereClause;

        List<Map<String, Object>> modifiedBody = modifySerializedPatternSet(serializedBody, propertiesMap);
        List<Map<String, Object>> modifiedHead = modifySerializedPatternSet(serializedHead, propertiesMap);
        this.head = new Head(modifiedHead);
        this.body = new Body(modifiedBody);
        this.where = new ArrayList<>();
        where.forEach(w -> this.where.add(WhereFactory.createWhere(w)));
        this.ignore = new HashSet<>();
        this.ignore.addAll(ignore);
        this.support = support;
        this.confidence = confidence;
    }

    public String getAnchor(){
        return this.anchor;
    }

    public String getAnchorLabel(){
        return this.anchorLabel;
    }

    public String getAnchorWhereClause(){
        StringBuilder sb = new StringBuilder();
//        sb.append("WHERE ")
                sb.append(this.anchorWhereClause);
        return sb.toString();
    }

    public PatternSet getBody(){
        return this.body;
    }

    public PatternSet getHead(){
        return this.head;
    }

    public Set<String> getIgnore(){
        return this.ignore;
    }

    public List<Where> getWhere(){
        return this.where;
    }
    public List<String> getRuleColumnNames(){
        List<String> columns = new ArrayList<>();
        columns.addAll(head.getColumnNames("head", ignore, false));
        columns.addAll(body.getColumnNames("body", ignore, false));
        columns.add("suppcount");
        return columns;
    }

    public Map<String, String> getColumnsNewNames(){
        Map<String, String> columnsName = new HashMap<>();

        List<String> head_keys = new ArrayList<>(head.getColumnNames("head", ignore, false));
        List<String> head_values = new ArrayList<>(head.getColumnNames("head", ignore, true));


        for (int i = 0; i < head_keys.size(); i++) {
            if (!head_keys.get(i).equals(head_values.get(i))) {
                columnsName.put(head_keys.get(i), head_values.get(i));
            }
        }

        List<String> body_keys = new ArrayList<>(body.getColumnNames("body", ignore, false));
        List<String> body_values = new ArrayList<>(body.getColumnNames("body", ignore, true));


        for (int i = 0; i < body_keys.size(); i++) {
            if (!body_keys.get(i).equals(body_values.get(i))) {
                columnsName.put(body_keys.get(i), body_values.get(i));
            }
        }
        return columnsName;
    }

    public List<String> getBodyColumnNames(){
        List<String> columns = new ArrayList<>(body.getColumnNames("body", ignore, false));
        columns.add("suppcount");
        return columns;
    }

    public double getSupport() {
        return support;
    }

    public double getConfidence() {
        return confidence;
    }

    private List<Map<String, Object>> modifySerializedPatternSet(List<Map<String, Object>> body, Map<String, String> propertiesMap) {
        return body.stream().map(pattern -> {
            if (pattern.containsKey("patternTail")) {
                Object tailObj = pattern.get("patternTail");

                if (tailObj instanceof List) {
                    List<?> tailList = (List<?>) tailObj;
                    List<Map<String, Object>> modifiedTail = tailList.stream()
                            .map(entry -> {
                                if (entry instanceof Map) {
                                    Map<String, Object> tailEntry = new HashMap<>((Map<String, Object>) entry);
                                    Object labelObj = tailEntry.get("nodeLabel");
                                    if (labelObj instanceof String) {
                                        String label = (String) labelObj;
                                        if (propertiesMap.containsKey(label)) {
                                            tailEntry.put("nodeProperty", propertiesMap.get(label));
                                        } else {
                                            tailEntry.put("nodeProperty", "");
                                        }
                                    } else {
                                        tailEntry.put("nodeProperty", "");
                                    }
                                    return tailEntry;
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    pattern.put("patternTail", modifiedTail);
                }
            }
            return pattern;
        }).collect(Collectors.toList());
    }





}