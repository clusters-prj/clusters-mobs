package com.kaguya.custommobs.model;

import java.util.Map;

public class AiBehaviorConfig {
    private final String type;
    private final Map<String, Object> params;

    public AiBehaviorConfig(String type, Map<String, Object> params) {
        this.type = type;
        this.params = params;
    }

    public String getType() { return type; }

    public double getDouble(String key, double def) {
        Object v = params.get(key);
        return v == null ? def : ((Number) v).doubleValue();
    }

    public int getInt(String key, int def) {
        Object v = params.get(key);
        return v == null ? def : ((Number) v).intValue();
    }
}
