package com.voxelgameslib.voxelgameslib.command.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.voxelgameslib.voxelgameslib.lang.Lang;
import com.voxelgameslib.voxelgameslib.lang.LangKey;
import com.voxelgameslib.voxelgameslib.map.Map;
import com.voxelgameslib.voxelgameslib.user.User;
import com.voxelgameslib.voxelgameslib.world.WorldHandler;
import net.kyori.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Commands related to worlds
 */
@Singleton
@CommandAlias("world")
@SuppressWarnings("JavaDoc") // commands don't need javadoc, go read the command's descriptions
public class WorldCommands extends BaseCommand {

    //TODO world command completer

    @Inject
    private WorldHandler handler;


    @Default
    @UnknownHandler
    @Subcommand("help")
    @CommandPermission("%admin")
    public void world(User sender) {
        //TODO remove me once GH-17 is implemented
        sender.sendMessage(TextComponent.of("It works! You are on " + sender.getPlayer().getLocation().getWorld()));
    }

    @Subcommand("load")
    @CommandPermission("%admin")
    @Syntax("<mapname> - the name of the map to load")
    public void load(User sender, String mapName) {
        Optional<Map> o = handler.getMap(mapName);
        Map map = o.orElseGet(() -> handler.loadMap(mapName));

        handler.loadWorld(map, sender.getUuid(), false);
    }

    @Subcommand("unload")
    @CommandPermission("%admin")
    @Syntax("<mapname> - the name of the map to unload")
    public void unload(User sender, String mapName) {
        Optional<Map> o = handler.getMap(mapName);
        if (o.isPresent()) {
            handler.unloadWorld(o.get(), sender.getUuid());
        } else {
            Lang.msg(sender, LangKey.WORLD_UNKNOWN_MAP, mapName);
        }
    }

    @Subcommand("loadlocal")
    @CommandPermission("%admin")
    @Syntax("<world> - the name of the world to load")
    public void loadLocal(User sender, String world) {
        handler.loadLocalWorld(world);
    }

    @Subcommand("unloadlocal")
    @CommandPermission("%admin")
    @Syntax("<world> - the name of the world to unload")
    public void unloadLocal(User sender, String world) {
        handler.unloadLocalWorld(world);
    }

    @Subcommand("tp")
    @CommandPermission("%admin")
    @Syntax("<world> - the name of the world to tp to")
    public void tp(User sender, String world) {
        Optional<Map> o = handler.getMap(world);
        if (o.isPresent()) {
            Map map = o.get();
            sender.getPlayer().teleport(map.getCenter().toLocation(map.getLoadedName(sender.getUuid())));
        } else {
            World w = Bukkit.getWorld(world);
            if (w != null) {
                sender.getPlayer().teleport(Bukkit.getWorld(world).getSpawnLocation());
            }
            Lang.msg(sender, LangKey.WORLD_UNKNOWN_MAP, world);
        }
    }
}