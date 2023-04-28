package com.bymatej.minecraft.plugin.utils.entity;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bymatej.minecraft.plugin.utils.message.TableGenerator;

import static com.bymatej.minecraft.plugin.utils.logging.LoggingUtils.log;
import static com.bymatej.minecraft.plugin.utils.message.TableGenerator.Alignment.LEFT;
import static com.bymatej.minecraft.plugin.utils.message.TableGenerator.DASHES_LINE_DELIMITER;
import static java.lang.String.format;

public class NearPlayerEntitySpawner {

    private static final Random random = new Random();

    public static Entity spawnEntityNearPlayer(Player player, EntityType entityType, int minimumBlocksAway, int maximumBlocksAway, boolean broadcastMessage) {
        return spawnEntityNearPlayer(player, entityType, minimumBlocksAway, maximumBlocksAway, "", broadcastMessage);
    }

    public static Entity spawnEntityNearPlayer(Player player, EntityType entityType, int minimumBlocksAway, int maximumBlocksAway, String customEntityName, boolean broadcastMessage) {
        Location playerLocation = player.getLocation();

        Location entitySpawnLocation = getRandomLocation(playerLocation, minimumBlocksAway, maximumBlocksAway);
        int checks = 0;
        while (!isLocationSpawnable(entitySpawnLocation) &&
               checks < 100 &&
               locationIsVisible(player, entitySpawnLocation)) {

            entitySpawnLocation = getRandomLocation(playerLocation, minimumBlocksAway + 2, maximumBlocksAway);
            checks++;
        }

        Entity entity = player.getWorld().spawnEntity(entitySpawnLocation, entityType);
        if (customEntityName != null && customEntityName.length() > 0) {
            entity.setCustomNameVisible(true);
            entity.setCustomName(customEntityName);
        } else {
            entity.setCustomNameVisible(false);
        }

        sendMessage(entity, player, broadcastMessage);
        return entity;
    }

    private static boolean locationIsVisible(Player player, Location location) {
        Vector facingNormal = player.getLocation().getDirection().normalize();
        Vector playerEntityVecNormal = player.getEyeLocation().toVector().subtract(location.toVector()).normalize();
        return playerEntityVecNormal.dot(facingNormal) < 0;
    }

    private static Location getRandomLocation(Location location, int min, int max) {
        int randomX = random.nextInt(max - min) + min;
        int randomZ = random.nextInt(max - min) + min;

        if (random.nextBoolean()) {
            randomX *= -1;
        }
        if (random.nextBoolean()) {
            randomZ *= -1;
        }

        Location clone = location.clone();
        clone.setX(clone.getX() + randomX);
        clone.setZ(clone.getZ() + randomZ);

        int checksUpwards = 0;
        while (clone.getBlock().getRelative(BlockFace.DOWN).getType().isAir() && checksUpwards != 40) {
            clone.subtract(0, 1, 0);
            checksUpwards++;
        }

        while (!clone.getBlock().isEmpty()) {
            clone.add(0, 1, 0);
        }

        return clone;
    }

    private static boolean isLocationSpawnable(Location location) {
        if (location.clone().subtract(0, 1, 0).getBlock().getType().isSolid() &&
            !location.clone().subtract(0, 1, 0).getBlock().isLiquid() &&
            location.getBlock().getType().isAir()) {

            return location.clone().add(0, 1, 0).getBlock().getType().isAir();
        }

        return false;
    }

    private static void sendMessage(Entity entity, Player player, boolean broadcastMessage) {
        String entitySpawnLocationMessage = format("Spawning %s near %s at: ", entity.getType().name(), player.getName());
        if (entity.getCustomName() != null && entity.getCustomName().length() > 0) {
            entitySpawnLocationMessage = format("Spawning %s named %s near %s at: ", entity.getType().name(), entity.getCustomName(), player.getName());
        }
        String entitySpawnLocationFormatted = format("%s/%s/%s",
                                                     entity.getLocation().getBlockX(), entity.getLocation().getBlockY(), entity.getLocation().getBlockZ());
        String playerLocationMessage = format("The player %s is at: ", player.getName());
        String playerLocationFormatted = format("%s/%s/%s",
                                                player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());

        TableGenerator tableGenerator = new TableGenerator(LEFT, LEFT);
        tableGenerator.addRow(entitySpawnLocationMessage, entitySpawnLocationFormatted);
        tableGenerator.addRow(playerLocationMessage, playerLocationFormatted);
        tableGenerator.addRow(DASHES_LINE_DELIMITER, "");
        tableGenerator.generate().forEach(msg -> {
            log(msg);
            if (broadcastMessage) {
                player.sendMessage(msg);
            }
        });
    }

}
