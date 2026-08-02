package com.kaguya.custommobs.model;

import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class CustomMobInstance {
    private final MobDefinition definition;
    private final LivingEntity entity;
    // AI behaviorごとのクールダウン管理などに使う汎用ステート置き場
    private final Map<String, Long> cooldowns = new HashMap<>();
    private ItemDisplay modelDisplay;

    public CustomMobInstance(MobDefinition definition, LivingEntity entity) {
        this.definition = definition;
        this.entity = entity;
    }

    public MobDefinition getDefinition() { return definition; }
    public LivingEntity getEntity() { return entity; }

    public ItemDisplay getModelDisplay() { return modelDisplay; }
    public void setModelDisplay(ItemDisplay modelDisplay) { this.modelDisplay = modelDisplay; }

    public boolean isReady(String key, long cooldownTicks, long nowTick) {
        Long last = cooldowns.get(key);
        return last == null || (nowTick - last) >= cooldownTicks;
    }

    public void markUsed(String key, long nowTick) {
        cooldowns.put(key, nowTick);
    }
}
