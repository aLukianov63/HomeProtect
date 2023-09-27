package net.alukianov.homeprotect.core;

import net.alukianov.homeprotect.core.util.BlockPoint;
import net.alukianov.homeprotect.core.util.WarpPoint;
import net.alukianov.homeprotect.core.util.flag.Flag;
import net.alukianov.homeprotect.core.util.flag.FlagType;
import net.alukianov.homeprotect.util.Banner;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class Area {

    // Max members at area
    public static final int MAX_MEMBERS;
    public static Pattern NAME_PATTERN;

    static {
        MAX_MEMBERS = 20;
        NAME_PATTERN = Pattern.compile("^[a-z[1-9][_@$&#]]{3,15}$", Pattern.CASE_INSENSITIVE);
    }

    private final String name;

    private final String ownerUUID;
    private final Set<String> membersUUID;

    private final World world;
    private final BlockPoint min;
    private final BlockPoint max;

    private final ItemStack areaBanner;

    private WarpPoint warpPoint;

    private Set<Flag> flags;

    public Area(String name, String owner, World world, BlockPoint min, BlockPoint max) {
        this.name = name;

        this.ownerUUID = owner;
        this.membersUUID = new LinkedHashSet<>();

        this.world = world;
        this.min = min;
        this.max = max;

        areaBanner = Banner.randomBanner();
        warpPoint = null;
        flags = new LinkedHashSet<>(Flag.defaultFlags());
    }

    public Area(String name, String owner, World world, @NotNull Location loc, int size) {
        this(name, owner, world,
                new BlockPoint(loc.getBlockX() - size / 2, loc.getWorld().getMinHeight(),
                        loc.getBlockZ() - size / 2),
                new BlockPoint(loc.getBlockX() + size / 2, loc.getWorld().getMaxHeight(),
                        loc.getBlockZ() + size / 2));
    }

    public static boolean isCorrectName(String name) {
        return Area.NAME_PATTERN.matcher(name).matches();
    }

    // << Area protection
    public Boolean isMember(final String uuid) {
        if (ownerUUID.equals(uuid)) return true;

        for (String member : membersUUID) {
            if (member.equals(uuid)) return true;
        }
        return false;
    }

    public boolean isContains(final @NotNull Location loc) {
        return loc.getWorld() == this.world &&
                loc.getBlockX() >= min.getX() && loc.getBlockX() <= max.getX() &&
                loc.getBlockY() >= min.getY() && loc.getBlockY() <= max.getY() &&
                loc.getBlockZ() >= min.getZ() && loc.getBlockZ() <= max.getZ();
    }

    public boolean isIntersect(final @NotNull Area area) {
        if (max.getZ() < area.min.getZ() || min.getZ() > area.max.getZ())
            return false;

        return max.getX() >= area.min.getX() && min.getX() <= area.max.getX();
    }

    public boolean isContains(final @NotNull Player player) {
        return isContains(player.getLocation());
    }
    // << Area protection

    // << Area data
    public String name() {
        return name;
    }

    public String ownerUUID() {
        return ownerUUID;
    }

    public Set<String> membersUUID() {
        return membersUUID;
    }

    public World world() {
        return world;
    }
    // << Area data

    // << Flags
    public boolean isFlagActive(final FlagType type) {
        return flags.contains(new Flag(type));
    }

    public boolean swapFlagState(final FlagType type) {
        if (isFlagActive(type)) {
            return removeFlag(type);
        }
        return addFlag(type);
    }

    public Set<Flag> flags() {
        return flags;
    }

    public boolean addFlag(final FlagType type) {
        return flags.add(new Flag(type));
    }

    public boolean removeFlag(final FlagType type) {
        return flags.remove(new Flag(type));
    }
    // << Flags

    // << Get banner item
    public ItemStack banner() {
        return areaBanner;
    }

    // << Warp point
    public Location warpLocation() {
        return new Location(world, warpPoint.getX(),
                warpPoint.getY(), warpPoint.getZ());
    }

    public void setWarpLocation(final @NotNull Location location) {
        warpPoint = new WarpPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void setWarpState(final WarpPoint.State state) {
        warpPoint.setState(state);
    }

    public boolean canWarp(final @NotNull Player player) {
        if (warpPoint == null) return false;
        if (warpPoint.state().equals(WarpPoint.State.PRIVATE)) return isMember(player.getUniqueId().toString());
        return true;
    }

    // << Warp point

    // << Members
    public boolean isAddMember() {
        return membersUUID.size() < MAX_MEMBERS;
    }

    public boolean addMember(final @NotNull String member) {
        return membersUUID.add(member);
    }

    public boolean removeMember(final @NotNull String member) {
        return membersUUID.remove(member);
    }
    // << Members

    // << Area points
    public BlockPoint min() {
        return min;
    }

    public BlockPoint max() {
        return max;
    }

    public BlockPoint center() {
        return BlockPoint.center(min, max);
    }

    public Set<Location> border() {

        Set<Location> temp = new HashSet<>();

        for (int x = min().getX(); x <= max.getX(); ++x) {
            for (int y = min().getY(); y <= max.getY(); ++y) {
                for (int z = min().getZ(); z <= max.getZ(); ++z) {

                    boolean isOutside = x == min.getX() || x == max.getX();

                    if (y == min.getY() || y == max.getY()) isOutside = true;
                    if (z == min.getZ() || z == max.getZ()) isOutside = true;

                    if (isOutside) {
                        temp.add(new Location(world, x, y, z));
                    }
                }
            }
        }
        return temp;
    }
    // << Area points

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Area area)) return false;

        return new org.apache.commons.lang3.builder.EqualsBuilder().append(name, area.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new org.apache.commons.lang3.builder
                .HashCodeBuilder(17, 37)
                .append(name).toHashCode();
    }

}
