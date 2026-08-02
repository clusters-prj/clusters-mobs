package com.kaguya.custommobs;

import com.kaguya.custommobs.manager.MobManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomMobCommand implements CommandExecutor {

    private final MobManager mobManager;

    public CustomMobCommand(MobManager mobManager) {
        this.mobManager = mobManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c使い方: /cmob spawn <mobId> | /cmob reload | /cmob list");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "spawn" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cプレイヤーのみ実行できます");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§c使い方: /cmob spawn <mobId>");
                    return true;
                }
                var instance = mobManager.spawn(args[1], player.getLocation());
                if (instance == null) {
                    sender.sendMessage("§c該当するMob定義がありません: " + args[1]);
                } else {
                    sender.sendMessage("§aスポーンしました: " + args[1]);
                }
            }
            case "reload" -> {
                mobManager.reloadDefinitions();
                sender.sendMessage("§aMob定義をリロードしました (" + mobManager.getAllDefinitions().size() + "件)");
            }
            case "list" -> {
                sender.sendMessage("§e登録済みMob: " + String.join(", ", mobManager.getAllDefinitions().keySet()));
            }
            default -> sender.sendMessage("§c不明なサブコマンドです");
        }
        return true;
    }
}
