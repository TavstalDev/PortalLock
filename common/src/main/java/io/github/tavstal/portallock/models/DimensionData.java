package io.github.tavstal.portallock.models;

import java.time.LocalDateTime;

public class DimensionData {

    @ConfigField(order = 1, comment = "Key of the dimension, example: minecraft:overworld")
    public String Key;

    @ConfigField(order = 2, comment = "Name of the dimension, example: overworld")
    public String DisplayName;

    @ConfigField(order = 3, comment = "Automatically allow players based on the specified dates for entering and leaving.")
    public boolean AutoAllowByDate;

    @ConfigField(order = 4, comment = "The date when players will be allowed to enter. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 30 minutes.")
    public String DateToAllowEnter;

    @ConfigField(order = 5, comment = "The date when players will be allowed to leave. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 30 minutes.")
    public String DateToAllowLeave;

    @ConfigField(order = 6, comment = "Whether unauthorized players should be kicked from the dimension.")
    public boolean KickUnauthorizedPlayers;

    @ConfigField(order = 7, comment = "Allow players to be kicked to their bed spawn if unauthorized.")
    public boolean AllowKickToBed;

    @ConfigField(order = 8, comment = "The dimension to send unauthorized players to if kicked.")
    public String KickTargetDimension;

    @ConfigField(order = 9, comment = "Whether players are allowed to enter the dimension.")
    public boolean AllowEnter;

    @ConfigField(order = 10, comment = "Require players to have a specific permission to enter the dimension.")
    public boolean RequireEnterPermission;

    @ConfigField(order = 11, comment = "The permission needed for players to enter.")
    public String EnterPermission;

    @ConfigField(order = 12, comment = "Whether players are allowed to leave the dimension.")
    public boolean AllowLeave;

    @ConfigField(order = 13, comment = "Require players to have a specific permission to leave.")
    public boolean RequireLeavePermission;

    @ConfigField(order = 14, comment = "The permission needed for players to leave.")
    public String LeavePermission;

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

    public LocalDateTime GetEnterDate() {
        return LocalDateTime.parse(DateToAllowEnter);
    }

    public LocalDateTime GetLeaveDate() {
        return LocalDateTime.parse(DateToAllowLeave);
    }
}
