
/*
 * This file is part of HomeProtect, licensed under the MIT License.
 *
 * Copyright (C) 2022 by ZxcReaper
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without l> imitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

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

    /*
    Sound.ENTITY_ITEM_BREAK <ERROR>
     */

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
