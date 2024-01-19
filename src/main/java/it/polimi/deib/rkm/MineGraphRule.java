package it.polimi.deib.rkm;

import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.Mode;
import org.neo4j.graphdb.Node;

public class MineGraphRule {

    @Procedure(name = "rkm.mineGraphRule", mode=Mode.READ)
    @Description("Association Rule Mining for Neo4j")
    public Stream<AssociationRule> mineGraphRule(
            // Input parameters here
            @Name("alias")      String alias,
            @Name("alias_node") String alias_node,
            @Name("item_head")   List<String> item_head,
            @Name("item_body")   List<String> item_body,
            @Name("support")    Number support,
            @Name("confidence") Number confidence
            ) {
        // Procedure logic here
        return Stream.of(new AssociationRule(alias));
    }

    public static final class AssociationRule {
        public String alias;

        public AssociationRule(String alias){
            this.alias = alias;
        }
    }
}


