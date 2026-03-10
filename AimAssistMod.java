package com.aimassist;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AimAssistMod implements ClientModInitializer {

    public static final String MOD_ID = "aimassist";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Aim assist sozlamalari
    public static boolean isEnabled = true;
    public static double aimRange = 7.0;      // Asosiy aim diapazoni (7 blok)
    public static double maxRange = 10.0;     // Maksimal diapazon (10 blok)
    public static float aimStrength = 0.15f;  // Aim kuchi (0.0 - 1.0)

    @Override
    public void onInitializeClient() {
        LOGGER.info("=================================");
        LOGGER.info("  Aim Assist Mod yuklandi!");
        LOGGER.info("  Minecraft 1.21.11 - Fabric");
        LOGGER.info("  Diapazon: {}-{} blok", aimRange, maxRange);
        LOGGER.info("=================================");

        // Tugmalarni ro'yxatdan o'tkazish
        AimAssistKeybinds.register();

        // Har bir tick da aim assistni ishlatish
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (!isEnabled) return;
        if (client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;

        // Eng yaqin dushmanni topish
        LivingEntity target = findBestTarget(client);
        if (target == null) return;

        // Crosshairni dushmanga yo'naltirish
        applyAimAssist(client, target);
    }

    private LivingEntity findBestTarget(MinecraftClient client) {
        PlayerEntity player = client.player;
        LivingEntity bestTarget = null;
        double bestScore = Double.MAX_VALUE;

        Vec3d playerLook = player.getRotationVec(1.0f);
        Vec3d playerEye = player.getEyePos();

        List<Entity> entities = client.world.getEntitiesByClass(
            LivingEntity.class,
            player.getBoundingBox().expand(maxRange),
            entity -> isValidTarget(entity, player)
        );

        for (Entity entity : entities) {
            LivingEntity living = (LivingEntity) entity;
            double distance = player.distanceTo(entity);

            if (distance > maxRange || distance < 0.5) continue;

            // Entity ga yo'nalish
            Vec3d toEntity = entity.getEyePos().subtract(playerEye).normalize();
            double dotProduct = playerLook.dotProduct(toEntity);
            double angle = Math.acos(Math.max(-1.0, Math.min(1.0, dotProduct)));

            // 90 daraja ichidagi dushmanlarni tekshirish
            if (angle > Math.PI / 2) continue;

            // Burchak + masofaga qarab ball hisoblash (kichik = yaxshi)
            double score = angle * 2.0 + distance * 0.1;

            if (score < bestScore) {
                bestScore = score;
                bestTarget = living;
            }
        }

        return bestTarget;
    }

    private boolean isValidTarget(Entity entity, PlayerEntity player) {
        if (entity == player) return false;
        if (!(entity instanceof LivingEntity living)) return false;
        if (!entity.isAlive()) return false;
        if (entity instanceof PlayerEntity) return false;

        // 1.21.11 barcha hostile mob'lari + zombie horse kabi undead
        if (entity instanceof HostileEntity) return true;

        // Zombie horse (1.21.11 da natural spawn bo'ladi)
        if (entity instanceof MobEntity mob) {
            return mob.isUndead() && !(entity instanceof PlayerEntity);
        }

        return false;
    }

    private void applyAimAssist(MinecraftClient client, LivingEntity target) {
        PlayerEntity player = client.player;
        double distance = player.distanceTo(target);

        // Masofaga qarab kuch: 7 blokgacha to'liq, 7-10 da kamayib boradi
        float strengthMultiplier;
        if (distance <= aimRange) {
            strengthMultiplier = 1.0f;
        } else {
            float t = (float)((distance - aimRange) / (maxRange - aimRange));
            strengthMultiplier = 1.0f - t * 0.6f;
        }

        // Nishon markazini hisoblash
        Vec3d targetPos = target.getEyePos();
        Vec3d playerEye = player.getEyePos();
        Vec3d direction = targetPos.subtract(playerEye);

        // Yaw (chapga-o'ngga) hisoblash
        double targetYaw = Math.toDegrees(Math.atan2(-direction.x, direction.z));

        // Pitch (yuqoriga-pastga) hisoblash
        double horizontalDist = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double targetPitch = Math.toDegrees(-Math.atan2(direction.y, horizontalDist));

        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        // Yaw farqini normallashtirish
        float yawDiff = (float)(targetYaw - currentYaw);
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        float pitchDiff = (float)(targetPitch - currentPitch);

        // Silliq harakat
        float finalStrength = aimStrength * strengthMultiplier;
        float newYaw = currentYaw + yawDiff * finalStrength;
        float newPitch = currentPitch + pitchDiff * finalStrength;
        newPitch = Math.max(-90f, Math.min(90f, newPitch));

        player.setYaw(newYaw);
        player.setPitch(newPitch);
    }
}
