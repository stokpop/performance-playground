package nl.stokpop.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StringToJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String createJson(Set<String> parameters) {
        return parameters.stream()
            .map(StringToJson::parameterSnippit)
            .collect(Collectors.joining(",", "{", "}"));
    }

    private static class JsonParameters {
        private Map<String, Object> parameters;
        JsonParameters(Map<String,Object> parameters) {
            this.parameters = parameters;
        }
    }

    public static String createJsonJackson(Set<String> parameters) throws JsonProcessingException {
        Map<String, Object> map = parameters.stream().collect(Collectors.toMap(p -> p, p -> "${" + p + "}"));
        return objectMapper.writeValueAsString(map);
    }

    private static String parameterSnippit(String par) {
        return String.format("\"%s\": \"${%s}\"", par, par);
    }

    public static String createJsonOld(Set<String> parameters) {
        String jsonBody = "{\n";
        Integer index = 0;
        Integer numberOfParameters = parameters.size() - 1;
        for (String parameter : parameters) {
            if(index != numberOfParameters ){
                jsonBody = jsonBody + "\t\"" + parameter + "\": ${" + parameter + "},\n";
            } else {
                jsonBody = jsonBody + "\t\"" + parameter + "\": ${" + parameter + "}\n";
            }
            index++;
        }

        return jsonBody + "}";
    }



}
