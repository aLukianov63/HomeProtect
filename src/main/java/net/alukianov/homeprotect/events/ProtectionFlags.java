package net.alukianov.homeprotect.events;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.core.util.flag.FlagType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.jetbrains.annotations.NotNull;

import static net.alukianov.homeprotect.util.Utils.getStrUuid;

public class ProtectionFlags implements Listener {

    private HomeProtect plugin;

    public ProtectionFlags(@NotNull HomeProtect plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    // [Block break protection]
    @EventHandler
    private void onBreak(BlockBreakEvent e) {
        for (Area area : plugin.areaManager().areas()) {
            if (area.isContains(e.getBlock().getLocation())) {
                if (area.isFlagActive(FlagType.BREAK) && !area.isMember(getStrUuid(e.getPlayer()))) {
                    e.setCancelled(true);
                }
            }
        }
    }
    // [Block break protection]

    // [Block place protection]
    @EventHandler
    private void onPlace(BlockPlaceEvent e) {
        for (Area area : plugin.areaManager().areas()) {
            if (area.isContains(e.getBlock().getLocation())) {
                if (area.isFlagActive(FlagType.BUILD) && !area.isMember(getStrUuid(e.getPlayer()))) {
                    e.setCancelled(true);
                }
            }
        }
    }
    // [Block place protection]

    // [Block explode protection]
    @EventHandler
    private void onExplode(EntityExplodeEvent e) {
        for (Area area : plugin.areaManager().areas()) {
            if (area.isFlagActive(FlagType.EXPLODE)) {
                e.blockList().removeIf(block -> area.isContains(block.getLocation()));
            }
        }
    }
    // [Block explode protection]

}
