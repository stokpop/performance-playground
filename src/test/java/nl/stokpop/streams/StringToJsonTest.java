package nl.stokpop.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringToJsonTest {

    @Test
    void createJson() {
        Set<String> params = Set.of("text", "123", "bla", "foo");
        String json = StringToJson.createJson(params);
        String json2 = StringToJson.createJsonOld(params);
        assertEquals(json, json2);
    }
    @Test
    void createJsonJackson() throws JsonProcessingException {
        Set<String> params = Set.of("text", "123", "bla", "foo");
        String json = StringToJson.createJson(params);
        String json2 = StringToJson.createJsonJackson(params);
        assertEquals(json, json2);
    }
}