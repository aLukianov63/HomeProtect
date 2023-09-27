package net.alukianov.homeprotect.ui.area;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.commands.HomeProtectCmd;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.ui.ConfirmGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import java.util.stream.IntStream;

import static net.alukianov.homeprotect.util.Utils.*;

public class AreaGui implements Listener, InventoryHolder {

    static int INVENTORY_SIZE;

    static {
        INVENTORY_SIZE = 45;
    }

    private Inventory inv;
    private HomeProtect plugin;
    private Area area;

    public AreaGui(@NotNull HomeProtect plugin, @NotNull Area area) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.area = area;

        inv = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3&l" + area.name() + "`s &rsettings")));
        initializeItems();
    }

    public void initializeItems() {
        OfflinePlayer offlineOwner = getOfflinePlayer(area.ownerUUID());

        // [Inventory items]
        ItemStack areaBanner = createItem(area.banner(), "&3&l" + area.name(),
                new String[]{"&7Area size: " + sizeStr((area.center().getX() - area.min().getX()) * 2),
                        "&7Area center: (&3" + area.center().getX() + "&7,&3 " + area.center().getY() +
                                "&7,&3 " + area.center().getZ() + "&7)"});

        ItemStack menuItem = createItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
                "&3" + "&3&l" + area.name(), new String[]{});

        ItemStack ownerHead = createItem(playerHead(offlineOwner),
                "&3&l" + offlineOwner.getName(), new String[]{});

        ItemStack deleteArea = createItem(new ItemStack(Material.BARRIER),
                "&c&lDELETE AREA", new String[]{"&3➔ Left click &7to delete area"});

        ItemStack members = createItem(new ItemStack(Material.CAMPFIRE),
                "&3&lMembers", new String[]{"&7Members: " + "&3"
                        + area.membersUUID().size() + "&7/&3" + Area.MAX_MEMBERS});

        ItemStack rating = createItem(new ItemStack(Material.NAME_TAG), "&6&lRating", new String[]{});

        ItemStack flags = createItem(new ItemStack(Material.REPEATING_COMMAND_BLOCK),
                "&3&lFlags", new String[]{});

        ItemStack boosts = createItem(new ItemStack(Material.EXPERIENCE_BOTTLE), "&a&lBoost", new String[]{});

        ItemStack comingSoon = createItem(new ItemStack(Material.BLAZE_POWDER),
                "&b&lComing Soon", new String[]{});

        ItemStack warpPoint = createItem(new ItemStack(Material.LIGHT_GRAY_GLAZED_TERRACOTTA),
                "&d&lWarp Point", new String[]{});
        // [Inventory items]

        // [Menu design]
        IntStream.range(0, 9).forEach(i -> {
            for (int k : new int[]{i, (INVENTORY_SIZE - 9) + i}) inv.setItem(k, menuItem);
            if (i >= 1 && i <= (INVENTORY_SIZE / 9) - 1) {
                for (int j = 9; j < 10; j++) inv.setItem(i * j, menuItem);
                for (int j = 8; j > 7; j--) inv.setItem(i * 9 + j, menuItem);
            }
        });
        // [Menu design]

        // [Set inventory items]
        inv.setItem(22, areaBanner);
        inv.setItem(13, ownerHead);
        inv.setItem(32, deleteArea);
        inv.setItem(30, members);
        inv.setItem(31, flags);
        inv.setItem(24, boosts);
        inv.setItem(12, rating);
        inv.setItem(14, comingSoon);
        inv.setItem(20, warpPoint);
        // [Set inventory items]
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

            if (e.getRawSlot() == 32) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                ConfirmGui confirmGui = new ConfirmGui(plugin);
                confirmGui.onClick((pl, isConfirm) -> {
                    if (isConfirm) {
                        new HomeProtectCmd(plugin).onDelete(pl, area.name());
                        return ConfirmGui.Response.CLICKED;
                    }
                    return ConfirmGui.Response.BACK.setInventory(inv);
                });
                confirmGui.open(p);
            }
            if (e.getRawSlot() == 30) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                new HomeProtectCmd(plugin).onMembers(p, area.name());
            }
            if (e.getRawSlot() == 31) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                new HomeProtectCmd(plugin).onFlags(p, area.name());
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
