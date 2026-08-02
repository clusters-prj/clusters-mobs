package com.kaguya.custommobs.model;

import org.bukkit.Material;

public class DropEntry {
    private final Material item;
    private final double chance;
    private final int amountMin;
    private final int amountMax;

    public DropEntry(Material item, double chance, int amountMin, int amountMax) {
        this.item = item;
        this.chance = chance;
        this.amountMin = amountMin;
        this.amountMax = amountMax;
    }

    public Material getItem() { return item; }
    public double getChance() { return chance; }
    public int getAmountMin() { return amountMin; }
    public int getAmountMax() { return amountMax; }
}
