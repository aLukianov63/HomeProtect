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

import java.util.Objects;
import java.util.function.BiFunction;

import static net.alukianov.homeprotect.util.Utils.*;

public class ConfirmGui implements Listener, InventoryHolder {

    public static int INVENTORY_SIZE;

    public static int[] OK_SLOTS;

    public static int[] NO_SLOTS;

    static {
        INVENTORY_SIZE = 45;
        OK_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
        NO_SLOTS = new int[]{14, 15, 16, 23, 24, 25, 32, 33, 34};
    }

    private BiFunction<Player, Boolean, Response> clickFunction;
    private Inventory inventory;

    public ConfirmGui(@NotNull HomeProtect plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        inventory = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3&lAre you sure?")));
        initializeItems();
    }

    private void initializeItems() {

        ItemStack confirm = createItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE),
                "&a&lCONFIRM", new String[]{});
        ItemStack cansel = createItem(new ItemStack(Material.RED_STAINED_GLASS_PANE),
                "&c&lCANSEL", new String[]{});

        for (int i : OK_SLOTS) {
            inventory.setItem(i, confirm);
        }

        for (int i : NO_SLOTS) {
            inventory.setItem(i, cansel);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    public void onClick(BiFunction<Player, Boolean, Response> function) {
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

            for (int okSlot : OK_SLOTS) {
                if (okSlot == e.getRawSlot()) {
                    sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);
                    response = clickFunction.apply(p, true);
                }
            }

            for (int noSlot : NO_SLOTS) {
                if (noSlot == e.getRawSlot()) {
                    sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);
                    response = clickFunction.apply(p, false);
                }
            }

            if (response == Response.CLICKED) inventory.close();
            if (response == Response.BACK && response.inv != null) p.openInventory(response.inv);
        }
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryDragEvent e) {
        if (Objects.equals(e.getInventory().getHolder(), this)) {
            e.setCancelled(true);
        }
    }

    public enum Response {
        CLICKED, WAIT, ERROR, BACK;

        Inventory inv;

        public Response setInventory(Inventory inventory) {
            inv = inventory;
            return this;
        }
    }

}
