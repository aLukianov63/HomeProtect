package net.alukianov.homeprotect.ui.player;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.commands.HomeProtectCmd;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.core.AreaManager;
import net.alukianov.homeprotect.core.util.flag.Flag;
import net.alukianov.homeprotect.ui.AreaSizeGui;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.IntStream;

import static net.alukianov.homeprotect.util.Utils.*;

public class PlayerAreasGui implements Listener, InventoryHolder {

    public static ItemStack createAreaItem;
    static int[] AREAS_SLOTS;
    static int INVENTORY_SIZE;

    static {
        AREAS_SLOTS = new int[]{11, 12, 13, 14, 15};
        INVENTORY_SIZE = 27;

        createAreaItem = createItem(
                new ItemStack(Material.LIME_STAINED_GLASS_PANE), "&a&lActive slot",
                new String[]{"&7You can use this slot to create",
                        "&7a new protected territory", "",
                        "&3➔ Left click &7to create area!"}
        );
    }

    private Inventory inv;
    private Player player;
    private HomeProtect plugin;

    public PlayerAreasGui(@NotNull Player player, @NotNull HomeProtect plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.player = player;
        this.plugin = plugin;

        inv = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3" + player.getName() + "`s &rareas")));
        initializeItems();
    }

    public void initializeItems() {

        ItemStack menuItem = createItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
                "&3" + player.getName() + "`s &rareas", new String[]{});

        ItemStack blockedSlotItem = createItem(new ItemStack(Material.IRON_BARS),
                "&c&lBlocked slot", new String[]{"&7You cannot create a protected", "&7territory in this slot"});

        int canCreate = plugin.areaManager().playersPossibleAreasCount(getStrUuid(player));

        int areasCount = plugin.areaManager().playerAreasCount(getStrUuid(player));

        IntStream.range(0, 9).forEach(i -> {
            for (int k : new int[]{i, (INVENTORY_SIZE - 9) + i}) inv.setItem(k, menuItem);
            if (i >= 1 && i <= 2) {
                for (int j = 9; j < 11; j++) inv.setItem(i * j, menuItem);
                for (int j = 8; j > 6; j--) inv.setItem(i * 9 + j, menuItem);
            }
        });

        int areas = AREAS_SLOTS[0];

        for (Area area : plugin.areaManager().getPlayerAreas(getStrUuid(player))) {
            ItemStack areaItems = createItem(area.banner(), "&3&l" + area.name(), new String[]
                    {"&7Owner:&3 " + getPlayer(area.ownerUUID()).getName(),
                            "&7Members:&3 " + area.membersUUID().size() + "&7/&3" + Area.MAX_MEMBERS,
                            "&7Flags:&3 " + area.flags().size() + "&7/&3" + Flag.MAX_FLAGS,
                            "&7Warp point: (&3none&7)", "",
                            "&3➔ Left click &7to open area settings"});
            // TODO: reset warp description to area item
            inv.setItem(areas++, areaItems);
        }

        int currentCount = areas - 11;

        while (areas - 10 <= AreaManager.MAX_AREAS) {
            if (areas - areasCount - 10 <= canCreate - currentCount) {
                inv.setItem(areas++, createAreaItem);
            } else {
                inv.setItem(areas++, blockedSlotItem);
            }
        }

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

            if (clickedItem.equals(createAreaItem)) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                AreaSizeGui sizeGui = new AreaSizeGui(plugin);
                sizeGui.onClick((creator, size) -> {

                    // << Create enchanted book
                    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                    meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                    book.setItemMeta(meta);
                    // <<

                    new AnvilGUI.Builder()
                            .onComplete((owner, name) -> {
                                new HomeProtectCmd(plugin).onCreate(owner, name, size);
                                return AnvilGUI.Response.close();
                            })
                            .plugin(plugin)
                            .title(color("&3&lEnter area name"))
                            .itemLeft(createItem(new ItemStack(Material.CYAN_BANNER), randomString(5, 6),
                                    new String[]{"&7Owner:&3 " + player.getName(),
                                            "&7Area size: " + sizeStr(size), "",
                                            "&3➔ Click &7to create area"}))
                            .itemRight(createItem(book, "&3Create instructions",
                                    new String[]{"&7Use the given rules when",
                                            "&7choosing the name of the area", "",
                                            "&7Use symbols: &3A&7-&3Z&7, &31&7-&39&7, &3_@$&#",
                                            "&7Length: &7(&33&7-&315&7)", "",
                                            "&3➔ Click &7to &4&lSTOP&r&7 creating area"}))
                            .onRightInputClick(HumanEntity::closeInventory)
                            .open(creator);
                    return AreaSizeGui.Response.CLICKED;
                });
                sizeGui.open(p);
            }

            if (clickedItem.getType().equals(Material.WHITE_BANNER)) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                Area area = plugin.areaManager().areaByName(decolor(clickedItem.getItemMeta().getDisplayName()));
                if (area != null) new HomeProtectCmd(plugin).onArea(p, area.name());
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
