package com.kaguya.custommobs;

import com.kaguya.custommobs.manager.MobDeathListener;
import com.kaguya.custommobs.manager.MobManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomMobsPlugin extends JavaPlugin {

    private MobManager mobManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mobManager = new MobManager(this);
        mobManager.reloadDefinitions();

        getServer().getPluginManager().registerEvents(new MobDeathListener(mobManager), this);
        getCommand("cmob").setExecutor(new CustomMobCommand(mobManager));

        // 1tickごとにAI Tick(重くなってきたら2~4tick間引き推奨)
        getServer().getScheduler().runTaskTimer(this, mobManager::tickAll, 1L, 1L);

        getLogger().info("CustomMobs 有効化完了");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomMobs 無効化");
    }

    public MobManager getMobManager() {
        return mobManager;
    }
}
