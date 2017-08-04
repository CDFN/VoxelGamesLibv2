package com.voxelgameslib.voxelgameslib.world;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Singleton;
import com.voxelgameslib.voxelgameslib.game.GameHandler;
import com.voxelgameslib.voxelgameslib.game.GameMode;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.map.Map;
import com.voxelgameslib.voxelgameslib.map.MapInfo;
import com.voxelgameslib.voxelgameslib.map.Vector3D;
import com.voxelgameslib.voxelgameslib.user.User;
import lombok.extern.java.Log;
import net.kyori.text.TextComponent;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.format.TextColor;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles creation of new worlds/maps
 */
@Log
@Singleton
@CommandAlias("worldcreator|wc")
@SuppressWarnings("JavaDoc") // commands don't need javadoc, go read the command's descriptions
public class WorldCreator extends BaseCommand {

    //TODO world creator completer

    @Inject
    private WorldHandler worldHandler;

    @Inject
    private GameHandler gameHandler;

    private User editor;

    private int step = 0;

    private String worldName;
    private Vector3D center;
    private int radius;
    private String displayName;
    private String author;
    private List<String> gameModes;

    private Map map;

    @CommandPermission("%admin")
    public void worldcreator(User sender) {
        Lang.msg(sender, LangKey.WORLD_CREATOR_INFO);
    }

    @Subcommand("start")
    @CommandPermission("%admin")
    public void start(User sender) {
        if (editor != null) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_IN_USE,
                    editor.getDisplayName());
            return;
        }

        editor = sender;
        gameModes = new ArrayList<>();

        sender.sendMessage(Lang.trans(LangKey.WORLD_CREATOR_ENTER_WORLD_NAME,
                sender.getLocale())
                .clickEvent((new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/worldcreator world "))));

        step = 1;
    }

    @Subcommand("world")
    @CommandPermission("%admin")
    public void world(User sender, String worldName) {
        if (step != 1) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 1);
            return;
        }

        this.worldName = worldName;

        worldHandler.loadLocalWorld(worldName);
        sender.getPlayer().teleport(Bukkit.getWorld(worldName).getSpawnLocation());

        sender.sendMessage(
                Lang.trans(LangKey.WORLD_CREATOR_ENTER_CENTER, sender.getLocale())
                        .clickEvent((new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldcreator center"))));

        step = 2;
    }

    @Subcommand("center")
    @CommandPermission("%admin")
    public void center(User sender) {
        if (step != 2) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 2);
            return;
        }

        center = new Vector3D(sender.getPlayer().getLocation().getX(),
                sender.getPlayer().getLocation().getY(), sender.getPlayer().getLocation().getZ());

        sender.sendMessage(
                Lang.trans(LangKey.WORLD_CREATOR_ENTER_RADIUS, sender.getLocale())
                        .clickEvent(
                                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/worldcreator radius ")));

        step = 3;
    }

    @Subcommand("radius")
    @CommandPermission("%admin")
    public void radius(User sender, int radius) {
        if (step != 3) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 3);
            return;
        }

        this.radius = radius;

        sender.sendMessage(Lang.trans(LangKey.WORLD_CREATOR_ENTER_DISPLAY_NAME,
                sender.getLocale())
                .clickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/worldcreator name ")));

        step = 4;
    }

    @Subcommand("name")
    @CommandPermission("%admin")
    public void name(User sender, String name) {
        if (step != 4) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 4);
            return;
        }

        displayName = name;

        sender.sendMessage(
                Lang.trans(LangKey.WORLD_CREATOR_ENTER_AUTHOR, sender.getLocale(),
                        displayName)
                        .clickEvent(
                                new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/worldcreator author ")));

        step = 5;
    }

    @Subcommand("author")
    @CommandPermission("%admin")
    public void author(User sender, String author) {
        if (step != 5) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 5);
            return;
        }

        this.author = author;

        Lang.msg(sender, LangKey.WORLD_CREATOR_AUTHOR_SET, author);
        for (GameMode mode : gameHandler.getGameModes()) {
            sender
                    .sendMessage(TextComponent.of(mode.getName() + " ").color(TextColor.YELLOW)
                            .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                    "/worldcreator gamemode " + mode.getName())));
        }

        sender.sendMessage(
                Lang.trans(LangKey.WORLD_CREATOR_DONE_QUERY, sender.getLocale())
                        .clickEvent(
                                new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldcreator gamemode done")));

        step = 6;
    }

    @Subcommand("gamemode")
    @CommandPermission("%admin")
    public void gamemode(User sender, String gamemode) {
        if (step != 6) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 6);
            return;
        }

        if (gamemode.equalsIgnoreCase("done")) {
            sender.sendMessage(Lang.trans(LangKey.WORLD_CREATOR_EDIT_MODE_ON,
                    sender.getLocale())
                    .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldcreator edit on")));
            sender.sendMessage(Lang.trans(LangKey.WORLD_CREATOR_EDIT_MODE_OFF,
                    sender.getLocale())
                    .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldcreator edit off")));
            step = 7;
        } else {
            gameModes.add(gamemode);
            Lang.msg(sender, LangKey.WORLD_CREATOR_GAMEMODE_ADDED);
        }
    }

    @Subcommand("edit")
    @CommandPermission("%admin")
    //TODO might want to replace onOff with boolean in the future
    public void edit(User sender, String onOff) {
        if (step != 7) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 7);
            return;
        }

        if (onOff.equalsIgnoreCase("on")) {
            sender.getPlayer().performCommand("editmode on");
        } else if (onOff.equalsIgnoreCase("off")) {
            sender.getPlayer().performCommand("editmode off");
            MapInfo info = new MapInfo(displayName, author, gameModes);
            map = new Map(info, worldName, center, radius);
            map.load(sender.getUuid(), worldName);
            map.printSummary(sender);
            sender.sendMessage(
                    Lang.trans(LangKey.WORLD_CREATOR_DONE_QUERY, sender.getLocale())
                            .clickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/worldcreator done")));
            step = 8;
        } else {
            Lang.msg(sender, LangKey.GENERAL_INVALID_ARGUMENT, onOff);
        }
    }

    @Subcommand("done")
    @CommandPermission("%admin")
    public void done(User sender) {
        if (step != 8) {
            Lang.msg(sender, LangKey.WORLD_CREATOR_WRONG_STEP, step, 8);
            return;
        }

        worldHandler.finishWorldEditing(editor, map);

        editor = null;
        step = 0;
        worldName = null;
        center = null;
        radius = -1;
        displayName = null;
        author = null;
        gameModes = new ArrayList<>();
    }
}