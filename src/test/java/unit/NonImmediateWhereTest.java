package unit;

import it.polimi.deib.rkm.filtering.NonImmediateWhere;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NonImmediateWhereTest {

    /**
     * Generates an ImmediateWhere object with the following properties:
     * variable = "firstPerson"
     * variableProperty = "age"
     * otherVariable = "secondPerson"
     * otherVariableProperty = "age"
     * operand = ">"
     * @return ImmediateWhere object with firstPerson.age > secondPerson.age
     */
    NonImmediateWhere generateImmediateWhere(){
        return new NonImmediateWhere(Map.of(
                "type", "non_immediate",
                "variable", "firstPerson",
                "variableProperty", "age",
                "otherVariable", "secondPerson",
                "otherVariableProperty", "age",
                "operand", ">"
        ));
    }

    @Test
    void testGetWhereClause(){
        NonImmediateWhere nonImmediateWhere = generateImmediateWhere();
        nonImmediateWhere.setVariableCardinality(1);
        nonImmediateWhere.setOtherVariableCardinality(1);
        assertThat(nonImmediateWhere.getWhereClause()).isEqualTo("firstPerson.age > secondPerson.age");
    }

    @Test
    void testGetWhereClauseHigherCardinality1(){
        NonImmediateWhere nonImmediateWhere = generateImmediateWhere();
        nonImmediateWhere.setVariableCardinality(3);
        nonImmediateWhere.setOtherVariableCardinality(1);
        assertThat(nonImmediateWhere.getWhereClause())
                .isEqualTo("firstPerson.age > secondPerson.age AND firstPerson1.age > secondPerson.age AND firstPerson2.age > secondPerson.age");
    }
    @Test
    void testGetWhereClauseHigherCardinality2(){
        NonImmediateWhere nonImmediateWhere = generateImmediateWhere();
        nonImmediateWhere.setVariableCardinality(1);
        nonImmediateWhere.setOtherVariableCardinality(3);
        assertThat(nonImmediateWhere.getWhereClause()).isEqualTo("firstPerson.age > secondPerson.age AND firstPerson.age > secondPerson1.age AND firstPerson.age > secondPerson2.age");
    }

    @Test
    void testGetWhereClauseHigherCardinality3(){
        NonImmediateWhere nonImmediateWhere = generateImmediateWhere();
        nonImmediateWhere.setVariableCardinality(2);
        nonImmediateWhere.setOtherVariableCardinality(2);
        assertThat(nonImmediateWhere.getWhereClause()).isEqualTo("firstPerson.age > secondPerson.age AND firstPerson.age > secondPerson1.age AND firstPerson1.age > secondPerson.age AND firstPerson1.age > secondPerson1.age");
    }
}
