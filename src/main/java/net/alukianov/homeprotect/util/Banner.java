package net.alukianov.homeprotect.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Banner {

    public static @NotNull ItemStack randomBanner() {
        return new ItemStack(Material.WHITE_BANNER, 1);
    }

    public static @NotNull String storeBanner(ItemStack banner) {
        return "temp";
    }

    public static @NotNull ItemStack restoreBanner(String banner) {
        return new ItemStack(Material.WHITE_BANNER, 1);
    }

}
