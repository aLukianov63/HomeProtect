package net.alukianov.homeprotect.ui.area;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.core.util.flag.FlagType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.IntStream;

import static net.alukianov.homeprotect.util.Utils.*;

public class FlagsGui implements Listener, InventoryHolder {

    public static int INVENTORY_SIZE;

    static {
        INVENTORY_SIZE = 36;
    }

    private Inventory inv;

    private HomeProtect plugin;

    private Area area;

    public FlagsGui(@NotNull HomeProtect plugin, @NotNull Area area) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.area = area;
        this.plugin = plugin;

        inv = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3&l" + area.name() + "`s &rflags")));
        initializeItems();
    }

    public void initializeItems() {

        ItemStack menuItem = createItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
                "&3" + "&3&l" + area.name(), new String[]{});

        ItemStack moveFlag = createItem(new ItemStack(Material.NETHERITE_BOOTS),
                "&c&lMove flag", new String[]{});

        ItemStack blockBreak = createItem(new ItemStack(Material.IRON_PICKAXE),
                "&3&lBlock break", new String[]{});


        ItemStack explodeFlag = createItem(new ItemStack(Material.TNT),
                "&3&lExplode flag", new String[]{});

        // [Menu design]
        IntStream.range(0, 9).forEach(i -> {
            for (int k : new int[]{i, (INVENTORY_SIZE - 9) + i}) inv.setItem(k, menuItem);
            if (i >= 1 && i <= (INVENTORY_SIZE / 9) - 1) {
                for (int j = 9; j < 10; j++) inv.setItem(i * j, menuItem);
                for (int j = 8; j > 7; j--) inv.setItem(i * 9 + j, menuItem);
            }
        });
        // [Menu design]

        inv.setItem(10, createItem(new ItemStack(Material.GOLDEN_PICKAXE),
                "&3&lBreak flag", new String[]{"&7Status: &7" + statusStr(area.isFlagActive(FlagType.BREAK)),
                        "&7Type: &f&lDefault", "",
                        "&7This flag prevents non-wanted players",
                        "&7from breaking blocks in your territory",
                        "", "&3➔ Double click&7 to change flag state"},
                area.isFlagActive(FlagType.BREAK)));
        inv.setItem(11, createItem(new ItemStack(Material.BRICKS),
                "&3&lBuild flag", new String[]{"&7Status: &7" + statusStr(area.isFlagActive(FlagType.BUILD)),
                        "&7Type: &f&lDefault", "", "&7This flag prevents non-wanted players",
                        "&7from placing blocks on your territory",
                        "", "&3➔ Double click&7 to change flag state"}, area.isFlagActive(FlagType.BUILD)));

        inv.setItem(23, moveFlag);
        inv.setItem(14, explodeFlag);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent e) {
        if (Objects.equals(e.getInventory().getHolder(), this)) {
            e.setCancelled(true);

            final ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem == null || clickedItem.getType().isAir()) return;

            final Player p = (Player) e.getWhoClicked();

            if (e.getRawSlot() == 23) {
                area.swapFlagState(FlagType.MOVE);
            }
            if (e.getRawSlot() == 10 && e.getClick() == ClickType.DOUBLE_CLICK) {
                sound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);

                area.swapFlagState(FlagType.BREAK);
                inv.setItem(10, createItem(new ItemStack(Material.GOLDEN_PICKAXE),
                        "&3&lBuild flag", new String[]{"&7Status: &7" + statusStr(area.isFlagActive(FlagType.BREAK)),
                                "&7Type: &f&lDefault", "",
                                "&7This flag prevents non-wanted players",
                                "&7from breaking blocks in your territory",
                                "", "&3➔ Double click&7 to change flag state"},
                        area.isFlagActive(FlagType.BREAK)));
            }
            if (e.getRawSlot() == 11 && e.getClick() == ClickType.DOUBLE_CLICK) {
                sound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);

                area.swapFlagState(FlagType.BUILD);
                inv.setItem(11, createItem(new ItemStack(Material.BRICKS),
                        "&3&lBuild flag", new String[]{"&7Status: &7" + statusStr(area.isFlagActive(FlagType.BUILD)),
                                "&7Type: &f&lDefault", "",
                                "&7This flag prevents non-wanted players",
                                "&7from placing blocks on your territory",
                                "", "&3➔ Double click&7 to change flag state"},
                        area.isFlagActive(FlagType.BUILD)));
            }
            if (e.getRawSlot() == 14) {
                area.swapFlagState(FlagType.EXPLODE);
            }

        }
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryDragEvent e) {
        if (Objects.equals(e.getInventory().getHolder(), this)) {
            e.setCancelled(true);
        }
    }

}
