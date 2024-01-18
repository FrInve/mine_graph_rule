package it.polimi.deib.rkm;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.neo4j.procedure.Description;
import org.neo4j.procedure.Procedure;
import org.neo4j.graphdb.Node;

public class MineGraphRule {

    @Procedure(name = "mineGraphRule")
    @Description("foo bar")
    public void mineGraphRule(
            // Input parameters here
    ) {
        // Procedure logic here
        return;
    }

    public static final class MyRecord {

        public final Node node;

        MyRecord(Node node) {
            this.node = node;
        }
    }
}


