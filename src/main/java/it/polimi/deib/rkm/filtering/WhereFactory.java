package it.polimi.deib.rkm.filtering;

import java.util.Map;

public interface WhereFactory {
    static Where createWhere(Map<String, String> serializedWhere) {
        if (serializedWhere.containsKey("value")) {
            return new ImmediateWhere(serializedWhere);
        } else {
            return new NonImmediateWhere(serializedWhere);
        }
    }
}
