package it.polimi.deib.rkm;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DslParser {

    public static class ParsedDsl {
        public String name;
        public String anchor;
        public String anchorLabel;
        public String anchorWhereClause = "";
        public List<Map<String, Object>> serializedHead;
        public List<Map<String, Object>> serializedBody;
        public List<Map<String, String>> where = new ArrayList<>();
        public List<String> ignore = new ArrayList<>();
        public double support;
        public double confidence;
    }

    public static ParsedDsl parse(String dsl) {
        ParsedDsl result = new ParsedDsl();
        // Standardize whitespace
        dsl = dsl.replace("\n", " ").replace("\r", " ").replace("\t", " ").replaceAll("\\s+", " ").trim();

        // 1. Name
        Matcher nameMatcher = Pattern.compile("MINE GRAPH RULE\\s+(\\w+)").matcher(dsl);
        if (nameMatcher.find()) {
            result.name = nameMatcher.group(1);
        }

        // 2. Grouping / Anchor
        Matcher groupingMatcher = Pattern.compile("GROUPING ON\\s+\\((\\w+):(\\w+)\\)(?:\\s+WHERE\\s+(.*?))?\\s+DEFINING").matcher(dsl);
        if (groupingMatcher.find()) {
            result.anchor = groupingMatcher.group(1);
            result.anchorLabel = groupingMatcher.group(2);
            if (groupingMatcher.group(3) != null) {
                result.anchorWhereClause = groupingMatcher.group(3).trim();
            }
        }

        // 3. Defining Body and Head
        Matcher definingMatcher = Pattern.compile("DEFINING BODY AS\\s+(.*?)\\s+HEAD AS\\s+(.*?)(?=\\s+WHERE|\\s+IGNORE|\\s+EXTRACTING|$)").matcher(dsl);
        if (definingMatcher.find()) {
            String bodyStr = definingMatcher.group(1).trim();
            String headStr = definingMatcher.group(2).trim();
            result.serializedBody = parseItemset(bodyStr, result.anchor);
            result.serializedHead = parseItemset(headStr, result.anchor);
            
            String remaining = dsl.substring(definingMatcher.end()).trim();
            
            // Optional WHERE (global)
            Matcher whereMatcher = Pattern.compile("^WHERE\\s+(.*?)(?=\\s+IGNORE|\\s+EXTRACTING|$)").matcher(remaining);
            if (whereMatcher.find()) {
                result.where = parseWhereConditions(whereMatcher.group(1).trim());
                remaining = remaining.substring(whereMatcher.end()).trim();
            }
            
            // Optional IGNORE
            Matcher ignoreMatcher = Pattern.compile("^IGNORE\\s+(.*?)(?=\\s+EXTRACTING|$)").matcher(remaining);
            if (ignoreMatcher.find()) {
                result.ignore = Arrays.stream(ignoreMatcher.group(1).trim().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                remaining = remaining.substring(ignoreMatcher.end()).trim();
            }
            
            // 6. Support and Confidence
            Matcher metricsMatcher = Pattern.compile("EXTRACTING RULES WITH SUPPORT\\s*>\\s*([\\d.]+)\\s+AND CONFIDENCE\\s*>\\s*([\\d.]+)").matcher(remaining);
            if (metricsMatcher.find()) {
                result.support = Double.parseDouble(metricsMatcher.group(1));
                result.confidence = Double.parseDouble(metricsMatcher.group(2));
            }
        }

        return result;
    }

    private static List<Map<String, Object>> parseItemset(String itemsetStr, String anchorVar) {
        List<Map<String, Object>> itemset = new ArrayList<>();
        List<String> patternStrings = splitItemset(itemsetStr);
        for (String ps : patternStrings) {
            itemset.add(parsePattern(ps, anchorVar));
        }
        return itemset;
    }

    private static List<String> splitItemset(String s) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '{' || c == '[') depth++;
            if (c == ')' || c == '}' || c == ']') depth--;
            if (c == ',' && depth == 0) {
                result.add(sb.toString().trim());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) result.add(sb.toString().trim());
        return result;
    }

    private static Map<String, Object> parsePattern(String pStr, String anchorVar) {
        Map<String, Object> pattern = new HashMap<>();
        
        long numMin = 1;
        long numMax = 1;
        Matcher cardMatcher = Pattern.compile("^(\\d+)\\.\\.(\\d+)").matcher(pStr);
        if (cardMatcher.find()) {
            numMin = Long.parseLong(cardMatcher.group(1));
            numMax = Long.parseLong(cardMatcher.group(2));
            pStr = pStr.substring(cardMatcher.end()).trim();
        }
        pattern.put("numMin", numMin);
        pattern.put("numMax", numMax);

        Matcher aliasMatcher = Pattern.compile("AS\\s+(\\w+)$").matcher(pStr);
        if (aliasMatcher.find()) {
            pattern.put("patternAlias", aliasMatcher.group(1));
            pStr = pStr.substring(0, aliasMatcher.start()).trim();
        }

        if (pStr.startsWith("(" + anchorVar + ")")) {
            pStr = pStr.substring(anchorVar.length() + 2).trim();
        }

        pattern.put("patternTail", parsePatternTail(pStr));

        return pattern;
    }

    private static List<Map<String, String>> parsePatternTail(String tailStr) {
        List<Map<String, String>> tail = new ArrayList<>();
        
        while (!tailStr.trim().isEmpty()) {
            Map<String, String> fragment = new HashMap<>();
            
            // Try matching each rel pattern type
            // 1. Any: -[]->{1, n}
            Matcher anyM = Pattern.compile("^\\s*-\\[\\]->\\{(\\d+)(?:,\\s*(\\d+))?\\}").matcher(tailStr);
            if (anyM.find()) {
                fragment.put("type", "any");
                fragment.put("relationshipType", "ANY");
                String length = anyM.group(2) != null ? anyM.group(2) : anyM.group(1);
                fragment.put("relationshipLength", length);
                tailStr = tailStr.substring(anyM.end()).trim();
            } else {
                // 2 & 3. Normal or Count: -[:REL]-> or -[:REL>=n]->
                Matcher relM = Pattern.compile("^\\s*-\\[:?(\\w+)(?:>=(\\d+))?\\](?:->)?").matcher(tailStr);
                if (relM.find()) {
                    String relType = relM.group(1);
                    String minVal = relM.group(2);
                    if (minVal != null) {
                        fragment.put("type", "count");
                        fragment.put("minValue", minVal);
                    } else {
                        fragment.put("type", "normal");
                    }
                    fragment.put("relationshipType", relType);
                    tailStr = tailStr.substring(relM.end()).trim();
                }
            }

            // Match following node: (variable:Label)
            Matcher nodeM = Pattern.compile("^\\s*\\((\\w+):\\s*(\\w+)\\)").matcher(tailStr);
            if (nodeM.find()) {
                fragment.put("nodeVariable", nodeM.group(1));
                fragment.put("nodeLabel", nodeM.group(2));
                tailStr = tailStr.substring(nodeM.end()).trim();
            }
            
            if (fragment.isEmpty()) {
                break;
            }
            tail.add(fragment);
        }
        
        return tail;
    }

    private static List<Map<String, String>> parseWhereConditions(String conditions) {
        List<Map<String, String>> result = new ArrayList<>();
        String[] parts = conditions.split("\\s+AND\\s+");
        for (String part : parts) {
            Map<String, String> w = new HashMap<>();
            // Non-Immediate: v1.prop1 operand v2.prop2
            Matcher nonImmMatcher = Pattern.compile("(\\w+)\\.(\\w+)\\s*([<>=!]+)\\s*(\\w+)\\.(\\w+)").matcher(part);
            if (nonImmMatcher.find()) {
                w.put("variable", nonImmMatcher.group(1));
                w.put("variableProperty", nonImmMatcher.group(2));
                w.put("operand", nonImmMatcher.group(3));
                w.put("otherVariable", nonImmMatcher.group(4));
                w.put("otherVariableProperty", nonImmMatcher.group(5));
            } else {
                // Immediate: v.prop operand constant
                Matcher immMatcher = Pattern.compile("(\\w+)\\.(\\w+)\\s*([<>=!]+)\\s*(?:'([^']*)'|(\\d+))").matcher(part);
                if (immMatcher.find()) {
                    w.put("variable", immMatcher.group(1));
                    w.put("variableProperty", immMatcher.group(2));
                    w.put("operand", immMatcher.group(3));
                    String val = immMatcher.group(4) != null ? immMatcher.group(4) : immMatcher.group(5);
                    w.put("constantValue", val);
                }
            }
            if (!w.isEmpty()) {
                result.add(w);
            }
        }
        return result;
    }
}