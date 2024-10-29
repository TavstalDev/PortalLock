package io.github.tavstal.portallock.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.tavstal.portallock.CommonClass;
import io.github.tavstal.portallock.CommonConfig;
import io.github.tavstal.portallock.Translations;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.EFieldType;
import io.github.tavstal.portallock.models.InteractableChatComponent;
import io.github.tavstal.portallock.models.ValueEditor;
import io.github.tavstal.portallock.utils.EntityUtils;
import io.github.tavstal.portallock.utils.ModUtils;
import io.github.tavstal.portallock.utils.WorldUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PortalLockCommand {

    public static final String Name = "portallock";
    public static final String Syntax = "add | edit | remove | list | info";
    public static final String[] Aliases = new String[] { "pl", "portall", "plock" };
    public static final Integer PermissionLevel = 2;

    /**
     * Registers commands with the given command dispatcher.
     * <p>
     * This method sets up the command structure and defines how commands
     * can be executed by players. It also includes subcommands for various
     * operations related to the main command.
     * </p>
     * @param dispatcher The command dispatcher used to register the commands.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        // Create the command itself
        LiteralCommandNode<CommandSourceStack> commandNode = dispatcher.register(
                Commands.literal(Name) // Create the command
                        .requires(source -> source.hasPermission(PermissionLevel)) // Check permission
                        //#region Add subcommand
                        .then(Commands.literal("add").then(Commands.argument("worldKey", StringArgumentType.string())
                                .suggests(PortalLockCommand::suggestWorldKeys)
                                .executes(PortalLockCommand::executeAdd) // Executes if the arg is present
                            ).executes(PortalLockCommand::executeAddSyntax) // Executes if the arg is not present
                        )
                        //#endregion
                        //#region Edit subcommand
                        .then(Commands.literal("edit").then(Commands.argument("worldKey", StringArgumentType.string())
                                .suggests(PortalLockCommand::suggestWorldKeys)
                                .then(Commands.argument("variable", StringArgumentType.word())
                                        .suggests(PortalLockCommand::suggestDimensionFields)
                                        .then(Commands.argument("newValue", StringArgumentType.string())
                                                .executes(PortalLockCommand::executeEdit)
                                        ).executes(PortalLockCommand::executeEditSyntax)
                                ).executes(PortalLockCommand::executeEditSyntax)
                            ).executes(PortalLockCommand::executeEditSyntax)
                        )
                        //#endregion
                        //#region Remove subcommand
                        .then(Commands.literal("remove").then(Commands.argument("worldKey", StringArgumentType.string())
                                        .suggests(PortalLockCommand::suggestWorldKeys)
                                        .executes(PortalLockCommand::executeRemove)
                                ).executes(PortalLockCommand::executeRemoveSyntax)
                        )
                        //#endregion
                        //#region List subcommand
                        .then(Commands.literal("list").then(Commands.argument("page", IntegerArgumentType.integer())
                                        .executes(source -> executeList(source, IntegerArgumentType.getInteger(source, "page")))
                                ).executes(source -> executeList(source, 1))
                        )
                        //#endregion
                        //#region Info subcommand
                        .then(Commands.literal("info").then(Commands.argument("worldKey", StringArgumentType.string())
                                        .suggests(PortalLockCommand::suggestWorldKeys)
                                        .executes(
                                                source -> executeInfo(source, 1)
                                        )
                                        .then(Commands.argument("page", IntegerArgumentType.integer())
                                                .executes(source -> executeInfo(source, IntegerArgumentType.getInteger(source, "page"))))
                                    .executes(source -> executeInfo(source, 1))
                                ).executes(PortalLockCommand::executeInfoSyntax)
                        )
                        //#endregion
                        .executes(PortalLockCommand::executeSyntax)); // If no argument was given then it's a syntax error

        // Generate Aliases
        for (String alias : Aliases) {
            dispatcher.register(Commands.literal(alias).redirect(commandNode));
        }
    }

    //#region Syntax errors
    /**
     * Sends a message to the player indicating the correct syntax for the command.
     *
     * @param command The command context containing the command source.
     * @return An integer indicating the success of the command execution.
     */
    private static int executeSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetLocaleComp("commands.syntax", Name, Syntax));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Sends a message to the player indicating the correct syntax for the add subcommand.
     *
     * @param command The command context containing the command source.
     * @return An integer indicating the success of the command execution.
     */
    private static int executeAddSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetLocaleComp("commands.syntax", Name,
                "add [worldKey]"));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Sends a message to the player indicating the correct syntax for the edit subcommand.
     *
     * @param command The command context containing the command source.
     * @return An integer indicating the success of the command execution.
     */
    private static int executeEditSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetLocaleComp("commands.syntax", Name,
                "edit [worldKey] [variable] [newValue]"));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Sends a message to the player indicating the correct syntax for the remove subcommand.
     *
     * @param command The command context containing the command source.
     * @return An integer indicating the success of the command execution.
     */
    private static int executeRemoveSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetLocaleComp("commands.syntax", Name,
                "remove [worldKey]"));
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Sends a message to the player indicating the correct syntax for the info subcommand.
     *
     * @param command The command context containing the command source.
     * @return An integer indicating the success of the command execution.
     */
    private static int executeInfoSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetLocaleComp("commands.syntax", Name,
                "info [worldKey] <page>"));
        return Command.SINGLE_SUCCESS;
    }
    //#endregion

    //#region Subcommands
    /**
     * Executes the "add" command.
     *
     * @param command The command context containing information about the command
     *                execution and the source of the command.
     * @return The result of the command execution, which is typically a success
     *         indicator (e.g., {@link Command#SINGLE_SUCCESS}).
     */
    private static int executeAdd(CommandContext<CommandSourceStack> command){
        try {
            Entity entity = command.getSource().getEntity();
            if (entity == null)
                return 0;

            CommonConfig config = CommonClass.CONFIG();
            String worldKey = StringArgumentType.getString(command, "worldKey");
            DimensionData dimensionData = null;
            for (var dimension : config.Dimensions) {
                if (dimension.Key.equalsIgnoreCase(worldKey)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData != null) {
                entity.sendSystemMessage(Translations.GetLocaleCompPrefix("commands.add.alreadyExist", worldKey));
                return 0;
            }

            String displayName;
            if (worldKey.contains(":"))
                displayName = worldKey.split(":")[1];
            else
                displayName = worldKey;
            config.Dimensions.add(new DimensionData(worldKey, displayName, false, "2024-01-01 00:00", "2024-01-01 00:00",
                            true, true, "minecraft:overworld",
                            true, false, worldKey.replaceAll(":", ".") + ".enter", true, false, worldKey.replaceAll(":", ".") + ".leave"
                    )
            );
            CommonClass.UpdateConfig(config);
            entity.sendSystemMessage(Translations.GetLocaleCompPrefix("commands.add.success", worldKey));
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeAdd:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeEdit(CommandContext<CommandSourceStack> command){
        Entity entity = command.getSource().getEntity();
        if (entity == null)
            return 0;
        String worldKey = StringArgumentType.getString(command, "worldKey");
        String fieldKey = StringArgumentType.getString(command, "variable");
        String newValue = StringArgumentType.getString(command, "newValue");

        try {
            CommonConfig config = CommonClass.CONFIG();
            DimensionData dimensionData = null;
            for (var dimension : config.Dimensions) {
                if (dimension.Key.equalsIgnoreCase(worldKey)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData == null) {
                entity.sendSystemMessage(Translations.GetLocaleCompPrefix("general.dimensionNotFound", worldKey));
                return 0;
            }

            // TODO
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeEdit:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeRemove(CommandContext<CommandSourceStack> command){
        Entity entity = command.getSource().getEntity();
        if (entity == null)
            return 0;
        String worldKey = StringArgumentType.getString(command, "worldKey");

        try {
            CommonConfig config = CommonClass.CONFIG();
            DimensionData dimensionData = null;
            for (var dimension : config.Dimensions) {
                if (dimension.Key.equalsIgnoreCase(worldKey)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData == null) {
                entity.sendSystemMessage(Translations.GetLocaleCompPrefix("general.dimensionNotFound", worldKey));
                return 0;
            }

            config.Dimensions.remove(dimensionData);
            CommonClass.UpdateConfig(config);
            entity.sendSystemMessage(Translations.GetLocaleCompPrefix("commands.remove.success", worldKey));
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeRemove:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeList(CommandContext<CommandSourceStack> command, int page){
        Entity entity = command.getSource().getEntity();
        if (entity == null)
            return 0;
        if (page < 1)
            page = 1;

        try {

            List<DimensionData> dimensions = CommonClass.CONFIG().Dimensions;
            var levels = command.getSource().getServer().levelKeys().stream().toList();

            entity.sendSystemMessage(Translations.GetLocaleComp("commands.list.title"));

            boolean reachedEnd = false;
            int maxPage = 1 + (levels.size() / 15);

            for (int i = 0; i < 15; i++) {

                int index = i + 15 * (page - 1);
                if (index >= levels.size()) {
                    reachedEnd = true;
                    break;
                }

                var level = levels.get(index);
                String currentLevelKey = level.location().toString();
                // Has dimension data
                if (dimensions.stream().anyMatch(x -> x.Key.equals(currentLevelKey))) {
                    String infoTranslation = Translations.GetLocale("commands.list.infoBtn");
                    if (infoTranslation == null)
                        infoTranslation = "ERROR_INFO_BTN";

                    String removeTranslation = Translations.GetLocale("commands.list.removeBtn");
                    if (removeTranslation == null)
                        removeTranslation = "ERROR_REMOVE_BTN";

                    List<InteractableChatComponent> args = new ArrayList<>();
                    args.add(new InteractableChatComponent(currentLevelKey));
                    args.add(new InteractableChatComponent(infoTranslation, false,
                            MessageFormat.format("/portallock info \"{0}\"", currentLevelKey),
                            Translations.GetLocale("commands.events.view"))
                    );
                    args.add(new InteractableChatComponent(removeTranslation, false,
                            MessageFormat.format("/portallock remove \"{0}\"", currentLevelKey),
                            Translations.GetLocale("commands.events.remove"))
                    );
                    Component comp = ModUtils.createClickableComponent(Translations.GetLocale("commands.list.line"), args);
                    entity.sendSystemMessage(comp);
                }
                // Dimension data does not exist
                else {

                    String addTranslation = Translations.GetLocale("commands.list.addBtn");
                    if (addTranslation == null)
                        addTranslation = "ERROR_ADD_BTN";

                    List<InteractableChatComponent> args = new ArrayList<>();
                    args.add(new InteractableChatComponent(currentLevelKey));
                    args.add(new InteractableChatComponent(addTranslation, false,
                            MessageFormat.format("/portallock add \"{0}\"", currentLevelKey),
                            Translations.GetLocale("commands.events.add"))
                    );
                    args.add(new InteractableChatComponent(""));
                    Component comp = ModUtils.createClickableComponent(Translations.GetLocale("commands.list.line"), args);
                    entity.sendSystemMessage(comp);
                }

            }

            List<InteractableChatComponent> args = new ArrayList<>();
            if (page > 1) {
                String prevBtnTranslation = Translations.GetLocale("commands.list.pagePrev");
                if (prevBtnTranslation == null)
                    prevBtnTranslation = "ERROR_PREV_BTN";
                args.add(new InteractableChatComponent(prevBtnTranslation, false,
                        MessageFormat.format("/portallock list {0}", page - 1),
                        Translations.GetLocale("commands.events.navigate"))
                );
            }
            else
                args.add(new InteractableChatComponent(""));
            args.add(new InteractableChatComponent(""+page));
            args.add(new InteractableChatComponent(""+maxPage));
            if (page != maxPage && !reachedEnd) {
                String nextBtnTranslation = Translations.GetLocale("commands.list.pageNext");
                if (nextBtnTranslation == null)
                    nextBtnTranslation = "ERROR_NEXT_BTN";
                args.add(new InteractableChatComponent(nextBtnTranslation,false,
                        MessageFormat.format("/portallock list {0}", page + 1),
                        Translations.GetLocale("commands.events.navigate"))
                );
            }
            else
                args.add(new InteractableChatComponent(""));
            Component comp = ModUtils.createClickableComponent(Translations.GetLocale("commands.list.bottom"), args);
            entity.sendSystemMessage(comp);
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeList:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeInfo(CommandContext<CommandSourceStack> command, int page){
        Entity entity = command.getSource().getEntity();
        if (entity == null)
            return 0;
        if (page < 1)
            page = 1;
        String worldKey = StringArgumentType.getString(command, "worldKey");

        try {
            DimensionData dimensionData = null;
            for (var dimension : CommonClass.CONFIG().Dimensions) {
                if (dimension.Key.equals(worldKey)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData == null) {
                entity.sendSystemMessage(Translations.GetLocaleCompPrefix("general.dimensionNotFound", worldKey));
                return 0;
            }

            entity.sendSystemMessage(Translations.GetLocaleComp("commands.info.title", worldKey));

            List<Field> fields = Arrays.stream(dimensionData.getClass().getFields()).filter(x -> x.isAnnotationPresent(ValueEditor.class)).toList();
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
                List<InteractableChatComponent> args = new ArrayList<>();
                args.add(new InteractableChatComponent(field.getName()));

                switch (annotation.type()) {
                    case TEXT, NUMBER, WORLD_KEY -> {
                        String btnLocale;
                        String btnValue;
                        var fieldValue = field.get(dimensionData);

                        if (fieldValue == null) {
                            btnLocale = "commands.info.inactive";
                            btnValue = Translations.GetLocale(btnLocale,
                                    Translations.GetLocale(annotation.type() == EFieldType.WORLD_KEY ?
                                            "commands.info.actionWorld" : "commands.info.actionCustom"
                                    )
                            );
                        }
                        else {
                            btnLocale = "commands.info.active";
                            btnValue = Translations.GetLocale(btnLocale, fieldValue.toString().length() > 10 ? fieldValue.toString().substring(0, 10) + "..." : fieldValue.toString()
                            );
                        }

                        if (btnValue == null)
                            btnValue = "ERROR_VALUE_BTN";

                        args.add(new InteractableChatComponent(btnValue, true,
                                MessageFormat.format("/portallock edit \"{0}\" {1} ", worldKey, field.getName()),
                                Translations.GetLocale("commands.events.set"))
                        );
                        args.add(new InteractableChatComponent(""));
                    }
                    case BOOLEAN -> {
                        String trueBtnLocale;
                        String falseBtnLocale;
                        var fieldValue = field.get(dimensionData);
                        if (fieldValue == null) {
                            trueBtnLocale = "commands.info.inactive";
                            falseBtnLocale = "commands.info.inactive";
                        }
                        else {
                            if ((Boolean)fieldValue) {
                                trueBtnLocale = "commands.info.active";
                                falseBtnLocale = "commands.info.inactive";
                            }
                            else {
                                trueBtnLocale = "commands.info.inactive";
                                falseBtnLocale = "commands.info.active";
                            }
                        }

                        String trueBtnValue = Translations.GetLocale(trueBtnLocale, Translations.GetLocale("commands.info.actionAllow"));
                        if (trueBtnValue == null)
                            trueBtnValue = "ERROR_VALUE_BTN";

                        String falseBtnValue = Translations.GetLocale(falseBtnLocale, Translations.GetLocale("commands.info.actionDeny"));
                        if (falseBtnValue == null)
                            falseBtnValue = "ERROR_VALUE_BTN";

                        args.add(new InteractableChatComponent(trueBtnValue, false,
                                MessageFormat.format("/portallock edit \"{0}\" {1} true", worldKey, field.getName()),
                                Translations.GetLocale("commands.events.set"))
                        );
                        args.add(new InteractableChatComponent(falseBtnValue, false,
                                MessageFormat.format("/portallock edit \"{0}\" {1} false", worldKey, field.getName()),
                                Translations.GetLocale("commands.events.set"))
                        );
                    }
                }

                Component comp = ModUtils.createClickableComponent(Translations.GetLocale("commands.info.line"), args);
                entity.sendSystemMessage(comp);
            }

            List<InteractableChatComponent> args = new ArrayList<>();
            if (page > 1) {
                String prevBtnTranslation = Translations.GetLocale("commands.info.pagePrev");
                if (prevBtnTranslation == null)
                    prevBtnTranslation = "ERROR_PREV_BTN";
                args.add(new InteractableChatComponent(prevBtnTranslation, false,
                        MessageFormat.format("/portallock info \"{0}\" {1}",  worldKey, page - 1),
                        Translations.GetLocale("commands.events.navigate"))
                );
            }
            else
                args.add(new InteractableChatComponent(""));
            args.add(new InteractableChatComponent(""+page));
            args.add(new InteractableChatComponent(""+maxPage));
            if (page != maxPage && !reachedEnd) {
                String nextBtnTranslation = Translations.GetLocale("commands.info.pageNext");
                if (nextBtnTranslation == null)
                    nextBtnTranslation = "ERROR_NEXT_BTN";
                args.add(new InteractableChatComponent(nextBtnTranslation,false,
                        MessageFormat.format("/portallock info \"{0}\" {1}", worldKey, page + 1),
                        Translations.GetLocale("commands.events.navigate"))
                );
            }
            else
                args.add(new InteractableChatComponent(""));
            Component comp = ModUtils.createClickableComponent(Translations.GetLocale("commands.list.bottom"), args);
            entity.sendSystemMessage(comp);
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeInfo:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }
    //#endregion

    //#region Suggests
    /**
     * Suggests world keys based on the current command context and user input.
     * <p>
     * This method generates suggestions for valid world keys that can be used in the command
     * being executed. It utilizes the provided {@link SuggestionsBuilder} to append suggestions,
     * enhancing the user experience by offering autocomplete options.
     * </p>
     * @param context The command context containing information about the command source
     *                and its current state.
     * @param builder The {@link SuggestionsBuilder} used to accumulate and provide suggestions.
     * @return A {@link CompletableFuture} that will complete with the generated suggestions
     *         for world keys.
     */
    public static CompletableFuture<Suggestions> suggestWorldKeys(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        // Example list of world keys to suggest
        MinecraftServer server = context.getSource().getServer();

        for (var world : server.getAllLevels()) {
            String name = WorldUtils.GetName(world);
            if (name.startsWith(builder.getRemaining())) {
                builder.suggest(String.format("\"%s\"", name));
            }
        }

        return builder.buildFuture();
    }

    /**
     * Suggests dimension fields based on the current context and user input.
     * <p>
     * This method analyzes the command context and generates suggestions for dimension fields
     * that can be used in the command being executed. The suggestions are appended to the
     * provided {@link SuggestionsBuilder}, allowing for enhanced user experience in command input.
     * </p>
     * @param context The command context that provides information about the command source
     *                and its current state.
     * @param builder The {@link SuggestionsBuilder} used to accumulate suggestions.
     * @return A {@link CompletableFuture} that will complete with the generated suggestions
     *         for dimension fields.
     */
    public static CompletableFuture<Suggestions> suggestDimensionFields(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {

        Field[] fields = DimensionData.class.getFields();

        for (Field field : fields) {
            if (!field.isAnnotationPresent(ValueEditor.class))
                continue;

            if (field.getName().startsWith(builder.getRemaining()))
                builder.suggest(field.getName());
        }

        return builder.buildFuture();
    }
    //#endregion
}
