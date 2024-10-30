package io.github.tavstal.portallock.models;

import io.github.tavstal.portallock.CommonClass;

import java.time.LocalDateTime;

public class DimensionData {

    @ConfigField(comment = "Key of the dimension, example: minecraft:overworld")
    public String Key;

    @ConfigField(comment = "Name of the dimension, example: overworld")
    @ValueEditor(type = EFieldType.TEXT)
    public String DisplayName;

    @ConfigField(comment = "Automatically allow players based on the specified dates for entering and leaving.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AutoAllowByDate;

    @ConfigField(comment = "The date when players will be allowed to enter. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 15 minutes.")
    @ValueEditor(type = EFieldType.DATETIME)
    public String DateToAllowEnter;

    @ConfigField(comment = "The date when players will be allowed to leave. Format: YYYY-MM-DD HH:mm.\nNote: It is checked every 15 minutes.")
    @ValueEditor(type = EFieldType.DATETIME)
    public String DateToAllowLeave;

    @ConfigField(comment = "Whether unauthorized players should be kicked from the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean KickUnauthorizedPlayers;

    @ConfigField(comment = "Allow players to be kicked to their bed spawn if unauthorized.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowKickToBed;

    @ConfigField(comment = "The dimension to send unauthorized players to if kicked.")
    @ValueEditor(type = EFieldType.WORLD_KEY)
    public String KickTargetDimension;

    @ConfigField(comment = "Whether players are allowed to enter the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowEnter;

    @ConfigField(comment = "Require players to have a specific permission to enter the dimension.\nNote: At the moment permissions are only supported by the plugin version.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean RequireEnterPermission;

    @ConfigField(comment = "The permission needed for players to enter.")
    @ValueEditor(type = EFieldType.TEXT)
    public String EnterPermission;

    @ConfigField(comment = "Whether players are allowed to leave the dimension.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean AllowLeave;

    @ConfigField(comment = "Require players to have a specific permission to leave.\nNote: At the moment permissions are only supported by the plugin version.")
    @ValueEditor(type = EFieldType.BOOLEAN)
    public boolean RequireLeavePermission;

    @ConfigField(comment = "The permission needed for players to leave.")
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
        return LocalDateTime.parse(DateToAllowEnter, CommonClass.DateFormatter);
    }

    /**
     * Parses and returns the date and time when exit is allowed.
     *
     * @return A {@link LocalDateTime} representing the date and time of allowed exit.
     */
    public LocalDateTime GetLeaveDate() {
        return LocalDateTime.parse(DateToAllowLeave, CommonClass.DateFormatter);
    }
}
