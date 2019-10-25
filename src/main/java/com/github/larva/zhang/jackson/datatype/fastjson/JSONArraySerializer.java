package com.github.larva.zhang.jackson.datatype.fastjson;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Serializer for com.alibaba.fastjson.JSONArray
 *
 * @author zhanghan
 */
@SuppressWarnings("WeakerAccess")
public class JSONArraySerializer extends JSONBaseSerializer<JSONArray> {
    private static final long serialVersionUID = 1L;

    public final static JSONArraySerializer INSTANCE = new JSONArraySerializer();

    public JSONArraySerializer() {
        super(JSONArray.class);
    }

    @Override // since 2.6
    public boolean isEmpty(SerializerProvider provider, JSONArray value) {
        return (value == null) || value.isEmpty();
    }

    @Override
    public void serialize(JSONArray value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeStartArray();
        serializeContents(value, g, provider);
        g.writeEndArray();
    }

    @Override
    public void serializeWithType(JSONArray value, JsonGenerator g, SerializerProvider provider,
                                  TypeSerializer typeSer) throws IOException {
        g.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, JsonToken.START_ARRAY));
        serializeContents(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
            throws JsonMappingException {
        return createSchemaNode("array", true);
    }

    protected void serializeContents(JSONArray value, JsonGenerator g, SerializerProvider provider)
            throws IOException {
        for (Object ob : value) {
            if (ob == null) {
                g.writeNull();
                continue;
            }
            Class<?> cls = ob.getClass();
            if (cls == JSONObject.class) {
                JSONObjectSerializer.INSTANCE.serialize((JSONObject) ob, g, provider);
            } else if (cls == JSONArray.class) {
                serialize((JSONArray) ob, g, provider);
            } else if (cls == String.class) {
                g.writeString((String) ob);
            } else if (cls == Integer.class) {
                g.writeNumber(((Integer) ob).intValue());
            } else if (cls == Long.class) {
                g.writeNumber(((Long) ob).longValue());
            } else if (cls == Boolean.class) {
                g.writeBoolean(((Boolean) ob).booleanValue());
            } else if (cls == Double.class) {
                g.writeNumber(((Double) ob).doubleValue());
            } else if (JSONObject.class.isAssignableFrom(cls)) { // sub-class
                JSONObjectSerializer.INSTANCE.serialize((JSONObject) ob, g, provider);
            } else if (JSONArray.class.isAssignableFrom(cls)) { // sub-class
                serialize((JSONArray) ob, g, provider);
            } else {
                provider.defaultSerializeValue(ob, g);
            }
        }
    }
}
