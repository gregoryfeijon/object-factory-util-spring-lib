package io.github.gregoryfeijon.config.gson.adapter;

import io.github.gregoryfeijon.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
@RequiredArgsConstructor
public class JsonNodeTypeAdapter implements JsonSerializer<JsonNode>, JsonDeserializer<JsonNode> {

    private final ObjectMapper mapper;

    @Override
    public JsonElement serialize(JsonNode jsonNode, Type type, JsonSerializationContext jsonSerializationContext) {
        if (jsonNode != null) {
            String json;
            try {
                json = mapper.writeValueAsString(jsonNode);
            } catch (JsonProcessingException e) {
                throw new ApiException("Error when serializing data of JsonNode type");
            }
            return JsonParser.parseString(json);
        }
        return null;
    }

    @Override
    public JsonNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement != null) {
            try {
                return mapper.readTree(getJsonStringFromJsonElement(jsonElement));
            } catch (JsonProcessingException e) {
                throw new ApiException("Error when deserializing data of JsonNode type");
            }
        }
        return null;
    }

    private String getJsonStringFromJsonElement(JsonElement jsonElement) {
        String json;
        if (jsonElement.isJsonObject()) {
            json = getJsonFromJsonObject(jsonElement);
        } else if (jsonElement.isJsonArray()) {
            json = getJsonFromJsonArray(jsonElement);
        } else {
            json = jsonElement.toString();
        }
        return json;
    }

    private String getJsonFromJsonArray(JsonElement jsonElement) {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        return jsonArray.toString();
    }

    private String getJsonFromJsonObject(JsonElement jsonElement) {
        String json;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.keySet().contains("_children")) {
            json = jsonObject.get("_children").toString();
        } else {
            json = jsonObject.toString();
        }
        return json;
    }
}

