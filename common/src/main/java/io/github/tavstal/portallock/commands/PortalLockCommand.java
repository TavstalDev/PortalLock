package io.github.tavstal.portallock.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.tavstal.portallock.CommonClass;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class PortalLockCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal("portallock").executes(PortalLockCommand::execute));
    }
    private static int execute(CommandContext<CommandSourceStack> command){
        try {
            if (command.getSource().getEntity() instanceof ServerPlayer player) {
                // TODO
            }
            return Command.SINGLE_SUCCESS;
        }
        catch (Exception ex)
        {
            CommonClass.LOG.error("Error during executing command 'portallock':");
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return 0;
        }
    }
}
