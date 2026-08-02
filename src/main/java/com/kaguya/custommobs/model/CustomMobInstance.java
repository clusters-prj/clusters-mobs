package com.kaguya.custommobs.model;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomMobInstance {
    private final MobDefinition definition;
    private final LivingEntity entity;
    // AI behaviorごとのクールダウン管理などに使う汎用ステート置き場
    private final Map<String, Long> cooldowns = new HashMap<>();
    private ArmorStand modelStand;

    public CustomMobInstance(MobDefinition definition, LivingEntity entity) {
        this.definition = definition;
        this.entity = entity;
    }

    public MobDefinition getDefinition() { return definition; }
    public LivingEntity getEntity() { return entity; }

    public ArmorStand getModelStand() { return modelStand; }
    public void setModelStand(ArmorStand modelStand) { this.modelStand = modelStand; }

    public boolean isReady(String key, long cooldownTicks, long nowTick) {
        Long last = cooldowns.get(key);
        return last == null || (nowTick - last) >= cooldownTicks;
    }

    public void markUsed(String key, long nowTick) {
        cooldowns.put(key, nowTick);
    }
}
