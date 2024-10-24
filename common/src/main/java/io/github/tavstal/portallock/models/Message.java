package io.github.tavstal.portallock.models;

import io.github.tavstal.portallock.utils.ModUtils;
import io.github.tavstal.portallock.utils.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;
import java.time.LocalDateTime;

public class Message {
    public  MessageTitleComponent Title;
    public MessageChatComponent Chat;
    public MessageChatComponent ActionBar;

    public Message(MessageTitleComponent title, MessageChatComponent chat, MessageChatComponent actionBar) {
        Title = title;
        Chat = chat;
        ActionBar = actionBar;
    }

    public Message(boolean enableTitle, String title, String subtitle, boolean enableChat, String chatMessage, boolean enableActionbar, String actionbarMessage) {
        Title = new MessageTitleComponent(enableTitle, title, subtitle);
        Chat = new MessageChatComponent(enableChat, chatMessage);
        ActionBar = new MessageChatComponent(enableActionbar, actionbarMessage);
    }

    public void SendToPlayer(ServerPlayer player, DimensionData data, boolean isEntering) {
        if (data.AutoAllowByDate)
        {
            if (isEntering)
                SendToPlayer(player, data.DisplayName, data.DateToAllowEnter);
            else
                SendToPlayer(player, data.DisplayName, data.DateToAllowLeave);
        }
        else
            SendToPlayer(player, data.DisplayName);
    }

    private void SendToPlayer(ServerPlayer player, String displayName, String date) {
        LocalDateTime localDate = LocalDateTime.parse(date);
        String remainingTime = String.format("%s", Duration.between(LocalDateTime.now(), localDate).getSeconds());

        if (Chat.Enable) {
            player.sendSystemMessage(ModUtils.Literal(Chat.Message.replaceAll("%display_name%", displayName).replaceAll("%time_left_to_unlock%", remainingTime)));
        }
        if (Title.Enable) {
            PlayerUtils.SendTitleMessage(player, Title.Title.replaceAll("%display_name%", displayName).replaceAll("%time_left_to_unlock%", remainingTime), Title.Subtitle.replaceAll("%display_name%", displayName).replaceAll("%time_left_to_unlock%", remainingTime), 5);
        }
        if (ActionBar.Enable) {
            player.displayClientMessage(ModUtils.Literal(ActionBar.Message.replaceAll("%display_name%", displayName).replaceAll("%time_left_to_unlock%", remainingTime)), true);
        }
    }

    private void SendToPlayer(ServerPlayer player, String displayName) {
        if (Chat.Enable) {
            player.sendSystemMessage(ModUtils.Literal(Chat.Message.replaceAll("%display_name%", displayName)));
        }
        if (Title.Enable) {
            PlayerUtils.SendTitleMessage(player, Title.Title.replaceAll("%display_name%", displayName), Title.Subtitle.replaceAll("%display_name%", displayName), 5);
        }
        if (ActionBar.Enable) {
            player.displayClientMessage(ModUtils.Literal(ActionBar.Message.replaceAll("%display_name%", displayName)), true);
        }
    }
}
