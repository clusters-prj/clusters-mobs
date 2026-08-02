package com.kaguya.custommobs.ai;

import com.kaguya.custommobs.model.AiBehaviorConfig;
import com.kaguya.custommobs.model.CustomMobInstance;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MeleeAttackBehavior implements AiBehavior {

    private static final String COOLDOWN_KEY = "melee_attack";

    @Override
    public void tick(CustomMobInstance mob, AiBehaviorConfig config, long nowTick) {
        LivingEntity self = mob.getEntity();
        double range = config.getDouble("range", 2.0);
        int cooldownTicks = config.getInt("cooldown-ticks", 20);

        Player target = findNearestPlayer(self, range * 3); // 索敵範囲は攻撃範囲より広め
        if (target == null) return;

        double distSq = self.getLocation().distanceSquared(target.getLocation());

        if (distSq <= range * range) {
            if (mob.isReady(COOLDOWN_KEY, cooldownTicks, nowTick)) {
                double damage = mob.getDefinition().getStats().getDamage();
                target.damage(damage, self);
                mob.markUsed(COOLDOWN_KEY, nowTick);
            }
        } else {
            // AI無効化中はsetVelocityが移動に反映されないため、直接座標を動かす
            Vector dir = target.getLocation().toVector().subtract(self.getLocation().toVector()).normalize();
            double speed = self.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).getValue() * 5.0;

            Location next = self.getLocation().add(dir.getX() * speed, 0, dir.getZ() * speed);
            next.setYaw((float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ())));
            self.teleport(next);
        }
    }

    private Player findNearestPlayer(LivingEntity self, double radius) {
        Player nearest = null;
        double nearestDistSq = radius * radius;
        for (org.bukkit.entity.Entity e : self.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof Player p) {
                double d = e.getLocation().distanceSquared(self.getLocation());
                if (d < nearestDistSq) {
                    nearestDistSq = d;
                    nearest = p;
                }
            }
        }
        return nearest;
    }
}
