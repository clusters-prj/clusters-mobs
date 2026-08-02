package com.kaguya.custommobs.model;

import org.bukkit.entity.EntityType;

import java.util.List;

public class MobDefinition {
    private final String id;
    private final EntityType baseEntity;
    private final String displayName;
    private final StatBlock stats;
    private final List<DropEntry> drops;
    private final List<AiBehaviorConfig> aiBehaviors;

    public MobDefinition(String id, EntityType baseEntity, String displayName,
                          StatBlock stats, List<DropEntry> drops, List<AiBehaviorConfig> aiBehaviors) {
        this.id = id;
        this.baseEntity = baseEntity;
        this.displayName = displayName;
        this.stats = stats;
        this.drops = drops;
        this.aiBehaviors = aiBehaviors;
    }

    public String getId() { return id; }
    public EntityType getBaseEntity() { return baseEntity; }
    public String getDisplayName() { return displayName; }
    public StatBlock getStats() { return stats; }
    public List<DropEntry> getDrops() { return drops; }
    public List<AiBehaviorConfig> getAiBehaviors() { return aiBehaviors; }
}
