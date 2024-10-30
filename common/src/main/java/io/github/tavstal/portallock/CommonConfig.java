package io.github.tavstal.portallock;

import io.github.tavstal.portallock.models.ConfigField;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.ESoundType;
import io.github.tavstal.portallock.models.Message;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the common configuration settings for the mod.
 * This class contains fields and methods to manage the configuration,
 * including options that can be customized by the server administrator.
 * It is designed to load and save configuration data, ensuring
 * proper handling of mod settings across different server instances.
 */
public class CommonConfig {

    @ConfigField(comment = "Shows more logs than usual. Helps locating errors.")
    public boolean EnableDebugMode;

    @ConfigField(comment = "Specifies the language used by the application.")
    public String Language;

    @ConfigField(comment = "How often should the server check the auto allow dates in minutes.")
    public int UpdateInterval;

    @ConfigField(comment = "List of the dimensions.\nAuto populated during first start.")
    public List<DimensionData> Dimensions;

    @ConfigField(comment = "The message shown to players on successful entry.")
    public Message EnterSuccess;

    @ConfigField(comment = "The message shown to players when entry fails.")
    public Message EnterFail;

    @ConfigField(comment = "Message displayed when the player fails to enter due to missing permission.")
    public Message EnterFailPermission;

    @ConfigField(comment = "Message displayed when the player fails to enter, but it will be auto unlocked.")
    public Message EnterFailAutoAllow;

    @ConfigField(comment = "The message shown to players on successful exit.")
    public Message LeaveSuccess;

    @ConfigField(comment = "The message shown to players when exit fails.")
    public Message LeaveFail;

    @ConfigField(comment = "Message displayed when the player fails to leave due to missing permission.")
    public Message LeaveFailPermission;

    @ConfigField(comment = "Message displayed when the player fails to leave, but it will be auto unlocked.")
    public Message LeaveFailAutoAllow;

    @ConfigField(comment = "The sound event triggered on successful entry.")
    public String SuccessSound;

    @ConfigField(comment = "The sound event triggered on failed entry.")
    public String FailEnterSound;

    @ConfigField(comment = "The sound event triggered on failed exit.")
    public String FailLeaveSound;

    @ConfigField(comment = "DO NOT TOUCH THIS. This helps handlig config related changes after updates.")
    public int FileVersion;

    public CommonConfig() {
        EnableDebugMode = false;
        Language = "en";
        UpdateInterval = 15;
        Dimensions = new ArrayList<>();
        EnterSuccess =  new Message(
                true,
                "§aEntering",
                "§e%display_name%",
                false,
                "§8[§bPortal§3Lock§8] §aEntering §e%display_name%§a.",
                false,
                "§aEntering §e%display_name%§a."
        );
        EnterFail =  new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cFailed to enter into §e%display_name%§c.",
                false,
                "§cFailed to enter into §e%display_name%§c."
        );
        EnterFailPermission =  new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cFailed to enter into §e%display_name%§c because of missing permission.",
                false,
                "§cFailed to enter into §e%display_name%§c because of missing permission."
        );
        EnterFailAutoAllow =  new Message(
                false,
                "§cFailed to enter",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cUnable to enter into §e%display_name%§c. The dimension will be unlocked after §e%time_left_to_unlock%",
                false,
                "§cUnable to enter into §e%display_name%§c. The dimension will be unlocked at §e%time_left_to_unlock%"
        );

        LeaveSuccess =  new Message(
                true,
                "§aLeaving",
                "§e%display_name%",
                false,
                "§8[§bPortal§3Lock§8] §aLeaving §e%display_name%§a.",
                false,
                "§aLeaving §e%display_name%§a."
        );
        LeaveFail =  new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cFailed to leave from §e%display_name%§c.",
                false,
                "§cFailed to leave from §e%display_name%§c."
        );
        LeaveFailPermission =  new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cFailed to leave from §e%display_name%§c because of missing permission.",
                false,
                "§cFailed to leave from §e%display_name%§c because of missing permission."
        );
        LeaveFailAutoAllow =  new Message(
                false,
                "§cFailed to leave",
                "§e%display_name%",
                true,
                "§8[§bPortal§3Lock§8] §cUnable to leave from §e%display_name%§c. The dimension will allow you to leave after §e%time_left_to_unlock%",
                false,
                "§cUnable to leave from §e%display_name%§c. The dimension will allow you to leave after §e%time_left_to_unlock%"
        );

        SuccessSound =  SoundEvents.PLAYER_LEVELUP.getLocation().toString();
        FailEnterSound =  SoundEvents.VILLAGER_NO.getLocation().toString();
        FailLeaveSound =  SoundEvents.VILLAGER_NO.getLocation().toString();

        FileVersion = 1;
    }

    public SoundEvent GetSoundEvent(ESoundType type) {
        String value = "";
        try {
            switch (type) {
                case ESoundType.Success -> {
                    value = SuccessSound;
                }
                case ESoundType.FailEnter -> {
                    value = FailEnterSound;
                }
                case ESoundType.FailLeave -> {
                    value = FailLeaveSound;
                }
            }

            if (value.equalsIgnoreCase("none"))
                return null;

            return SoundEvent.createVariableRangeEvent(ResourceLocation.parse(value));
        }
        catch (Exception ex) {
            CommonClass.LOG.warn(MessageFormat.format("Invalid sound value ''{0}''.", value));
            CommonClass.LOG.error(ex.getLocalizedMessage());
            return null;
        }
    }
}
