package net.alukianov.homeprotect.core.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BlockPoint {

    private static final BlockPoint ZERO = new BlockPoint(0, 0, 0);

    private final int x;
    private final int y;
    private final int z;

    public BlockPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull BlockPoint center(final @NotNull BlockPoint min, final @NotNull BlockPoint max) {
        return new BlockPoint((max.x - min.x) / 2 + min.x, (max.y - min.y) / 2 + min.y,
                (max.z - min.z) / 2 + min.z);
    }

    public static double pointDistance(final @NotNull BlockPoint p1, final @NotNull BlockPoint p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2d) + Math.pow((p1.getY() - p2.getY()), 2d) +
                Math.pow((p1.getZ() - p2.getZ()), 2d));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
