package io.github.tavstal.portallock.commands;

import io.github.tavstal.minecorelib.core.PluginLogger;
import io.github.tavstal.minecorelib.utils.ChatUtils;
import io.github.tavstal.portallock.PortalLock;
import io.github.tavstal.portallock.models.ConfigField;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.EFieldType;
import io.github.tavstal.portallock.models.ValueEditor;
import io.github.tavstal.portallock.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * CommandPortalLock class handles the execution of the PortalLock commands.
 */
public class CommandPortalLock implements CommandExecutor {
    private final PluginLogger _logger = PortalLock.Logger().WithModule(CommandPortalLock.class);
    
    /**
     * Executes the given command, returning its success.
     *
     * @param sender  The source of the command.
     * @param command The command which was executed.
     * @param label   The alias of the command which was used.
     * @param args    The arguments passed to the command.
     * @return true if the command was successful, otherwise false.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            _logger.Info(ChatUtils.translateColors("Commands.ConsoleCaller", true).toString());
            return true;
        }
        Player player = (Player) sender;
        var translator = PortalLock.Translator();

        if (!player.hasPermission("portallock.commands.portallock")) {
            PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
            return true;
        }

        try
        {
            if (args.length == 0) {
                ExecuteHelp(player);
                return true;
            }

            String arg = args[0].toLowerCase();
            switch (arg) {
                case "help": {
                    ExecuteHelp(player);
                    return true;
                }
                case "reload": {
                    if (!player.hasPermission("portallock.commands.portallock.reload")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    PortalLock.Instance.reload();
                    return true;
                }
                case "version": {
                    if (!player.hasPermission("portallock.commands.portallock.version")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("version", PortalLock.Instance.getVersion());
                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.Version.Current", parameters);

                    boolean isUpToDate = PortalLock.Instance.isUpToDate();
                    parameters = new HashMap<>();
                    if (isUpToDate) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Version.UpToDate");
                    } else {
                        parameters.put("link", PortalLock.Instance.getDownloadUrl());
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Version.Outdated");
                    }
                    return true;
                }
                case "list": {
                    if (!player.hasPermission("portallock.commands.portallock.list")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.List.Title");

                    int page = 1;
                    if (args.length > 1)
                        page = Integer.parseInt(args[1]);

                    var worlds = PortalLock.Instance.getServer().getWorlds();

                    boolean reachedEnd = false;
                    int maxPage = 1 + (worlds.size() / 15);
                    for (int i = 0; i < 15; i++) {
                        int index = i + (page - 1) * 15;

                        if (index >= worlds.size()) {
                            reachedEnd = true;
                            break;
                        }

                        World world = worlds.get(index);
                        DimensionData dimensionData = null;
                        for (var dim : DimensionUtils.Dimensions) {
                            if (dim.Key.equals(world.getName())) {
                                dimensionData = dim;
                                break;
                            }
                        }

                        if (dimensionData == null) {
                            String dimensionName = world.getName();
                            player.sendMessage(ChatUtils.buildWithButtons(translator.Localize(player,"Commands.List.Line"), new HashMap<>() {{
                                put("dimension", ChatUtils.translateColors(dimensionName, true));
                                put("button1", ChatUtils.translateColors(translator.Localize(player,"Commands.List.AddBtn"), true)
                                        .clickEvent(ClickEvent.runCommand("/portallock add " + world.getName())));
                                put("button2", Component.empty());
                            }}));
                        }
                        else {
                            String dimensionName = dimensionData.DisplayName;
                            player.sendMessage(ChatUtils.buildWithButtons(translator.Localize(player,"Commands.List.Line"), new HashMap<>() {{
                                put("dimension", ChatUtils.translateColors(dimensionName, true));
                                put("button1", ChatUtils.translateColors(translator.Localize(player,"Commands.List.InfoBtn"), true)
                                        .clickEvent(ClickEvent.runCommand("/portallock info " + world.getName())));
                                put("button2", ChatUtils.translateColors(translator.Localize(player,"Commands.List.RemoveBtn"), true)
                                        .clickEvent(ClickEvent.runCommand("/portallock remove " + world.getName())));
                            }}));
                        }
                    }

                    // Bottom message
                    Component prevBtn;
                    Component nextBtn;
                    if (page > 1)
                        prevBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.List.PrevBtn"), true)
                                .clickEvent(ClickEvent.runCommand("/portallock list " + (page - 1)));
                    else
                        prevBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.List.PrevBtn"), true);
                    if (!reachedEnd && maxPage >= page + 1)
                        nextBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.List.NextBtn"), true)
                                .clickEvent(ClickEvent.runCommand("/portallock list " + (page + 1)));
                    else
                        nextBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.List.NextBtn"), true);

                    int finalPage = page;
                    player.sendMessage(ChatUtils.buildWithButtons(translator.Localize(player,"Commands.List.Bottom"), new HashMap<>() {{
                        put("prev_btn", prevBtn);
                        put("current_page", Component.text(finalPage));
                        put("max_page", Component.text(maxPage));
                        put("next_btn", nextBtn);
                    }}));

                    return true;
                }
                case "info": {
                    if (!player.hasPermission("portallock.commands.portallock.info")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    if (args.length < 2 || args[1].isEmpty()) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Info.Usage");
                        return true;
                    }

                    DimensionData dimensionData = null;
                    for (var dim : DimensionUtils.Dimensions) {
                        if (dim.Key.equals(args[1])) {
                            dimensionData = dim;
                            break;
                        }
                    }

                    if (dimensionData == null) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.DimensionNotFound", new HashMap<>() {{
                            put("dimension", args[1]);
                        }});
                        return true;
                    }

                    int page = 1;
                    if (args.length > 2)
                        page = Integer.parseInt(args[2]);
                    if (page < 1)
                        page = 1;

                    String dimensionKey = dimensionData.Key;
                    String dimensionName = dimensionData.DisplayName;
                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.Info.Title", new HashMap<>() {{
                        put("dimension", dimensionName);
                    }});

                    List<Field> fields = Arrays.stream(dimensionData.getClass().getFields())
                            .filter(x -> x.isAnnotationPresent(ValueEditor.class))
                            .sorted(Comparator.comparingInt(x -> x.getAnnotation(ConfigField.class).order()))
                            .toList();
                    boolean reachedEnd = false;
                    int maxPage = 1 + (fields.size() / 15);

                    for (int i = 0; i < 15; i++) {
                        int index = i + 15 * (page - 1);
                        if (index >= fields.size()) {
                            reachedEnd = true;
                            break;
                        }

                        Field field = fields.get(index);
                        field.setAccessible(true);
                        /*if (!field.isAnnotationPresent(ValueEditor.class))
                            continue;*/

