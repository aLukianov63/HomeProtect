package net.alukianov.homeprotect.ui;

import net.alukianov.homeprotect.HomeProtect;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.function.BiFunction;

import static net.alukianov.homeprotect.util.Utils.*;

public class AreaSizeGui implements Listener, InventoryHolder {

    public static int[] CLICKED_SLOTS;
    public static int INVENTORY_SIZE;

    public static int SMALL_AREA;
    public static int MEDIUM_AREA;
    public static int LARGE_AREA;

    static {
        CLICKED_SLOTS = new int[]{11, 13, 15};
        INVENTORY_SIZE = 27;

        SMALL_AREA = 64;
        MEDIUM_AREA = 128;
        LARGE_AREA = 256;
    }

    private BiFunction<Player, Integer, Response> clickFunction;
    private Inventory inventory;

    public AreaSizeGui(@NotNull HomeProtect plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        inventory = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3&lChose size of your area")));
        initializeItems();
    }

    private void initializeItems() {

        ItemStack menuItem = createItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
                "&3Chose size of your area", new String[]{});

        ItemStack smallArea = createItem(new ItemStack(Material.LIME_TERRACOTTA),
                "&2&lSmall area", new String[]{"&7Your area will get a size",
                        "&7of &3%d&7 x &3%d&7 blocks".formatted(SMALL_AREA, SMALL_AREA), "",
                        "&3➔ Left click &7to chose area size"});

        ItemStack mediumArea = createItem(new ItemStack(Material.YELLOW_TERRACOTTA),
                "&6&lMedium area", new String[]{"&7Your area will get a size",
                        "&7of &3%d&7 x &3%d&7 blocks".formatted(MEDIUM_AREA, MEDIUM_AREA), "",
                        "&3➔ Left click &7to chose area size"});

        ItemStack largeArea = createItem(new ItemStack(Material.PURPLE_TERRACOTTA),
                "&5&lLarge area", new String[]{color("&7Your area will get a size"),
                        "&7of &3%d&7 x &3%d&7 blocks".formatted(LARGE_AREA, LARGE_AREA), "",
                        "&3➔ Left click &7to chose area size"});


        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (Collections.singletonList(CLICKED_SLOTS).contains(i)) continue;
            inventory.setItem(i, menuItem);
        }

        inventory.setItem(CLICKED_SLOTS[0], smallArea);
        inventory.setItem(CLICKED_SLOTS[1], mediumArea);
        inventory.setItem(CLICKED_SLOTS[2], largeArea);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public void onClick(BiFunction<Player, Integer, Response> function) {
        this.clickFunction = function;
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent e) {
        if (Objects.equals(e.getInventory().getHolder(), this)) {
            e.setCancelled(true);

            final ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem == null || clickedItem.getType().isAir()) return;

            final Player p = (Player) e.getWhoClicked();

            Response response = Response.WAIT;

            if (e.getRawSlot() == CLICKED_SLOTS[0]) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);
                response = clickFunction.apply(p, SMALL_AREA);
            }
            if (e.getRawSlot() == CLICKED_SLOTS[1]) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);
                response = clickFunction.apply(p, MEDIUM_AREA);

            }
            if (e.getRawSlot() == CLICKED_SLOTS[2]) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);
                response = clickFunction.apply(p, LARGE_AREA);
            }
            if (response == Response.CLICKED) inventory.close();
        }
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryDragEvent e) {
        if (Objects.equals(e.getInventory().getHolder(), this)) {
            e.setCancelled(true);
        }
    }

    public enum Response {CLICKED, WAIT, ERROR}

}
