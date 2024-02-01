package unit;

import it.polimi.deib.rkm.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryTest {

    @Test
    void shouldGenerateBody(){
//        static final Map<Integer, String> MY_MAP = Map.of(
//                1, "one",
//                2, "two"
//        );


//        Query query = new Query("P",
//                "Person",
//                {Map.of()});
//
//        itemMap.put("type", "normal");
//        itemMap.put("rel_type", "BUY");
//        itemMap.put("rel_alias", "buy");
//        itemMap.put("end_node", "Book");
//        itemMap.put("end_node_alias", "b");

        String actual = "";
        String expected = "";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldGenerateRule(){

        String actual = "";
        String expected = "";
        assertThat(actual).isEqualTo(expected);

    }
}
