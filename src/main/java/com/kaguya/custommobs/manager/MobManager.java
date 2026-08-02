package com.kaguya.custommobs.manager;

import com.kaguya.custommobs.ai.AiBehavior;
import com.kaguya.custommobs.ai.MeleeAttackBehavior;
import com.kaguya.custommobs.model.AiBehaviorConfig;
import com.kaguya.custommobs.model.CustomMobInstance;
import com.kaguya.custommobs.model.MobDefinition;
import com.kaguya.custommobs.model.ModelConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MobManager {

    private final JavaPlugin plugin;
    private final NamespacedKey mobIdKey;
    private final Map<String, MobDefinition> definitions = new HashMap<>();
    private final Map<UUID, CustomMobInstance> activeMobs = new HashMap<>();
    private final Map<String, AiBehavior> behaviorRegistry = new HashMap<>();

    private long tickCounter = 0;

    public MobManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.mobIdKey = new NamespacedKey(plugin, "custom_mob_id");
        registerDefaultBehaviors();
    }

    private void registerDefaultBehaviors() {
        behaviorRegistry.put("melee_attack", new MeleeAttackBehavior());
    }

    public void reloadDefinitions() {
        File file = new File(plugin.getDataFolder(), "mobs.yml");
        if (!file.exists()) {
            plugin.saveResource("mobs.yml", false);
        }
        definitions.clear();
        definitions.putAll(new MobDefinitionLoader(plugin.getLogger()).load(file));
    }

    public MobDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    public Map<String, MobDefinition> getAllDefinitions() {
        return definitions;
    }

    public CustomMobInstance spawn(String mobId, Location location) {
        MobDefinition def = definitions.get(mobId);
        if (def == null) return null;

        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, def.getBaseEntity());

        // 表示名
        entity.customName(LegacyComponentSerializer.legacyAmpersand().deserialize(def.getDisplayName()));
        entity.setCustomNameVisible(true);

        // ステータス反映
        applyStats(entity, def);

        // タグ付け(死亡判定・データ復元用)
        entity.getPersistentDataContainer().set(mobIdKey, PersistentDataType.STRING, mobId);

        // バニラAIは切って独自Tickに完全移譲
        entity.setAI(false);

        CustomMobInstance instance = new CustomMobInstance(def, entity);

        if (def.getModel() != null) {
            entity.setInvisible(true);
            instance.setModelStand(spawnModelStand(entity, def.getModel()));
        }

        activeMobs.put(entity.getUniqueId(), instance);
        return instance;
    }

    /**
     * ArmorStandの頭にカスタムアイテムを被せて見た目を表現する。
     * ItemDisplayと違いGeyser(Bedrock)でも橋渡しされ、head/body/arm/legの姿勢APIを
     * 使って将来的な簡易関節アニメーションにも拡張しやすい。
     */
    private ArmorStand spawnModelStand(LivingEntity base, ModelConfig model) {
        ArmorStand stand = (ArmorStand) base.getWorld().spawnEntity(base.getLocation(), EntityType.ARMOR_STAND);
        // TODO: Bedrock(Geyser)は透明エンティティの装備品も一緒に隠す挙動があるか検証中のため、
        //       一時的にinvisibleを外している。検証後に戻すかBedrock向け分岐を検討する。
        stand.setBasePlate(false);
        stand.setArms(true);
        stand.setGravity(false);
        // TODO: Bedrock(Geyser)でmarker armor standが描画されない疑いがあり、検証のため一時的に外している
        stand.setPersistent(false);
        stand.setCustomNameVisible(false);

        ItemStack item = new ItemStack(model.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(model.getCustomModelData());
        item.setItemMeta(meta);
        // TODO: Bedrock(Geyser)がArmorStandの頭装備を橋渡ししない疑いがあり、検証のため手持ちスロットに変更中
        stand.getEquipment().setItem(EquipmentSlot.HAND, item);

        return stand;
    }

    private void syncModelStand(CustomMobInstance instance) {
        ArmorStand stand = instance.getModelStand();
        if (stand == null || !stand.isValid()) return;

        LivingEntity entity = instance.getEntity();
        Location loc = entity.getLocation().clone();
        loc.setY(loc.getY() + instance.getDefinition().getModel().getYOffset());
        stand.teleport(loc);
    }

    private void applyStats(LivingEntity entity, MobDefinition def) {
        var stats = def.getStats();
        if (entity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(stats.getHealth());
            entity.setHealth(stats.getHealth());
        }
        if (entity.getAttribute(Attribute.GENERIC_ARMOR) != null) {
            entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(stats.getArmor());
        }
        if (entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(stats.getMovementSpeed());
        }
    }

    public String getMobId(LivingEntity entity) {
        return entity.getPersistentDataContainer().get(mobIdKey, PersistentDataType.STRING);
    }

    /** BukkitSchedulerから毎Tick呼ばれる想定 */
    public void tickAll() {
        tickCounter++;
        Iterator<Map.Entry<UUID, CustomMobInstance>> it = activeMobs.entrySet().iterator();
        while (it.hasNext()) {
            CustomMobInstance instance = it.next().getValue();
            LivingEntity entity = instance.getEntity();

            if (!entity.isValid() || entity.isDead()) {
                if (instance.getModelStand() != null) {
                    instance.getModelStand().remove();
                }
                it.remove();
                continue;
            }

            if (instance.getModelStand() != null) {
                syncModelStand(instance);
            }

            for (AiBehaviorConfig behaviorConfig : instance.getDefinition().getAiBehaviors()) {
                AiBehavior behavior = behaviorRegistry.get(behaviorConfig.getType());
                if (behavior != null) {
                    behavior.tick(instance, behaviorConfig, tickCounter);
                }
            }
        }
    }

    public CustomMobInstance getInstance(UUID entityId) {
        return activeMobs.get(entityId);
    }

    public void removeInstance(UUID entityId) {
        CustomMobInstance instance = activeMobs.remove(entityId);
        if (instance != null && instance.getModelStand() != null) {
            instance.getModelStand().remove();
        }
    }
}