                        ValueEditor annotation = field.getAnnotation(ValueEditor.class);
                        Map<String, Component> parameters = new HashMap<>() {{
                            put("variable", Component.text(field.getName()));
                        }};

                        switch (annotation.type()) {
                            case TEXT, NUMBER, WORLD_KEY, DATETIME -> {
                                String btnValue;
                                var fieldValue = field.get(dimensionData);

                                if (fieldValue == null) {
                                    btnValue = translator.Localize(player,"Commands.Info.Inactive")
                                            .replace("%value%", translator.Localize(player,
                                                    annotation.type() == EFieldType.WORLD_KEY ? "Commands.Info.ActionWorld" : "Commands.Info.ActionCustom")
                                            );
                                }
                                else {
                                    btnValue = translator.Localize(player,"Commands.Info.Active")
                                            .replace("%value%",
                                                    fieldValue.toString().length() > 10 ? fieldValue.toString().substring(0, 10) + "..." : fieldValue.toString()
                                            );
                                }

                                parameters.put("button1", ChatUtils.translateColors(btnValue, true)
                                        .clickEvent(ClickEvent.suggestCommand(
                                        MessageFormat.format("/portallock edit {0} {1} ", dimensionKey, field.getName())
                                )));
                                parameters.put("button2", Component.empty());
                            }
                            case BOOLEAN -> {
                                String trueBtnLocale;
                                String falseBtnLocale;
                                var fieldValue = field.get(dimensionData);
                                if (fieldValue == null) {
                                    trueBtnLocale = "Commands.Info.Inactive";
                                    falseBtnLocale = "Commands.Info.Inactive";
                                }
                                else {
                                    if ((Boolean)fieldValue) {
                                        trueBtnLocale = "Commands.Info.Active";
                                        falseBtnLocale = "Commands.Info.Inactive";
                                    }
                                    else {
                                        trueBtnLocale = "Commands.Info.Inactive";
                                        falseBtnLocale = "Commands.Info.Active";
                                    }
                                }

                                String trueBtnValue = translator.Localize(player,trueBtnLocale).replace("%value%", translator.Localize(player,"Commands.Info.ActionAllow"));
                                String falseBtnValue = translator.Localize(player,falseBtnLocale).replace("%value%", translator.Localize(player,"Commands.Info.ActionDeny"));

                                parameters.put("button1", ChatUtils.translateColors(trueBtnValue, true)
                                        .clickEvent(ClickEvent.runCommand(
                                                MessageFormat.format("/portallock edit {0} {1} true",dimensionKey, field.getName())
                                        )));
                                parameters.put("button2", ChatUtils.translateColors(falseBtnValue, true)
                                        .clickEvent(ClickEvent.runCommand(
                                                MessageFormat.format("/portallock edit {0} {1} false", dimensionKey, field.getName())
                                        )));
                            }
                        }

