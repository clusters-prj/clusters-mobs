package com.kaguya.custommobs.model;

import org.bukkit.Material;

public class ModelConfig {
    private final Material material;
    private final int customModelData;
    private final float scale;
    private final double yOffset;

    public ModelConfig(Material material, int customModelData, float scale, double yOffset) {
        this.material = material;
        this.customModelData = customModelData;
        this.scale = scale;
        this.yOffset = yOffset;
    }

    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public float getScale() { return scale; }
    public double getYOffset() { return yOffset; }
}
