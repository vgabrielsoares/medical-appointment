package com.me.medical.infra;

import java.lang.reflect.Method;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converte String JSON para PGobject (jsonb) ao persistir e retorna String ao ler.
 */
@Converter
public class JsonbConverter implements AttributeConverter<String, Object> {
    @Override
    public Object convertToDatabaseColumn(String attribute) {
        try {
            // Criar PGobject dinamicamente por reflection para evitar dependência em tempo de compilação
            Class<?> pgClass = Class.forName("org.postgresql.util.PGobject");
            Object obj = pgClass.getDeclaredConstructor().newInstance();
            Method setType = pgClass.getMethod("setType", String.class);
            Method setValue = pgClass.getMethod("setValue", String.class);
            setType.invoke(obj, "jsonb");
            // Se attribute for nulo, retornamos null para que o driver faça um bind NULL do tipo correto
            if (attribute != null) {
                setValue.invoke(obj, attribute);
            } else {
                return null;
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON string to PGobject", e);
        }
    }

    @Override
    public String convertToEntityAttribute(Object dbData) {
        if (dbData == null) return null;
        try {
            // Se for PGobject, extrair value via reflection
            Class<?> pgClass = Class.forName("org.postgresql.util.PGobject");
            if (pgClass.isInstance(dbData)) {
                Method getValue = pgClass.getMethod("getValue");
                Object v = getValue.invoke(dbData);
                return v == null ? null : v.toString();
            }
        } catch (Exception e) {
            // fallback para toString
        }
        return dbData.toString();
    }
}
