package net.alukianov.homeprotect.events;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.core.util.flag.FlagType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class AdvanceFlags implements Listener {

    private HomeProtect plugin;

    public AdvanceFlags(@NotNull HomeProtect plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    private void onMove(PlayerMoveEvent e) {
        for (Area area : plugin.areaManager().areas()) {
            if (area.isFlagActive(FlagType.MOVE)) {

            }
        }
    }
}
