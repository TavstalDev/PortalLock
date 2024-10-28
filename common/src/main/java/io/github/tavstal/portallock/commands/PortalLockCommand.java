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
import io.github.tavstal.portallock.Translations;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.utils.ModUtils;
import io.github.tavstal.portallock.utils.WorldUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

public class PortalLockCommand {

    public static final String Name = "portallock";
    public static final String Syntax = "add | edit | remove | list | info";
    public static final String[] Aliases = new String[] { "pl", "portall", "plock" };
    public static final Integer PermissionLevel = 0;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        // Create the command itself
        LiteralCommandNode<CommandSourceStack> commandNode = dispatcher.register(
                Commands.literal(Name) // Create the command
                        .requires(source -> source.hasPermission(PermissionLevel)) // Check permission
                        //#region Add subcommand
                        .then(Commands.literal("add").then(Commands.argument("worldKey", StringArgumentType.word())
                                .suggests(PortalLockCommand::suggestWorldKeys)
                                .executes(PortalLockCommand::executeAdd) // Executes if the arg is present
                            ).executes(PortalLockCommand::executeAddSyntax) // Executes if the arg is not present
                        )
                        //#endregion
                        //#region Edit subcommand
                        .then(Commands.literal("edit").then(Commands.argument("worldKey", StringArgumentType.word())
                                .suggests(PortalLockCommand::suggestWorldKeys)
                                .then(Commands.argument("variable", StringArgumentType.word())
                                        .then(Commands.argument("newValue", StringArgumentType.word())
                                                .executes(PortalLockCommand::executeEdit)
                                        ).executes(PortalLockCommand::executeEditSyntax)
                                ).executes(PortalLockCommand::executeEditSyntax)
                            ).executes(PortalLockCommand::executeEditSyntax)
                        )
                        //#endregion
                        //#region Remove subcommand
                        .then(Commands.literal("remove").then(Commands.argument("worldKey", StringArgumentType.word())
                                        .suggests(PortalLockCommand::suggestWorldKeys)
                                        .executes(PortalLockCommand::executeRemove)
                                ).executes(PortalLockCommand::executeRemoveSyntax)
                        )
                        //#endregion
                        //#region List subcommand
                        .then(Commands.literal("list").then(Commands.argument("page", IntegerArgumentType.integer())
                                        .executes(PortalLockCommand::executeList)
                                ).executes(PortalLockCommand::executeListSyntax)
                        )
                        //#endregion
                        //#region Info subcommand
                        .then(Commands.literal("info").then(Commands.argument("worldKey", StringArgumentType.word())
                                        .suggests(PortalLockCommand::suggestWorldKeys)
                                        .executes(PortalLockCommand::executeInfo)
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
    private static int executeSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name, Syntax));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAddSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name,
                "add [worldKey]"));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeEditSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name,
                "edit [worldKey] [variable] [newValue]"));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemoveSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name,
                "remove [worldKey]"));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeListSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name,
                "list <page>"));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeInfoSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(Translations.GetTranslationComp("command_syntax", Name,
                "info [worldKey]"));
        return Command.SINGLE_SUCCESS;
    }
    //#endregion

    //#region Subcommands
    private static int executeAdd(CommandContext<CommandSourceStack> command){
        try {
            Entity entity = command.getSource().getEntity();
            if (entity == null)
                return 0;

            String worldKey = StringArgumentType.getString(command, "worldKey");
            DimensionData dimensionData = null;
            for (var dimension : CommonClass.CONFIG().Dimensions) {
                if (dimension.Key.equalsIgnoreCase(worldKey)) {
                    dimensionData = dimension;
                    break;
                }
            }

            if (dimensionData != null) {
                entity.sendSystemMessage(Translations.GetTranslationComp("error_dimension_already_exist", worldKey));
                return 0;
            }


            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeAdd:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeEdit(CommandContext<CommandSourceStack> command){
        String worldKey = StringArgumentType.getString(command, "worldKey");

        try {


            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeEdit:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeRemove(CommandContext<CommandSourceStack> command){
        String worldKey = StringArgumentType.getString(command, "worldKey");

        try {
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeRemove:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeList(CommandContext<CommandSourceStack> command){
        int page = IntegerArgumentType.getInteger(command, "page");
        try {
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex) {
            CommonClass.LOG.error("Error in PortalLock command, executeList:");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }

    private static int executeInfo(CommandContext<CommandSourceStack> command){
        String worldKey = StringArgumentType.getString(command, "worldKey");

        try {
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
    public static CompletableFuture<Suggestions> suggestWorldKeys(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        // Example list of world keys to suggest
        MinecraftServer server = context.getSource().getServer();

        for (var world : server.getAllLevels()) {
            String name = WorldUtils.GetName(world);
            if (name.startsWith(builder.getRemaining())) {
                builder.suggest(name);
            }
        }

        return builder.buildFuture();
    }
    //#endregion
}
