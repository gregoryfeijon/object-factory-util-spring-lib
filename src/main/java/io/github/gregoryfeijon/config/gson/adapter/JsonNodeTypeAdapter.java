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

/**
 * A Gson adapter that enables serialization and deserialization of Jackson's JsonNode type.
 * <p>
 * This adapter bridges between Gson and Jackson by allowing JsonNode objects to be
 * included in Gson-serialized objects and vice versa.
 *
 * @author gregory.feijon
 */
@Component
@RequiredArgsConstructor
public class JsonNodeTypeAdapter implements JsonSerializer<JsonNode>, JsonDeserializer<JsonNode> {

    private final ObjectMapper mapper;

    /**
     * Serializes a Jackson JsonNode to a Gson JsonElement.
     *
     * @param jsonNode The Jackson JsonNode to serialize
     * @param type The type of the object being serialized
     * @param jsonSerializationContext The serialization context
     * @return The Gson JsonElement representation
     * @throws ApiException If there is an error serializing the JsonNode
     */
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

    /**
     * Deserializes a Gson JsonElement to a Jackson JsonNode.
     *
     * @param jsonElement The Gson JsonElement to deserialize
     * @param type The type of the object being deserialized
     * @param jsonDeserializationContext The deserialization context
     * @return The Jackson JsonNode representation
     * @throws JsonParseException If there is an error during deserialization
     * @throws ApiException If there is an error deserializing the JsonNode
     */
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

    /**
     * Converts a Gson JsonElement to its string representation.
     *
     * @param jsonElement The JsonElement to convert
     * @return The string representation of the JsonElement
     */
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

    /**
     * Extracts the JSON string from a JsonElement that is a JsonArray.
     *
     * @param jsonElement The JsonElement containing a JsonArray
     * @return The string representation of the JsonArray
     */
    private String getJsonFromJsonArray(JsonElement jsonElement) {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        return jsonArray.toString();
    }

    /**
     * Extracts the JSON string from a JsonElement that is a JsonObject.
     * <p>
     * If the JsonObject contains a "_children" property, returns the string representation
     * of that property; otherwise, returns the string representation of the entire JsonObject.
     *
     * @param jsonElement The JsonElement containing a JsonObject
     * @return The string representation of the JsonObject or its "_children" property
     */
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