                        player.sendMessage(ChatUtils.buildWithButtons(translator.Localize(player,"Commands.Info.Line"), parameters));
                    }

                    // Bottom message
                    Component prevBtn;
                    Component nextBtn;
                    if (page > 1)
                        prevBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.Info.PrevBtn"), true)
                                .clickEvent(ClickEvent.runCommand("/portallock info " + dimensionKey + " " + (page - 1)));
                    else
                        prevBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.Info.PrevBtn"), true);
                    if (!reachedEnd && maxPage >= page + 1)
                        nextBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.Info.NextBtn"), true)
                                .clickEvent(ClickEvent.runCommand("/portallock info " + dimensionKey + " " + (page + 1)));
                    else
                        nextBtn = ChatUtils.translateColors(translator.Localize(player,"Commands.Info.NextBtn"), true);

                    int finalPage = page;
                    player.sendMessage(ChatUtils.buildWithButtons(translator.Localize(player,"Commands.Info.Bottom"), new HashMap<>() {{
                        put("prev_btn", prevBtn);
                        put("current_page", Component.text(finalPage));
                        put("max_page", Component.text(maxPage));
                        put("next_btn", nextBtn);
                    }}));

                    break;
                }
                case "add": {
                    if (!player.hasPermission("portallock.commands.portallock.add")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    if (args.length != 2 ) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Add.Usage");
                        return true;
                    }

                    DimensionData dimensionData = null;
                    for (var dimension : DimensionUtils.Dimensions) {
                        if (dimension.Key.equalsIgnoreCase(args[1])) {
                            dimensionData = dimension;
                            break;
                        }
                    }

                    if (dimensionData != null) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Add.AlreadyExist", new HashMap<>() {{
                            put("dimension", args[1]);
                        }});
                        return true;
                    }

                    String displayName;
                    if (args[1].contains(":"))
                        displayName = args[1].split(":")[1];
                    else
                        displayName = args[1];
                    DimensionUtils.Dimensions.add(new DimensionData(args[1], displayName, false, "2024-01-01 00:00", "2024-01-01 00:00",
                                    true, true, "minecraft:overworld",
                                    true, false, args[1].replaceAll(":", ".") + ".enter", true, false, args[1].replaceAll(":", ".") + ".leave"
                            )
                    );
                    DimensionUtils.SaveConfig();
                    player.sendMessage(ChatUtils.translateColors(translator.Localize(player,"Commands.Add.Success").replace("%dimension%", displayName), true));

                    break;
                }
                case "edit": {
                    if (!player.hasPermission("portallock.commands.portallock.edit")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    if (args.length < 4) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.Usage");
                        return true;
                    }

                    DimensionData dimensionData = null;
                    int index = 0;
                    for (var dimension : DimensionUtils.Dimensions) {
                        if (dimension.Key.equalsIgnoreCase(args[1])) {
                            dimensionData = dimension;
                            break;
                        }
                        index++;
                    }

                    if (dimensionData == null) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.DimensionNotFound", new HashMap<>() {{
                            put("dimension", args[1]);
                        }});
                        return true;
                    }

                    List<Field> fields = Arrays.stream(dimensionData.getClass().getFields()).filter(x -> x.isAnnotationPresent(ValueEditor.class)).toList();
                    Field field = null;
                    for (var f : fields) {
                        if (f.getName().equals(args[2])) {
                            field = f;
                            break;
                        }
                    }

                    if (field == null) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.InvalidField", new HashMap<>() {{
                            put("value", args[2]);
                        }});
                        return true;
                    }

                    String newValue = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
                    field.setAccessible(true);
                    ValueEditor annotation = field.getAnnotation(ValueEditor.class);
                    switch (annotation.type()) {
                        case BOOLEAN -> {
                            switch (newValue.toLowerCase()) {
                                case "true", "on", "yes", "1" -> field.set(dimensionData, true);
                                case "false", "off", "no", "0" -> field.set(dimensionData, false);
                                default -> {
                                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.InvalidBoolean", new HashMap<>() {{
                                        put("value", newValue);
                                    }});
                                    return true;
                                }
                            }
                            break;
                        }
                        case TEXT -> field.set(dimensionData, newValue);
                        case DATETIME -> {
                            try {
                                LocalDateTime.parse(newValue, PortalLock.DateFormatter);
                                // If the date is invalid then it won't be set.
                                field.set(dimensionData, newValue);
                            }
                            catch (Exception ex) {
                                PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.InvalidDate", new HashMap<>() {{
                                    put("value", newValue);
                                }});
                                return true;
                            }
                            break;
                        }
                        case NUMBER -> {
                            if (MathUtils.isInt(newValue) && field.getType() == int.class) {
                                field.set(dimensionData, Integer.parseInt(newValue));
                            }
                            else if (MathUtils.isFloat(newValue) && field.getType() == float.class) {
                                field.set(dimensionData, Float.parseFloat(newValue));
                            }
                            else if (MathUtils.isByte(newValue) && field.getType() == byte.class) {
                                field.set(dimensionData, Byte.parseByte(newValue));
                            }
                            else if (MathUtils.isDecimal(newValue) && field.getType() == double.class) {
                                field.set(dimensionData, Double.parseDouble(newValue));
                            }
                            else
                            {
                                PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.InvalidNumber", new HashMap<>() {{
                                    put("value", newValue);
                                }});
                                return true;
                            }
                            break;
                        }
                        case WORLD_KEY -> {
                            boolean isValid = false;
                            for (var world : PortalLock.Instance.getServer().getWorlds()) {
                                if (world.getName().equals(newValue)) {
                                    isValid = true;
                                    break;
                                }
                            }

                            if (!isValid) {
                                PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.InvalidWorld", new HashMap<>() {{
                                    put("value", newValue);
                                }});
                                return true;
                            }

                            field.set(dimensionData, newValue);
                            break;
                        }
                    }

                    DimensionUtils.Dimensions.set(index, dimensionData);
                    DimensionUtils.SaveConfig();
                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.Edit.Success");

                    break;
                }
                case "remove": {
                    if (!player.hasPermission("portallock.commands.portallock.remove")) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    if (args.length != 2 ) {
                        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Remove.Usage");
                        return true;
                    }

                    DimensionData dimensionData = null;
                    for (var dimension : DimensionUtils.Dimensions) {
                        if (dimension.Key.equalsIgnoreCase(args[1])) {
                            dimensionData = dimension;
                            break;
                        }
                    }

                    if (dimensionData == null) {
                        PortalLock.Instance.sendLocalizedMsg(player, "General.DimensionNotFound", new HashMap<>() {{
                            put("dimension", args[1]);
                        }});
                        return true;
                    }

                    DimensionUtils.Dimensions.remove(dimensionData);
                    DimensionUtils.SaveConfig();
                    PortalLock.Instance.sendLocalizedMsg(player, "Commands.Remove.Success", new HashMap<>() {{
                        put("dimension", args[1]);
                    }});

                    break;
                }
            }
        }
        catch (Exception ex) {
            PortalLock.Instance.sendLocalizedMsg(player, "General.UnknownError");
            _logger.Warn("Error while executing portallock command:");
            _logger.Error(ex.getMessage());
        }

        return true;
    }

    /**
     * Sends the help message to the player.
     *
     * @param player The player to send the help message to.
     */
    private void ExecuteHelp(Player player) {
        var translator = PortalLock.Translator();
        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Title", new HashMap<>() {{
            put("page", 1);
            put("maxpage", 1);
        }});
        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Info");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("subcommand", "help");
        parameters.put("syntax", translator.Localize(player,"Commands.Help.Syntax"));
        parameters.put("description", translator.Localize(player,"Commands.Help.Desc"));

        PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        if (player.hasPermission("portallock.commands.portallock.reload")) {
            parameters.put("subcommand", "reload");
            parameters.put("syntax", "");
            parameters.put("description", translator.Localize(player,"Commands.Reload.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }
        if (player.hasPermission("portallock.commands.portallock.version")) {
            parameters.put("subcommand", "version");
            parameters.put("syntax", "");
            parameters.put("description", translator.Localize(player,"Commands.Version.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }

        if (player.hasPermission("portallock.commands.portallock.list")) {
            parameters.put("subcommand", "list");
            parameters.put("syntax", translator.Localize(player,"Commands.List.Syntax"));
            parameters.put("description", translator.Localize(player,"Commands.List.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }

        if (player.hasPermission("portallock.commands.portallock.info")) {
            parameters.put("subcommand", "info");
            parameters.put("syntax", translator.Localize(player,"Commands.Info.Syntax"));
            parameters.put("description", translator.Localize(player,"Commands.Info.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }

        if (player.hasPermission("portallock.commands.portallock.add")) {
            parameters.put("subcommand", "add");
            parameters.put("syntax", translator.Localize(player,"Commands.Add.Syntax"));
            parameters.put("description", translator.Localize(player,"Commands.Add.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }

        if (player.hasPermission("portallock.commands.portallock.edit")) {
            parameters.put("subcommand", "edit");
            parameters.put("syntax", translator.Localize(player,"Commands.Edit.Syntax"));
            parameters.put("description", translator.Localize(player,"Commands.Edit.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }

        if (player.hasPermission("portallock.commands.portallock.remove")) {
            parameters.put("subcommand", "remove");
            parameters.put("syntax", translator.Localize(player,"Commands.Remove.Syntax"));
            parameters.put("description", translator.Localize(player,"Commands.Remove.Desc"));
            PortalLock.Instance.sendLocalizedMsg(player, "Commands.Help.Line", parameters);
        }
    }
}
