package io.github.tavstaldev.portallock.commands;

import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.portallock.PortalLock;
import io.github.tavstaldev.portallock.models.DimensionData;
import io.github.tavstaldev.portallock.models.ValueEditor;
import io.github.tavstaldev.portallock.utils.DimensionUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CommandPortalLockCompleter class provides tab completion for the PortalLock commands.
 */
public class CommandPortalLockCompleter implements TabCompleter {
    private final PluginLogger _logger = PortalLock.Logger().WithModule(CommandPortalLockCompleter.class);

    /**
     * Handles tab completion for the PortalLock commands.
     *
     * @param sender  The sender of the command.
     * @param command The command being executed.
     * @param alias   The alias used for the command.
     * @param args    The arguments passed to the command.
     * @return A list of possible completions for the final argument, or null to default to the standard list.
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        try {
            if (sender instanceof ConsoleCommandSender) {
                return null;
            }
            Player player = (Player) sender;
            var server = PortalLock.Instance.getServer();
            List<String> commandList = new ArrayList<>();

            switch (args.length) {
                case 0:
                case 1: {
                    commandList.add("help");
                    if (player.hasPermission("portallock.commands.portallock.version"))
                        commandList.add("version");
                    if (player.hasPermission("portallock.commands.portallock.reload"))
                        commandList.add("reload");
                    if (player.hasPermission("portallock.commands.portallock.list"))
                        commandList.add("list");
                    if (player.hasPermission("portallock.commands.portallock.info"))
                        commandList.add("info");
                    if (player.hasPermission("portallock.commands.portallock.add"))
                        commandList.add("add");
                    if (player.hasPermission("portallock.commands.portallock.remove"))
                        commandList.add("remove");
                    if (player.hasPermission("portallock.commands.portallock.edit"))
                        commandList.add("edit");

                    commandList.removeIf(cmd -> !cmd.toLowerCase().startsWith(args[0].toLowerCase()));
                    break;
                }
                case 2: {
                    switch (args[0].toLowerCase()) {
                        case "list": {
                            commandList.add("1");
                            commandList.add("5");
                            commandList.add("10");
                            break;
                        }
                        case "add": {
                            for (var world : server.getWorlds()) {
                                commandList.add(world.getName());
                            }
                            break;
                        }
                        case "info":
                        case "edit":
                        case "remove": {
                            for (var world : DimensionUtils.Dimensions) {
                                commandList.add(world.Key);
                            }
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (args[0].toLowerCase()) {
                        case "info": {
                            commandList.add("1");
                            commandList.add("5");
                            commandList.add("10");
                            break;
                        }
                        case "edit": {
                            Field[] fields = DimensionData.class.getFields();

                            for (Field field : fields) {
                                if (!field.isAnnotationPresent(ValueEditor.class))
                                    continue;

                                commandList.add(field.getName());
                            }

                            commandList.removeIf(cmd -> !cmd.toLowerCase().startsWith(args[2].toLowerCase()));
                            break;
                        }
                    }
                    break;
                }
                case 4: {
                    // Maybe add some more options here in the future
                    break;
                }
            }

            Collections.sort(commandList);
            return commandList;
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while trying to tab complete the portallock command.");
            _logger.Error(ex.getMessage());
            return new ArrayList<>();
        }
    }
}
