package unit;

import it.polimi.deib.rkm.filtering.ImmediateWhere;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ImmediateWhereTest {

    /**
     * Generates an ImmediateWhere object with the following properties:
     * variable = "person"
     * variableProperty = "age"
     * constantValue = "18"
     * operand = ">"
     * @return ImmediateWhere object with person.age > 18
     */
    ImmediateWhere generateImmediateWhere(){
        return new ImmediateWhere(Map.of(
                "type", "immediate",
                "variable", "person",
                "variableProperty", "age",
                "constantValue", "18",
                "operand", ">"
        ));
    }

    @Test
    void testGetWhereClause(){
        ImmediateWhere immediateWhere = generateImmediateWhere();
        immediateWhere.setVariableCardinality(1);
        assertThat(immediateWhere.getWhereClause()).isEqualTo("person.age > 18");
    }
    @Test

    void testGetWhereClauseHigherCardinality(){
        ImmediateWhere immediateWhere = generateImmediateWhere();
        immediateWhere.setVariableCardinality(4);
        assertThat(immediateWhere
                .getWhereClause())
                .isEqualTo("person.age > 18 AND person1.age > 18 AND person2.age > 18 AND person3.age > 18");
    }
}
