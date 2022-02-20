package com.xjh.common.utils.json;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new JsonUtils.CustomObjectMapper();
    private static final ObjectMapper nullObjectMapper = new JsonUtils.CustomObjectMapper();

    public JsonUtils() {
    }

    public static JsonNode string2JsonNode(String jsonStr) {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            return jsonNode;
        } catch (IOException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var3);
        }
    }

    public static JsonNode bytes2JsonNode(byte[] bytes) {
        try {
            JsonNode jsonNode = objectMapper.readTree(bytes);
            return jsonNode;
        } catch (IOException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var3);
        }
    }

    public static <T> T string2Object(String jsonStr, Class<T> clazz) {
        try {
            T t = objectMapper.readValue(jsonStr, clazz);
            return t;
        } catch (IOException var4) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", "deserialize error. " + jsonStr + " convert to " + clazz.getName(), var4);
        }
    }

    public static <T> T string2Object(InputStream inputStream, Class<T> clazz) {
        try {
            T t = objectMapper.readValue(inputStream, clazz);
            return t;
        } catch (IOException var4) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var4);
        }
    }

    public static <T> T string2Object(String jsonString, TypeReference type) {
        try {
            return (T) objectMapper.readValue(jsonString, type);
        } catch (IOException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var3);
        }
    }

    public static <T> T bytes2Object(byte[] bytes, Class<T> clazz) {
        try {
            T t = objectMapper.readValue(bytes, clazz);
            return t;
        } catch (IOException var4) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var4);
        }
    }

    public static <T> T bytes2Object(byte[] bytes, TypeReference type) {
        try {
            T t = (T) objectMapper.readValue(bytes, type);
            return t;
        } catch (IOException var4) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var4);
        }
    }

    public static String object2String(Object obj) {
        try {
            String s = objectMapper.writeValueAsString(obj);
            return s;
        } catch (JsonProcessingException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "serialize error.", var3);
        }
    }

    public static String object2StringNotNull(Object obj) {
        try {
            String s = nullObjectMapper.writeValueAsString(obj);
            return s;
        } catch (JsonProcessingException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "serialize error.", var3);
        }
    }

    public static String object2PrettyString(Object obj) {
        try {
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return s;
        } catch (JsonProcessingException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "serialize error.", var3);
        }
    }

    public static byte[] object2Bytes(Object obj) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(obj);
            return bytes;
        } catch (JsonProcessingException var3) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "serialize error.", var3);
        }
    }

    public static <K, V> Map<K, V> string2MapObject(String jsonStr, Class<K> keyClazz, Class<V> valueClazz) {
        JavaType javaType = getCollectionType(HashMap.class, keyClazz, valueClazz);

        try {
            return (Map) objectMapper.readValue(jsonStr, javaType);
        } catch (IOException var5) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var5);
        }
    }

    public static <T> List<T> string2ListObject(String jsonStr, Class<T> tClass) {
        JavaType javaType = getCollectionType(ArrayList.class, tClass);

        try {
            return (List) objectMapper.readValue(jsonStr, javaType);
        } catch (IOException var4) {
            throw new CommonException(CommonErrorCode.REQUEST_PARAM_ERROR, "deserialize error.", var4);
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    static {
        nullObjectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    private static class CustomObjectMapper extends ObjectMapper {
        private static final String dateFormatPattern = "yyyy-MM-dd HH:mm:ss";

        public CustomObjectMapper() {
            this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.setVisibility(PropertyAccessor.FIELD, Visibility.PUBLIC_ONLY);
            this.registerModule(new JavaTimeModule());
            this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        }
    }
}

