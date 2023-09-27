
package net.alukianov.homeprotect;

import co.aikar.commands.MessageKeys;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import net.alukianov.homeprotect.commands.HomeProtectCmd;
import net.alukianov.homeprotect.core.AreaManager;
import net.alukianov.homeprotect.events.AdvanceFlags;
import net.alukianov.homeprotect.events.ProtectionFlags;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class HomeProtect extends JavaPlugin {

    private AreaManager areaManager = new AreaManager();

    @Override
    public void onEnable() {
        setup();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void setup() {
        registerCommands();

        // [Active areas flags]
        new AdvanceFlags(this);
        new ProtectionFlags(this);
        // [Active areas flags]

    }

    // << Register plugin command handler
    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new HomeProtectCmd(this));

        commandManager.registerCommand(
                new HomeProtectCmd(this).setExceptionHandler((command, registeredCommand, sender, args, t) -> {
                    sender.sendMessage(MessageType.ERROR, MessageKeys.ERROR_GENERIC_LOGGED);
                    return true;
                }));

        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            getLogger().warning("Error occurred while executing command " + command.getName());
            return false;
        });

        // << change error message colors
        commandManager.setFormat(MessageType.ERROR, 1, ChatColor.GRAY);
        commandManager.setFormat(MessageType.ERROR, 2, ChatColor.DARK_AQUA);
    }
    // << Register plugin command handler

    // << Getter for AreaManager
    public AreaManager areaManager() {
        return areaManager;
    }

}
