package com.kaguya.custommobs.model;

public class StatBlock {
    private final double health;
    private final double damage;
    private final double armor;
    private final double movementSpeed;

    public StatBlock(double health, double damage, double armor, double movementSpeed) {
        this.health = health;
        this.damage = damage;
        this.armor = armor;
        this.movementSpeed = movementSpeed;
    }

    public double getHealth() { return health; }
    public double getDamage() { return damage; }
    public double getArmor() { return armor; }
    public double getMovementSpeed() { return movementSpeed; }
}
