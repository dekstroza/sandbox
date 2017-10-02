import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JsonPathTest {

    @Test
    public void doTestSomething() throws IOException {

        DocumentContext ctx = JsonPath.parse(JsonPathTest.class.getResourceAsStream("github.json"));
        List<String> nameList = ctx.read("$.items[*].full_name", List.class);
        List<String> descList = ctx.read("$.items[?(@.full_name == 'component/reactive')].description", List.class);
        System.out.println("nameList = " + nameList);
        System.out.println("descList = " + descList);

    }
}
