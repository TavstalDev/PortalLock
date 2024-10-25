package io.github.tavstal.portallock.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A utility class that provides static methods for performing operations
 * related to entities in the game world.
 *
 * <p>
 * This class includes methods for checking entity types, retrieving positions,
 * calculating distances, and other common operations that can be performed
 * on or with entities. It is designed to simplify interactions with entities
 * and reduce redundancy in code throughout the mod.
 * </p>
 *
 * <p>
 * All methods in this class are static and should not be instantiated.
 * This class is intended to be used as a helper class, facilitating
 * better organization and readability of entity-related operations.
 * </p>
 *
 * @since 1.0
 */
public class EntityUtils {
    /**
     * Retrieves the name of the specified entity.
     *
     * <p>
     * This method extracts the display name of the provided entity.
     * </p>
     *
     * @param entity the entity whose name is to be retrieved
     * @return the name of the entity, or an empty string if the entity is null
     * @throws IllegalArgumentException if the entity is null
     */
    public static String GetName(Entity entity) {
        return entity.getName().getString();
    }

    /**
     * Checks if the specified entity is a player.
     *
     * <p>
     * This method determines whether the provided entity is an instance of
     * a player entity. It can be used to filter or handle player-specific
     * logic in gameplay mechanics.
     * </p>
     *
     * @param entity the entity to check
     * @return true if the entity is a player, false otherwise
     */
    public static boolean IsPlayer(Entity entity)
    {
        return entity instanceof Player;
    }

    /**
     * Checks if the specified entity is a living entity.
     *
     * <p>
     * This method determines whether the provided entity is an instance of a living entity,
     * such as a player, mob, or any other entity that can be alive in the game.
     * It can be useful for filtering or handling logic that applies only to living entities.
     * </p>
     *
     * @param entity the entity to check
     * @return true if the entity is a living entity, false otherwise
     */
    public static boolean IsLiving(Entity entity)
    {
        return entity instanceof LivingEntity;
    }

    /**
     * Retrieves the level (dimension) of the specified entity.
     *
     * <p>
     * This method returns the {@link Level} instance representing the dimension
     * in which the provided entity is currently located. The level can be
     * used for various purposes, such as determining the environment of the
     * entity or for processing level-specific logic.
     * </p>
     *
     * @param entity the entity whose level is to be retrieved
     * @return the {@link Level} instance corresponding to the entity's current dimension,
     *         or null if the entity is not in a valid level
     */
    public static Level GetLevel(Entity entity) {
        return entity.level();
    }

    /**
     * Retrieves the {@link ServerLevel} in which the specified entity is located.
     *
     * <p>
     * This method returns the {@link ServerLevel} instance representing the dimension
     * in which the provided entity is currently situated. The server level can be
     * utilized for operations that require knowledge of the entity's environment
     * or for performing dimension-specific actions.
     * </p>
     *
     * @param entity the entity whose server level is to be retrieved
     * @return the {@link ServerLevel} instance corresponding to the entity's current
     *         dimension, or null if the entity is not in a valid server level
     */
    public static ServerLevel GetServerLevel(Entity entity) {
        var server = entity.getServer();
        if (server == null)
            return null;

        return server.getLevel(GetLevel(entity).dimension());
    }

    /**
     * Retrieves the position of the specified entity as a {@link Vec3} object.
     *
     * <p>
     * This method returns a {@link Vec3} instance representing the current coordinates
     * (X, Y, Z) of the provided entity in the world. This information can be used for
     * various purposes, such as calculating distances, spawning entities, or determining
     * the entity's location within a game mechanic.
     * </p>
     *
     * @param entity the entity whose position is to be retrieved
     * @return a {@link Vec3} object representing the entity's current position,
     *         or null if the entity is not valid or does not have a position
     */
    public static Vec3 GetPosition(Entity entity) {
        return entity.position();
    }

    /**
     * Retrieves the block position of the specified entity as a {@link BlockPos} object.
     *
     * <p>
     * This method returns a {@link BlockPos} instance representing the block coordinates
     * (X, Y, Z) of the provided entity in the world. The block position can be useful for
     * operations involving block interactions, such as placing or breaking blocks,
     * as well as for determining the entity's location relative to other blocks.
     * </p>
     *
     * @param entity the entity whose block position is to be retrieved
     * @return a {@link BlockPos} object representing the block position of the entity,
     *         or null if the entity is not valid or does not have a block position
     */
    public static BlockPos GetBlockPosition(Entity entity) {
        return entity.blockPosition();
    }

    /**
     * Calculates the walking speed of the specified entity based on its old position.
     *
     * <p>
     * This method determines the speed at which the entity is walking by comparing its current
     * position with its old position. The walking speed is calculated as the distance traveled
     * between the two positions divided by the time elapsed. This can be useful for gameplay
     * mechanics, such as detecting sprinting or walking behavior.
     * </p>
     *
     * @param entity the entity whose walking speed is to be calculated
     * @param oldPos the previous position of the entity as a {@link Vec3}
     * @return the walking speed of the entity as a {@code double} value,
     *         representing the distance traveled per tick
     */
    public static double GetWalkSpeed(Entity entity, Vec3 oldPos) {
        var deltaX = entity.getX() - oldPos.x;
        var deltaZ = entity.getZ() - oldPos.z;
        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
    }

    /**
     * Calculates the squared distance from the specified entity to a given point in 3D space.
     *
     * <p>
     * This method computes the squared distance to avoid the computational overhead
     * of calculating the square root, which is unnecessary if only the comparison of distances is needed.
     * This can be useful for various gameplay mechanics, such as determining proximity
     * between entities or triggering events based on distance thresholds.
     * </p>
     *
     * @param entity the entity from which the distance is measured
     * @param vec3 the target point in 3D space represented as a {@link Vec3}
     * @return the squared distance between the entity and the specified point
     *         as a {@code double} value
     */
    public static double DistanceToSqr(Entity entity, Vec3 vec3) {
        double d = entity.getX() - vec3.x;
        double e = entity.getY() - vec3.y;
        return d * d + e * e;
    }
}
