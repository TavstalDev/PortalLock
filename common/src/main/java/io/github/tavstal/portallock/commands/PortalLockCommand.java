package io.github.tavstal.portallock.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.tavstal.portallock.CommonClass;
import io.github.tavstal.portallock.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.text.MessageFormat;

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
                                .executes(PortalLockCommand::executeAdd) // Executes if the arg is present
                            ).executes(PortalLockCommand::executeAddSyntax) // Executes if the arg is not present
                        )
                        //#endregion
                        //#region Edit subcommand
                        .then(Commands.literal("edit").then(Commands.argument("worldKey", StringArgumentType.word())
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

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name, Syntax)));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAddSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name,
                "add [worldKey]")));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeEditSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name,
                "edit [worldKey] [variable] [newValue]")));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemoveSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name,
                "remove [worldKey]")));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeListSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name,
                "list <page>")));
        return Command.SINGLE_SUCCESS;
    }

    private static int executeInfoSyntax(CommandContext<CommandSourceStack> command){
        var entity = command.getSource().getEntity();
        if (entity == null)
            return 0;

        entity.sendSystemMessage(ModUtils.Literal(MessageFormat.format(CommonClass.CONFIG().CommandSyntaxFormat, Name,
                "info [worldKey]")));
        return Command.SINGLE_SUCCESS;
    }
    //#endregion

    //#region Subcommands
    private static int executeAdd(CommandContext<CommandSourceStack> command){
        //StringArgumentType.getString(command, "password")

        return Command.SINGLE_SUCCESS;
    }

    private static int executeEdit(CommandContext<CommandSourceStack> command){
        //StringArgumentType.getString(command, "password")

        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemove(CommandContext<CommandSourceStack> command){
        //StringArgumentType.getString(command, "password")

        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(CommandContext<CommandSourceStack> command){
        //StringArgumentType.getString(command, "password")

        return Command.SINGLE_SUCCESS;
    }

    private static int executeInfo(CommandContext<CommandSourceStack> command){
        //StringArgumentType.getString(command, "password")

        return Command.SINGLE_SUCCESS;
    }
    //#endregion
}
