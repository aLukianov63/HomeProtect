package net.alukianov.homeprotect.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.alukianov.homeprotect.HomeProtect;
import net.alukianov.homeprotect.core.Area;
import net.alukianov.homeprotect.core.AreaManager;
import net.alukianov.homeprotect.ui.area.AreaGui;
import net.alukianov.homeprotect.ui.area.FlagsGui;
import net.alukianov.homeprotect.ui.area.MembersGui;
import net.alukianov.homeprotect.ui.player.PlayerAreasGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.alukianov.homeprotect.util.Utils.*;

@CommandAlias("homeprotect|hp|home")
public class HomeProtectCmd extends BaseCommand {

    public static final String[] COMMANDS_NAME = {"help"};

    private final HomeProtect plugin;
    private final AreaManager manager;

    public HomeProtectCmd(@NotNull HomeProtect plugin) {
        this.plugin = plugin;
        manager = plugin.areaManager();
    }

    @Subcommand("help")
    @CommandPermission("homeprotect.help")
    public static void onHelp(@NotNull Player player, @Single @Default("1") @NotNull String page) {
        if (page.equals("1")) {
            player.sendRawMessage("1");

        } else if (page.equals("2")) {

            player.sendRawMessage("2");
        }
    }

    @CatchUnknown
    @Default
    @CommandPermission("homeprotect.area")
    public void onArea(@NotNull Player player, @Single String name) {
        Area area = plugin.areaManager().areaByName(name);
        if (area != null) {
            AreaGui areaGui = new AreaGui(plugin, area);
            player.openInventory(areaGui.getInventory());
            return;
        }
        player.sendRawMessage(header() + "This area does not exist");
    }

    @Subcommand("areas")
    public void onPlayerAreas(@NotNull Player player, @Single @Default("self") @NotNull String name) {
        if (name.equals("self")) {
            player.openInventory(new PlayerAreasGui(player, plugin).getInventory());
        }
    }

    @Subcommand("info")
    @CommandPermission("homeprotect.info")
    public void onInfo(@NotNull Player player) {
        Area currentArea = plugin.areaManager().areaByLocation(player.getLocation());
        if (currentArea != null) {
            player.sendRawMessage(color("&3--------------------------------------------------"));
            player.sendRawMessage(color("&7Name:&3&l " + currentArea.name()));
            player.sendRawMessage(color("&7Owner:&3 " + getPlayer(currentArea.ownerUUID()).getName()));
            player.sendRawMessage(color("&7Members: (&3ALL&7)"));
            player.sendRawMessage(color("&7Flags: (&3none&7)"));
            player.sendRawMessage(color("&7Warp point: (&30&7, &30&7, &30&7) [TELEPORT]"));
        } else {
            player.sendRawMessage(color("&3--------------------------------------------------"));
            player.sendRawMessage(color("&7Name:&3&l GLOBAL"));
            player.sendRawMessage(color("&7Owner: (&3none&7)"));
            player.sendRawMessage(color("&7Members: (&3ALL&7)"));
            player.sendRawMessage(color("&7Flags: (&3none&7)"));
            player.sendRawMessage(color("&7Warp point: (&30&7, &30&7, &30&7) [TELEPORT]"));
        }
    }

    @Subcommand("create|ct")
    @CommandPermission("homeprotect.create")
    public void onCreate(@NotNull Player player, String name, int size) {
        if (manager.canCreateArea(player)) {
            if (manager.isNameUnique(name) && Area.isCorrectName(name)) {
                Area area = new Area(name, player.getUniqueId().toString(),
                        player.getWorld(), player.getLocation(), size);
                if (manager.isAreaNonIntersect(area)) {
                    manager.addArea(area);

                    sound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 0.7f);
                    sound(player, Sound.ENTITY_CAT_AMBIENT, 1f, 1f);
                    title(player, "&7Area &3&l" + area.name(), "&7Successful &a&lcreated");

                    return;
                }
                player.sendRawMessage(color(header() +
                        " &7You cannot create an area because there is another one nearby"));
                return;
            }
            player.sendRawMessage(color(header() +
                    " &7You entered the wrong name, or it is already in use"));
            return;
        }
        player.sendRawMessage(color(header() +
                " &7You have reached the maximum number of areas"));
    }

    @Subcommand("delete|dlt")
    @CommandPermission("homeprotect.delete")
    public void onDelete(Player player, @Single String name) {
        Area area = plugin.areaManager().areaByName(name);
        if (area != null) {
            if (player.getUniqueId().toString().equals(area.ownerUUID())) {
                plugin.areaManager().removeArea(area);

                sound(player, Sound.ITEM_TOTEM_USE, 0.5f, 0.7f);
                sound(player, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.5f, 0.5f);
                title(player, "&7Area &3&l" + area.name(), "&7Has been &c&ldeleted");

                return;
            }
        }
        player.sendRawMessage(color(header() + " This area does not exist"));
    }

    @Subcommand("members|m")
    @CommandPermission("homeprotect.members")
    public void onMembers(Player player, @Single String name) {
        Area area = plugin.areaManager().areaByName(name);
        if (area != null) {
            if (player.getUniqueId().toString().equals(area.ownerUUID())) {
                player.openInventory(new MembersGui(plugin, area).getInventory());
                return;
            }
        }
        player.sendRawMessage(color(header() + " This area does not exist"));
    }

    @Subcommand("addmember|addm")
    @CommandPermission("homeprotect.members.add")
    public void addMember(Player player, String areaName, String playerName) {
        Area area = plugin.areaManager().areaByName(areaName);
        if (area != null && area.isAddMember()) {
            if (player.getUniqueId().toString().equals(area.ownerUUID())) {
                OfflinePlayer member = Bukkit.getOfflinePlayer(playerName);
                if (member != null && member.hasPlayedBefore()) {
                    area.addMember(member.getUniqueId().toString());
                } else {
                    player.sendRawMessage("This player is exist!");
                }
                return;
            }
        }
        player.sendRawMessage(color(header() + " This area does not exist"));

    }

    @Subcommand("delmember|delm")
    @CommandPermission("homeprotect.members.del")
    public void delMember(Player player, String areaName, String playerName) {
        Area area = plugin.areaManager().areaByName(areaName);
        if (area != null) {
            if (player.getUniqueId().toString().equals(area.ownerUUID())) {
                if (area.removeMember(Bukkit.getOfflinePlayer(playerName).getUniqueId().toString())) {
                    player.sendRawMessage("good");
                } else {
                    player.sendRawMessage("This player is exist!");
                }
                return;
            }
        }
        player.sendRawMessage(color(header() + " This area does not exist"));

    }

    @Subcommand("flags|fl")
    @CommandPermission("homeprotect.flags")
    public void onFlags(Player player, String name) {
        Area area = plugin.areaManager().areaByName(name);
        if (area != null) {
            FlagsGui flags = new FlagsGui(plugin, area);
            player.openInventory(flags.getInventory());
        }
    }

}
