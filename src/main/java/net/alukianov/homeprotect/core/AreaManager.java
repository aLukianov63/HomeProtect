package net.alukianov.homeprotect.core;

import net.alukianov.homeprotect.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AreaManager {

    // << Max areas can player creates
    public static final int MAX_AREAS = 5;

    private Map<World, LinkedHashSet<Area>> areas;

    public AreaManager() {
        areas = new HashMap<>();
    }

    @Contract(pure = true)
    public AreaManager(@NotNull AreaManager manager) {
        areas = manager.areas;
    }

    public boolean isNameUnique(final @NotNull String name) {
        return areaByName(name) == null;
    }

    public boolean isAreaNonIntersect(final Area playerArea) {
        for (Area area : areas()) {
            if (area.isIntersect(playerArea)) return false;
        }
        return true;
    }

    /**
     * @param uuid of player
     * @return the number of the player has
     */
    public int playerAreasCount(final String uuid) {
        return getPlayerAreas(uuid).size();
    }

    public int playersPossibleAreasCount(final String uuid) {
        Player player = Utils.getPlayer(uuid);

        if (player == null || !player.hasPermission("homeprotect.create")) return 0;
        if (player.hasPermission("homeprotect.create.5")) return 5;
        if (player.hasPermission("homeprotect.create.4")) return 4;
        if (player.hasPermission("homeprotect.create.3")) return 3;

        return 2;
    }

    public boolean canCreateArea(@NotNull Player player) {
        return playerAreasCount(player.getUniqueId().toString()) < MAX_AREAS &&
                playerAreasCount(player.getUniqueId().toString()) <
                        playersPossibleAreasCount(player.getUniqueId().toString());
    }

    // << Add and remove areas
    public void addArea(final @NotNull Area area) {
        if (areas.containsKey(area.world())) {
            areas.get(area.world()).add(area);
            return;
        }
        areas.put(area.world(), new LinkedHashSet<Area>());
        areas.get(area.world()).add(area);
    }

    public void removeArea(final @NotNull Area area) {
        if (areas.containsKey(area.world())) {
            areas.get(area.world()).remove(area);
        }
    }

    public void putAreas(final World world, final LinkedHashSet<Area> areas) {
        this.areas.put(world, areas);
    }
    // << Add and remove areas


    // << Get areas
    public LinkedHashSet<Area> worldAreas(final World world) {
        if (areas.containsKey(world)) {
            return areas.get(world);
        }
        return null;
    }

    public LinkedHashSet<Area> areas() {
        LinkedHashSet<Area> temp = new LinkedHashSet<>();
        for (World world : areas.keySet()) {
            temp.addAll(areas.get(world));
        }
        return temp;
    }

    public Area areaByName(final String name) {
        for (Area area : areas()) {
            if (area.name().equals(name)) return area;
        }
        return null;
    }

    public List<Area> getPlayerAreas(String uuid) {
        List<Area> temp = new ArrayList<>();

        for (Area area : areas()) {
            if (area.ownerUUID().equals(uuid)) temp.add(area);
        }
        return temp;
    }

    public Area areaByLocation(Location loc) {
        for (Area area : areas()) {
            if (area.isContains(loc)) return area;
        }
        return null;
    }
    // << Get areas

}
