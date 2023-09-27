package net.alukianov.homeprotect.ui.area;

import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.commands.HomeProtectCmd;
import net.alukianov.homeprotect.core.Area;
import net.kyori.adventure.text.Component;
import net.wesjd.anvilgui.AnvilGUI;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.IntStream;

import static net.alukianov.homeprotect.util.Utils.*;

public class MembersGui implements Listener, InventoryHolder {

    static int INVENTORY_SIZE;

    static int[] BLOCKED_SLOTS;

    static {
        INVENTORY_SIZE = 45;

        BLOCKED_SLOTS = new int[]{17, 26};
    }

    private Inventory inv;
    private HomeProtect plugin;
    private Area area;

    public MembersGui(@NotNull HomeProtect plugin, @NotNull Area area) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.area = area;

        inv = Bukkit.createInventory(this, INVENTORY_SIZE,
                Component.text(color("&3&l" + area.name() + "`s &rmembers")));
        initializeItems();
    }

    private void initializeItems() {

        ItemStack menuItem = createItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE),
                "&3" + "&3&l" + area.name(), new String[]{});

        ItemStack addMember = createItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE),
                "&a&lAdd member", new String[]{});

        ItemStack backItem = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) backItem.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        backItem.setItemMeta(meta);

        backItem = createItem(backItem, "&c&l← Back", new String[]{});

        // [Menu design]
        IntStream.range(0, 9).forEach(i -> {
            for (int k : new int[]{i, (INVENTORY_SIZE - 9) + i}) inv.setItem(k, menuItem);
            if (i >= 1 && i <= (INVENTORY_SIZE / 9) - 1) {
                for (int j = 9; j < 10; j++) inv.setItem(i * j, menuItem);
                for (int j = 8; j > 7; j--) inv.setItem(i * 9 + j, menuItem);
            }
        });
        // [Menu design]

        // [set area members]
        int i = 11;

        for (String memberUuid : area.membersUUID()) {
            if (i >= 35) break; // [members > 20]

            OfflinePlayer member = getOfflinePlayer(memberUuid);
            ItemStack memberHead = createItem(playerHead(member), "&3&l" + member.getName(), new String[]{});

            if (i == BLOCKED_SLOTS[0] || i == BLOCKED_SLOTS[1]) i = i + 2;
            inv.setItem(i, memberHead);
            i++;
        }
        // [set area members]

        inv.setItem(10, addMember);
        inv.setItem(40, backItem);
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

            if (e.getRawSlot() == 40) {
                p.openInventory(new AreaGui(plugin, area).getInventory());
            }

            if (e.getRawSlot() == 10) {
                sound(p, Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.3f);

                new AnvilGUI.Builder()
                        .onComplete((owner, member) -> {
                            new HomeProtectCmd(plugin).addMember(owner, area.name(), member);
                            return AnvilGUI.Response.openInventory(new MembersGui(plugin, area).getInventory());
                        })
                        .plugin(plugin)
                        .title(color("&3&lEnter area name"))
                        .itemLeft(createItem(new ItemStack(Material.CAMPFIRE), "Add Member", new String[]{}))
                        .open(p);

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
