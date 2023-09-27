package net.alukianov.homeprotect.util;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Utils {
    @Contract("_ -> new")
    public static @NotNull String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String decolor(String string) {
        return ChatColor.stripColor(color(string));
    }

    @SuppressWarnings("deprecation")
    public static void title(@NotNull Player player, String title, String subTitle) {
        player.sendTitle(color(title), color(subTitle));
    }

    public static void sound(@NotNull Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static Player getPlayer(String uuid) {
        return Bukkit.getPlayer(UUID.fromString(uuid));
    }

    public static @NotNull OfflinePlayer getOfflinePlayer(String uuid) {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    public static String getStrUuid(@NotNull Player player) {
        return player.getUniqueId().toString();
    }

    @Contract(" -> new")
    public static @NotNull String header() {
        return color("&7[&3&lH&f&lP&r&7]");
    }

    public static @NotNull String randomString(int min, int max) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "0123456789" +
                "abcdefghijklmnopqrstuvxyz" +
                "_@$&#";
        StringBuilder sb = new StringBuilder(max);
        int rand_max = new Random().nextInt(min, max + 1);

        for (int i = 0; i < rand_max; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    @Contract(pure = true)
    public static @Nullable String sizeStr(int size) {
        return switch (size) {
            case 64 -> "&2&lSmall";
            case 128 -> "&6&lMedium";
            case 256 -> "&5&lLarge";
            default -> null;
        };
    }

    @Contract("_, _, _ -> param1")
    public static @NotNull ItemStack createItem(@NotNull ItemStack item, String name, String[] lore) {
        ItemMeta meta = item.getItemMeta();

        List<String> tempLore = new ArrayList<>(List.of(lore));
        List<Component> loreList = new ArrayList<>();

        tempLore.forEach((str) -> loreList.add(Component.text(color(str))));

        meta.lore(loreList);
        meta.displayName(Component.text(color(name)));
        item.setItemMeta(meta);

        return item;
    }


    public static @NotNull ItemStack createItem(@NotNull ItemStack item, String name, String[] lore, boolean enchanted) {
        ItemMeta meta = item.getItemMeta();

        List<String> tempLore = new ArrayList<>(List.of(lore));
        List<Component> loreList = new ArrayList<>();

        tempLore.forEach((str) -> loreList.add(Component.text(color(str))));

        if (enchanted) meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);

        meta.lore(loreList);
        meta.displayName(Component.text(color(name)));
        item.setItemMeta(meta);

        return item;
    }


    public static @NotNull ItemStack playerHead(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        head.setItemMeta(meta);

        return head;
    }

    public static String statusStr(boolean status) {
        return status ? "&2&lActive&r" : "&4&lUnactive&r";
    }

}
