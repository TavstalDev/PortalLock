package io.github.tavstal.portallock.models;

import io.github.tavstal.minecorelib.utils.ChatUtils;
import io.github.tavstal.portallock.PortalLock;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class DimensionData {

    @ConfigField(order = 1, comment = "Key of the dimension, example: minecraft:overworld")
    public String Key;

    @ConfigField(order = 2, comment = "Name of the dimension, example: overworld")
    @ValueEditor(type = EFieldType.TEXT)
    public String DisplayName;

    @ConfigField(order = 3, comment = "Automatically allow players based on the specified dates for entering and leaving.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AutoAllowByDate;

    @ConfigField(order = 4, comment = "The date when players will be allowed to enter. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 15 minutes.")
    @ValueEditor(type = EFieldType.DATETIME)
    public String DateToAllowEnter;

    @ConfigField(order = 5, comment = "The date when players will be allowed to leave. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 15 minutes.")
    @ValueEditor(type = EFieldType.DATETIME)
    public String DateToAllowLeave;

    @ConfigField(order =  6, comment = "Whether unauthorized players should be kicked from the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean KickUnauthorizedPlayers;

    @ConfigField(order =  7, comment = "Allow players to be kicked to their bed spawn if unauthorized.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowKickToBed;

    @ConfigField(order = 8, comment = "The dimension to send unauthorized players to if kicked.")
    @ValueEditor(type = EFieldType.WORLD_KEY)
    public String KickTargetDimension;

    @ConfigField(order = 9, comment = "Whether players are allowed to enter the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowEnter;

    @ConfigField(order = 10, comment = "Require players to have a specific permission to enter the dimension.\nNote: At the moment permissions are only supported by the plugin version.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean RequireEnterPermission;

    @ConfigField(order = 11, comment = "The permission needed for players to enter.")
    @ValueEditor(type = EFieldType.TEXT)
    public String EnterPermission;

    @ConfigField(order = 12, comment = "Whether players are allowed to leave the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowLeave;

    @ConfigField(order = 13, comment = "Require players to have a specific permission to leave.\nNote: At the moment permissions are only supported by the plugin version.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean RequireLeavePermission;

    @ConfigField(order = 14, comment = "The permission needed for players to leave.")
    @ValueEditor(type = EFieldType.TEXT)
    public String LeavePermission;

    public DimensionData() {}

    public DimensionData(String key, String displayName, boolean autoAllowByDate, String dateToAllowEnter, String dateToAllowLeave, boolean kickUnauthorizedPlayers, boolean allowKickToBed, String kickTargetDimension, boolean allowEnter, boolean requireEnterPermission, String enterPermission, boolean allowLeave, boolean requireLeavePermission, String leavePermission) {
        Key = key;
        DisplayName = displayName;
        AutoAllowByDate = autoAllowByDate;
        DateToAllowEnter = dateToAllowEnter;
        DateToAllowLeave = dateToAllowLeave;
        KickUnauthorizedPlayers = kickUnauthorizedPlayers;
        AllowKickToBed = allowKickToBed;
        KickTargetDimension = kickTargetDimension;
        AllowEnter = allowEnter;
        RequireEnterPermission = requireEnterPermission;
        EnterPermission = enterPermission;
        AllowLeave = allowLeave;
        RequireLeavePermission = requireLeavePermission;
        LeavePermission = leavePermission;
    }

    /**
     * Parses and returns the date and time when entry is allowed.
     *
     * @return A {@link LocalDateTime} representing the date and time of allowed entry.
     */
    public LocalDateTime GetEnterDate() {
        return LocalDateTime.parse(DateToAllowEnter, PortalLock.DateFormatter);
    }

    /**
     * Parses and returns the date and time when exit is allowed.
     *
     * @return A {@link LocalDateTime} representing the date and time of allowed exit.
     */
    public LocalDateTime GetLeaveDate() {
        return LocalDateTime.parse(DateToAllowLeave, PortalLock.DateFormatter);
    }

    /**
     * Sends a message to a player or all online players based on the announcement type.
     *
     * @param player The player to send the message to. If null, the message is sent to all online players.
     * @param type The type of announcement to determine the message content.
     */
    public void SendMessage(Player player, EAnnouncement type) {
        String chatMsg, titleMsg, subTitleMsg, actionBarMsg;
        boolean enableChatMsg, enableTitleMsg, enableActionBarMsg;
        var translator = PortalLock.Translator();

        switch (type) {
            case EAnnouncement.Enter_Success:
                chatMsg = translator.Localize(player, "DimensionMsg.Enter.Chat");
                titleMsg = translator.Localize(player,"DimensionMsg.Enter.Title");
                subTitleMsg = translator.Localize(player,"DimensionMsg.Enter.SubTitle");
                actionBarMsg = translator.Localize(player,"DimensionMsg.Enter.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("enterSuccess.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("enterSuccess.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("enterSuccess.enableActionBarMsg");
                break;
            case EAnnouncement.Enter_Fail:
                chatMsg = translator.Localize(player,"DimensionMsg.EnterFail.Chat");
                titleMsg = translator.Localize(player,"DimensionMsg.EnterFail.Title");
                subTitleMsg = translator.Localize(player,"DimensionMsg.EnterFail.SubTitle");
                actionBarMsg = translator.Localize(player,"DimensionMsg.EnterFail.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("enterFail.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("enterFail.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("enterFail.enableActionBarMsg");
                break;
            case EAnnouncement.Enter_Fail_Permission:
                chatMsg = translator.Localize(player, "DimensionMsg.EnterFailPermission.Chat");
                titleMsg = translator.Localize(player, "DimensionMsg.EnterFailPermission.Title");
                subTitleMsg = translator.Localize(player, "DimensionMsg.EnterFailPermission.SubTitle");
                actionBarMsg = translator.Localize(player, "DimensionMsg.EnterFailPermission.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("enterFailPermission.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("enterFailPermission.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("enterFailPermission.enableActionBarMsg");
                break;
            case EAnnouncement.Enter_Fail_Auto_Unlock: {
                String time = translator.Localize(player, "Time");
                Duration duration = Duration.between(LocalDateTime.now(), GetEnterDate());
                long seconds = duration.getSeconds();
                long days = seconds / 86400;
                long hours =  (seconds % 86400) / 3600;
                long minutes = (seconds % 3600) / 60;
                long remainingSeconds = seconds % 60;
                time = time.replace("%days%", String.format("%02d", days))
                        .replace("%hours%", String.format("%02d", hours))
                        .replace("%minutes%", String.format("%02d", minutes))
                        .replace("%seconds%", String.format("%02d", remainingSeconds));
                chatMsg = translator.Localize(player, "DimensionMsg.EnterFailAutoUnlock.Chat").replace("%time%", time);
                titleMsg = translator.Localize(player, "DimensionMsg.EnterFailAutoUnlock.Title").replace("%time%", time);
                subTitleMsg = translator.Localize(player, "DimensionMsg.EnterFailAutoUnlock.SubTitle").replace("%time%", time);
                actionBarMsg = translator.Localize(player, "DimensionMsg.EnterFailAutoUnlock.ActionBar").replace("%time%", time);
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("enterFailAutoUnlock.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("enterFailAutoUnlock.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("enterFailAutoUnlock.enableActionBarMsg");
                break;
            }
            case EAnnouncement.Enter_Unlocked:
            case EAnnouncement.Leave_Unlocked:
                String action = translator.Localize(player, type == EAnnouncement.Enter_Unlocked ? "DimensionMsg.Unlock.EnterAction" : "DimensionMsg.Unlock.ExitAction");
                chatMsg = translator.Localize(player, "DimensionMsg.Unlock.Chat").replace("%action%", action);
                titleMsg = translator.Localize(player, "DimensionMsg.Unlock.Title").replace("%action%", action);
                subTitleMsg = translator.Localize(player, "DimensionMsg.Unlock.SubTitle").replace("%action%", action);
                actionBarMsg = translator.Localize(player, "DimensionMsg.Unlock.ActionBar").replace("%action%", action);
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("unlock.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("unlock.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("unlock.enableActionBarMsg");
                break;
            case EAnnouncement.Leave_Success:
                chatMsg = translator.Localize(player, "DimensionMsg.Exit.Chat");
                titleMsg = translator.Localize(player, "DimensionMsg.Exit.Title");
                subTitleMsg = translator.Localize(player, "DimensionMsg.Exit.SubTitle");
                actionBarMsg = translator.Localize(player, "DimensionMsg.Exit.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("exitSuccess.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("exitSuccess.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("exitSuccess.enableActionBarMsg");
                break;
            case EAnnouncement.Leave_Fail:
                chatMsg = translator.Localize(player, "DimensionMsg.ExitFail.Chat");
                titleMsg = translator.Localize(player, "DimensionMsg.ExitFail.Title");
                subTitleMsg = translator.Localize(player, "DimensionMsg.ExitFail.SubTitle");
                actionBarMsg = translator.Localize(player, "DimensionMsg.ExitFail.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("exitFail.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("exitFail.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("exitFail.enableActionBarMsg");
                break;
            case EAnnouncement.Leave_Fail_Permission:
                chatMsg = translator.Localize(player, "DimensionMsg.ExitFailPermission.Chat");
                titleMsg = translator.Localize(player, "DimensionMsg.ExitFailPermission.Title");
                subTitleMsg = translator.Localize(player, "DimensionMsg.ExitFailPermission.SubTitle");
                actionBarMsg = translator.Localize(player, "DimensionMsg.ExitFailPermission.ActionBar");
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("exitFailPermission.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("exitFailPermission.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("exitFailPermission.enableActionBarMsg");
                break;
            case EAnnouncement.Leave_Fail_Auto_Unlock: {
                String time = translator.Localize(player, "Time");
                Duration duration = Duration.between(LocalDateTime.now(), GetLeaveDate());
                long seconds = duration.getSeconds();
                long days = seconds / 86400;
                long hours =  (seconds % 86400) / 3600;
                long minutes = (seconds % 3600) / 60;
                long remainingSeconds = seconds % 60;
                time = time.replace("%days%", String.format("%02d", days))
                        .replace("%hours%", String.format("%02d", hours))
                        .replace("%minutes%", String.format("%02d", minutes))
                        .replace("%seconds%", String.format("%02d", remainingSeconds));
                chatMsg = translator.Localize(player, "DimensionMsg.ExitFailAutoUnlock.Chat").replace("%time%", time);
                titleMsg = translator.Localize(player, "DimensionMsg.ExitFailAutoUnlock.Title").replace("%time%", time);
                subTitleMsg = translator.Localize(player, "DimensionMsg.ExitFailAutoUnlock.SubTitle").replace("%time%", time);
                actionBarMsg = translator.Localize(player, "DimensionMsg.ExitFailAutoUnlock.ActionBar").replace("%time%", time);
                enableChatMsg = PortalLock.Instance.getConfig().getBoolean("exitFailAutoUnlock.enableChatMsg");
                enableTitleMsg = PortalLock.Instance.getConfig().getBoolean("exitFailAutoUnlock.enableTitleMsg");
                enableActionBarMsg = PortalLock.Instance.getConfig().getBoolean("exitFailAutoUnlock.enableActionBarMsg");
                break;
            }
            default:
                return;
        }

        if (player != null) {
            if (enableChatMsg)
                PortalLock.Instance.sendRichMsg(player, chatMsg, new HashMap<>() {{
                    put("dimension", DisplayName);
                }});
            if (enableTitleMsg)
                player.showTitle(Title.title(ChatUtils.translateColors(titleMsg.replace("%dimension%", DisplayName), true), ChatUtils.translateColors(subTitleMsg.replace("%dimension%", DisplayName), true)));
            if (enableActionBarMsg)
                player.sendActionBar(ChatUtils.translateColors(actionBarMsg.replace("%dimension%", DisplayName), true));
            return;
        }
        
        for (var onlinePlayer : PortalLock.Instance.getServer().getOnlinePlayers()) {
            if (enableChatMsg)
                PortalLock.Instance.sendRichMsg(onlinePlayer, chatMsg, new HashMap<>() {{
                    put("dimension", DisplayName);
                }});
            if (enableTitleMsg)
                onlinePlayer.showTitle(Title.title(ChatUtils.translateColors(titleMsg.replace("%dimension%", DisplayName), true), ChatUtils.translateColors(subTitleMsg.replace("%dimension%", DisplayName), true)));
            if (enableActionBarMsg)
                onlinePlayer.sendActionBar(ChatUtils.translateColors(actionBarMsg.replace("%dimension%", DisplayName), true));
        }
    }
}
