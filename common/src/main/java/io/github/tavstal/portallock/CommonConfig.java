package io.github.tavstal.portallock;

import io.github.tavstal.portallock.models.ConfigField;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.Message;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {

    @ConfigField(order = 1, comment = "Shows more logs than usual. Helps locating errors.")
    public boolean EnableDebugMode;

    @ConfigField(order = 2, comment = "List of the dimensions.\nAuto populated during first start.")
    public List<DimensionData> Dimensions;

    @ConfigField(order = 3, comment = "The message shown to players on successful entry.")
    public Message EnterSuccess;

    @ConfigField(order = 4, comment = "The message shown to players when entry fails.")
    public Message EnterFail;

    @ConfigField(order = 5, comment = "Message displayed when the player fails to enter due to missing permission.")
    public Message EnterFailPermission;

    @ConfigField(order = 6, comment = "Message displayed when the player fails to enter, but it will be auto unlocked.")
    public Message EnterFailAutoAllow;

    @ConfigField(order = 7, comment = "The message shown to players on successful exit.")
    public Message LeaveSuccess;

    @ConfigField(order = 8, comment = "The message shown to players when exit fails.")
    public Message LeaveFail;

    @ConfigField(order = 9, comment = "Message displayed when the player fails to leave due to missing permission.")
    public Message LeaveFailPermission;

    @ConfigField(order = 10, comment = "Message displayed when the player fails to leave, but it will be auto unlocked.")
    public Message LeaveFailAutoAllow;

    @ConfigField(order = 11, comment = "The sound event triggered on successful entry.")
    public SoundEvent SuccessEnterSound;

    @ConfigField(order = 12, comment = "The sound event triggered on successful exit.")
    public SoundEvent SuccessLeaveSound;

    @ConfigField(order = 13, comment = "The sound event triggered on failed entry.")
    public SoundEvent FailEnterSound;

    @ConfigField(order = 14, comment = "The sound event triggered on failed exit.")
    public SoundEvent FailLeaveSound;

    @ConfigField(order = 100, comment = "DO NOT TOUCH THIS. This helps handlig config related changes after updates.")
    public int FileVersion;

    public CommonConfig() {
        EnableDebugMode = false;
        Dimensions = new ArrayList<>();
        EnterSuccess = new Message(
                true,
                "§aEntering",
                "§e%display_name%",
                false,
                "Entering %display_name%.",
                false,
                "Entering %display_name%."
        );
        EnterFail = new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "Failed to enter into %display_name%.",
                false,
                "Failed to enter into %display_name%."
        );
        EnterFailPermission = new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "Failed to enter into %display_name% because of missing permission.",
                false,
                "Failed to enter into %display_name% because of missing permission."
        );
        EnterFailAutoAllow = new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "Unable to enter into %display_name%. The dimension will be unlocked after %time_left_to_unlock%s.",
                false,
                "Unable to enter into %display_name%. The dimension will be unlocked at %time_left_to_unlock%s"
        );

        LeaveSuccess = new Message(
                true,
                "§aLeaving",
                "§e%display_name%",
                false,
                "Leaving %display_name%.",
                false,
                "Leaving %display_name%."
        );
        LeaveFail = new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "Failed to leave from %display_name%.",
                false,
                "Failed to leave from %display_name%."
        );
        LeaveFailPermission = new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "Failed to leave from %display_name% because of missing permission.",
                false,
                "Failed to leave from %display_name% because of missing permission."
        );
        LeaveFailAutoAllow = new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "Unable to leave from %display_name%. The dimension will allow you to leave after %time_left_to_unlock%s.",
                false,
                "Unable to leave from %display_name%. The dimension will allow you to leave after %time_left_to_unlock%s."
        );

        SuccessEnterSound = SoundEvents.PLAYER_LEVELUP;
        SuccessLeaveSound = SoundEvents.PLAYER_LEVELUP;
        FailEnterSound = SoundEvents.VILLAGER_NO;
        FailLeaveSound = SoundEvents.VILLAGER_NO;

        FileVersion = 1;
    }
}
