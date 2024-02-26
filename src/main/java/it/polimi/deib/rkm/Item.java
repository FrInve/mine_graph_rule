package it.polimi.deib.rkm;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class Item {


    private final ItemPath itemPath;
    private final int numMax;
    private final int numMin;


    public Item(Map<String, Object> item, ItemPath.ItemType itemMode) {
        this.numMax = ((Long) item.get("num_max")).intValue();
        this.numMin = ((Long) item.get("num_min")).intValue();
        this.itemPath = new ItemPath((List<Map<String, String>>) item.get("item_path"),
                itemMode);
    }
    /**
     * This method returns the match clause of the item, with the correct variables for the current size.
     * Note that numMin < currentSize < numMax.
     * EG: if currentSize = 0, the match clause will be "MATCH (alias)-[buy:BUY]-(b:Book)"
     * EG: if currentSize = 1, the match clause will be "MATCH (alias)-[buy1:BUY]-(b1:Book)"
     * @param currentSize the current size of the item.
     * @return the match clause of the item
     */
    public String toCypherMatch(int currentSize) {
        return "MATCH (alias)" + this.itemPath.toCypher(currentSize);
    }

    public String toCypherMatch() {
        return this.toCypherMatch(0);
    }

    public String toCypherReturn(String otherAlias) {
        StringBuilder builder = new StringBuilder();
        for(int i=this.numMin; i<=this.numMax; i++){
                for(int j=0; j<i; j++){
                    builder.append(this.toCypherMatch(j)).append("\n"); // MATCH (alias)-[buy:BUY]-(b:Book)
                }                                                       // MATCH (alias)-[buy1:BUY]-(b1:Book) ...
            if(otherAlias.isEmpty()) {
                builder.append("RETURN size(collect(DISTINCT alias)) as suppcount, ")
                        .append(this.itemPath.getStringVariable(i-1));    // RETURN ...
            } else {
                builder.append("RETURN size(collect(DISTINCT alias)) as suppcount, ")
                        .append(otherAlias).append(", ")
                        .append(this.itemPath.getStringVariable(i-1));    // RETURN ...
            }
            if(i!=this.numMax){
                builder.append("\nUNION\n");
            }
        }
//        if (otherAlias.isEmpty()) {
//            return toCypherMatch(0) + "\nRETURN size(collect(DISTINCT alias)) as suppcount, " + this.itemPath.getStringVariable();}
//        else {
//            return toCypherMatch(0) + "\nRETURN size(collect(DISTINCT alias)) as suppcount, " + otherAlias + ", " + this.itemPath.getStringVariable();}
        return builder.toString();
    }

    public String toCypherWith(String otherAlias, String alias, String aliasNode) {
        StringBuilder builder = new StringBuilder();
        builder.append("CALL{\n")
                .append("MATCH (").append(alias).append(":").append(aliasNode).append(")\n")
                .append("WITH ").append(alias).append(" as alias\n");
        for(int i=this.numMin; i<=this.numMax; i++){
            for(int j=0; j<i; j++){
                builder.append(this.toCypherMatch(j)).append("\n"); // MATCH (alias)-[buy:BUY]-(b:Book)
            }                                                       // MATCH (alias)-[buy1:BUY]-(b1:Book) ...
            if (otherAlias.isEmpty()) {
                builder.append("RETURN alias as nestedAlias,")
                        .append(this.itemPath.getStringVariable(i-1));    // WITH ...
            } else {
                builder.append("RETURN alias as nestedAlias, ")
                        .append(otherAlias).append(", ")
                        .append(this.itemPath.getStringVariable(i-1));    // WITH ...
            }
            if(i!=this.numMax){
                builder.append("\nUNION\n");
            }
        }
        if (otherAlias.isEmpty()) {
            builder.append("}\nWITH nestedAlias as alias, ").append(this.itemPath.getStringAlias());}
        else {
            builder.append("}\nWITH nestedAlias as alias, ").append(otherAlias).append(", ")
                    .append(this.itemPath.getStringAlias());}
        return builder.toString();
    }

    public String getAliasString(){
        return this.itemPath.getStringAlias();
    }
}
