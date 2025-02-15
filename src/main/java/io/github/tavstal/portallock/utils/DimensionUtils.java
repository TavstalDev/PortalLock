package io.github.tavstal.portallock.utils;

import io.github.tavstal.portallock.PortalLock;
import io.github.tavstal.portallock.models.DimensionData;
import io.github.tavstal.portallock.models.ESoundType;
import io.github.tavstal.portallock.models.EAnnouncement;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for handling dimension-related operations in the PortalLock plugin.
 */
public class DimensionUtils {
    private static Map<String, Object> _data;
    public static List<DimensionData> Dimensions;

    /**
     * Loads the dimension data from the configuration file.
     *
     * @return true if the data was loaded successfully, false otherwise.
     */
    public static Boolean Load() {
        InputStream inputStream;

        Path filePath = Paths.get(PortalLock.Instance.getDataFolder().getPath(), "dimensions.yml");

        if (!Files.exists(filePath))
        {
            try {
                // Get the resource URL
                String localePath = "/dimensions.yml";
                inputStream = PortalLock.Instance.getClass().getResourceAsStream(localePath);
                if (inputStream == null) {
                    throw new IOException("Resource file not found: " + localePath);
                }
                // Copy the file
                Files.copy(inputStream, filePath);

                // Fill the dimensions list
                List<DimensionData> dimensions = new ArrayList<>();
                for (var level : PortalLock.Instance.getServer().getWorlds()) {
                    String name = level.getName();
                    String displayName;
                    if (name.contains(":"))
                        displayName = name.split(":")[1];
                    else
                        displayName = name;
                    displayName = displayName.replaceAll("_", " ").toUpperCase();
                    dimensions.add(new DimensionData(name, displayName, false, "2025-01-01 00:00", "2025-01-01 00:00",
                                    true, true, "minecraft:overworld",
                                    true, false, name.replaceAll(":", ".") + ".enter", true, false, name.replaceAll(":", ".") + ".leave"
                            )
                    );
                }

                // Convert dimensions list to YAML
                Yaml yaml = new Yaml();
                Map<String, Object> yamlData = yaml.load(new FileInputStream(filePath.toString()));
                yamlData.put("dimensions", dimensions);

                // Write the updated data back to the file
                try (FileWriter writer = new FileWriter(filePath.toString())) {
                    yaml.dump(yamlData, writer);
                }

                // Close the input stream
                inputStream.close();
            } catch (IOException e) {
                LoggerUtils.LogError("Failed to get resource file.");
            }
        }

        try
        {
            inputStream = new FileInputStream(filePath.toString());
        }
        catch (FileNotFoundException ex)
        {
            LoggerUtils.LogError(String.format("Failed to get file. Path: %s", filePath));
            return false;
        }
        catch (Exception ex)
        {
            LoggerUtils.LogWarning("Unknown error happened while reading the file.");
            LoggerUtils.LogError(ex.getMessage());
            return false;
        }

        Yaml yaml = new Yaml();
        Object yamlObject = yaml.load(inputStream);
        if (!(yamlObject instanceof Map))
        {
            LoggerUtils.LogError("Failed to cast the yamlObject after reading the file data.");
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> localValue = (Map<String, Object>)yamlObject;
        _data = localValue; // Warning fix
        if (!_data.containsKey("dimensions"))
        {
            LoggerUtils.LogError("Failed to find the dimensions key in the configuration file.");
            return false;
        }
        Dimensions = _data.get("dimensions") instanceof List ? (List<DimensionData>)_data.get("dimensions") : new ArrayList<>();
        return true;
    }

    /**
     * Saves the dimension data to the configuration file.
     *
     * @return true if the data was saved successfully, false otherwise.
     */
    public static Boolean SaveConfig() {
        try {
            Path filePath = Paths.get(PortalLock.Instance.getDataFolder().getPath(), "dimensions.yml");
            Yaml yaml = new Yaml();
            _data.put("dimensions", Dimensions);
            try (FileWriter writer = new FileWriter(filePath.toString())) {
                yaml.dump(_data, writer);
            }
            return true;
        }
        catch (Exception ex) {
            LoggerUtils.LogError("Failed to save the configuration file.");
            LoggerUtils.LogError(ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if the player should be prevented from changing dimensions.
     *
     * @param player   The player to check.
     * @param oldLevel The old dimension.
     * @param newLevel The new dimension.
     * @return true if the player should be prevented from changing dimensions, false otherwise.
     */
    public static boolean shouldPreventDimensionChange(Player player, World oldLevel, World newLevel) {
        try {
            DimensionData oldDimensionData = null;
            DimensionData newDimensionData = null;
            for (var dimensionData : Dimensions) {
                if (Objects.equals(dimensionData.Key, oldLevel.getName()))
                    oldDimensionData = dimensionData;

                if (Objects.equals(dimensionData.Key, newLevel.getName()))
                    newDimensionData = dimensionData;
            }

            World playerLevel = null;
            try {
                playerLevel = player.getWorld();
            } catch(Exception ex) { /* ignore */}
            if (playerLevel == null)
                return  false;

            if (oldDimensionData != null) {
                if (oldDimensionData.RequireLeavePermission && player.hasPermission(oldDimensionData.LeavePermission)) {
                    oldDimensionData.SendMessage(player, EAnnouncement.Leave_Fail_Permission);
                    if (PortalLock.Instance.getSound(ESoundType.FailLeave) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailLeave));
                    return true;
                }

                if (oldDimensionData.AutoAllowByDate && Duration.between(LocalDateTime.now(), oldDimensionData.GetLeaveDate()).getSeconds() > 0) {
                    oldDimensionData.SendMessage(player, EAnnouncement.Leave_Fail_Auto_Unlock);
                    if (PortalLock.Instance.getSound(ESoundType.FailLeave) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailLeave));
                    return true;
                }

                if (!oldDimensionData.AllowLeave) {
                    oldDimensionData.SendMessage(player, EAnnouncement.Leave_Fail);
                    if (PortalLock.Instance.getSound(ESoundType.FailLeave) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailLeave));
                    return true;
                }
            }

            if (newDimensionData != null) {
                if (newDimensionData.RequireEnterPermission && player.hasPermission(newDimensionData.EnterPermission)) {
                    newDimensionData.SendMessage(player, EAnnouncement.Enter_Fail_Permission);
                    if (PortalLock.Instance.getSound(ESoundType.FailEnter) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailEnter));
                    return true;
                }

                if (newDimensionData.AutoAllowByDate && Duration.between(LocalDateTime.now(), newDimensionData.GetEnterDate()).getSeconds() > 0) {
                    newDimensionData.SendMessage(player, EAnnouncement.Enter_Fail_Auto_Unlock);
                    if (PortalLock.Instance.getSound(ESoundType.FailEnter) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailEnter));
                    return true;
                }

                if (!newDimensionData.AllowEnter) {
                    newDimensionData.SendMessage(player, EAnnouncement.Enter_Fail);
                    if (PortalLock.Instance.getSound(ESoundType.FailEnter) != null)
                        player.playSound(PortalLock.Instance.getSound(ESoundType.FailEnter));
                    return true;
                }
            }

            if (PortalLock.Instance.getSound(ESoundType.Success) != null)
                player.playSound(PortalLock.Instance.getSound(ESoundType.Success));

        } catch (Exception ex) {
            LoggerUtils.LogError("Error in shouldPreventDimensionChange:");
            LoggerUtils.LogError(ex.getMessage());
        }
        return false;
    }

    /**
     * Checks if the player should be prevented from entering the dimension.
     *
     * @param player The player to check.
     */
    public static void checkDimension(Player player) {
        try {
            DimensionData currentDimension = null;
            for (var dimensionData : Dimensions) {
                if (Objects.equals(dimensionData.Key, player.getWorld().getName())) {
                    currentDimension = dimensionData;
                    break;
                }
            }

            if (currentDimension == null)
                return;

            if (!currentDimension.KickUnauthorizedPlayers)
                return;

            if (currentDimension.RequireEnterPermission && player.hasPermission(currentDimension.EnterPermission)) {
                currentDimension.SendMessage(player, EAnnouncement.Enter_Fail_Permission);
                kickOutOfDimension(player, currentDimension);
                return;
            }

            if (currentDimension.AutoAllowByDate && Duration.between(LocalDateTime.now(), currentDimension.GetEnterDate()).getSeconds() > 0) {
                currentDimension.SendMessage(player, EAnnouncement.Enter_Fail_Auto_Unlock);
                kickOutOfDimension(player, currentDimension);
                return;
            }

            if (!currentDimension.AllowEnter) {
                currentDimension.SendMessage(player, EAnnouncement.Enter_Fail);
                kickOutOfDimension(player, currentDimension);
            }
        }
        catch (Exception ex) {
            LoggerUtils.LogError("Error in checkDimension:");
            LoggerUtils.LogError(ex.getLocalizedMessage());
        }
    }

    /**
     * Kicks a player out of the current dimension to a specified target dimension or their respawn location.
     *
     * @param player The player to be kicked out.
     * @param currentDimension The current dimension data of the player.
     */
    private static void kickOutOfDimension(Player player, DimensionData currentDimension) {
        try {
            if (currentDimension.AllowKickToBed && player.getRespawnLocation() != null) {
                var respawnLoc = player.getRespawnLocation();
                if (respawnLoc != null) {
                    player.teleport(respawnLoc);
                    return;
                }
            }

            World targetLevel = PortalLock.Instance.getServer().getWorld(currentDimension.KickTargetDimension);
            if (targetLevel == null)
                return;

            var spawnLoc = targetLevel.getSpawnLocation();
            player.teleport(spawnLoc);
        }
        catch (Exception ex) {
            LoggerUtils.LogError("Error in kickOutOfDimension:");
            LoggerUtils.LogError(ex.getLocalizedMessage());
        }
    }
}
