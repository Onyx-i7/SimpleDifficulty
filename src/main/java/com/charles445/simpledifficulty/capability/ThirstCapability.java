package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

/**
 * Capability implementation for the Thirst system.
 * Handles thirst exhaustion, saturation, and dehydration damage.
 */
public class ThirstCapability implements IThirstCapability {
    private float exhaustion = 0.0f;
    private int thirst = 20;
    private float saturation = 5.0f;
    private int ticktimer = 0;
    private int damagecounter = 0;

    // Unsaved data
    private int oldthirst = 0;
    private float oldsaturation = 0.0f;
    private double posX, posY, posZ;
    private boolean positionInitialized = false;
    private int packetTimer = 0;

    @Override
    public void tickUpdate(PlayerEntity player, World world, TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.START) {
            packetTimer++;
            return;
        }

        // Initialize position
        if (!positionInitialized) {
            posX = player.position().x();
            posY = player.position().y();
            posZ = player.position().z();
            positionInitialized = true;
        }

        // Calculate movement distance
        double dx = player.position().x() - posX;
        double dy = player.position().y() - posY;
        double dz = player.position().z() - posZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;

        double distance = Math.sqrt(distanceSquared);
        int moveDistance = (int) Math.round(distance * 100);

        // Update position
        posX = player.position().x();
        posY = player.position().y();
        posZ = player.position().z();

        // Avoid getting thirsty on teleport
        if (moveDistance > 1000) {
            moveDistance = 0;
        }

        if (moveDistance > 0) {
            // Manage exhaustion
            float moveSensitivity = (float) ModConfig.SERVER.thirstBaseMovement.get();
            if (player.isInWater() || player.isInWaterOrBubble()) {
                moveSensitivity = (float) ModConfig.SERVER.thirstSwimmingMovement.get();
            } else if (player.isOnGround()) {
                if (player.isSprinting()) {
                    moveSensitivity = (float) ModConfig.SERVER.thirstSprintingMovement.get();
                } else {
                    moveSensitivity = (float) ModConfig.SERVER.thirstWalkingMovement.get();
                }
            }
            this.addThirstExhaustion(moveSensitivity * 0.01f * moveDistance);
        }

        // Process exhaustion
        if (this.getThirstExhaustion() > (float) ModConfig.SERVER.thirstExhaustionLimit.get()) {
            this.addThirstExhaustion(-1.0f * (float) ModConfig.SERVER.thirstExhaustionLimit.get());

            if (this.getThirstSaturation() > 0.0f) {
                this.addThirstSaturation(-1.0f);
            } else if (DamageUtil.isModDangerous(world)) {
                this.addThirstLevel(-1);
            }
        }

        // Dehydration damage
        if (this.getThirstLevel() <= 0) {
            this.addThirstTickTimer(1);
            if (this.getThirstTickTimer() >= 80) {
                this.setThirstTickTimer(0);

                if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                    float thirstDamageToApply = 1.0f + (1.0f * (float) this.getThirstDamageCounter() * (float) ModConfig.SERVER.thirstDamageScaling.get());
                    player.hurt(SDDamageSources.DEHYDRATION, thirstDamageToApply);
                    this.addThirstDamageCounter(1);
                }
            }
        } else {
            this.setThirstTickTimer(0);
            this.setThirstDamageCounter(0);
        }
    }

    @Override
    public boolean isDirty() {
        return (this.thirst != this.oldthirst || this.saturation != this.oldsaturation);
    }

    @Override
    public void setClean() {
        this.oldthirst = this.thirst;
        this.oldsaturation = this.saturation;
    }

    @Override
    public float getThirstExhaustion() {
        return exhaustion;
    }

    @Override
    public int getThirstLevel() {
        return thirst;
    }

    @Override
    public float getThirstSaturation() {
        return saturation;
    }

    @Override
    public int getThirstTickTimer() {
        return ticktimer;
    }

    @Override
    public int getThirstDamageCounter() {
        return damagecounter;
    }

    @Override
    public void setThirstExhaustion(float exhaustion) {
        this.exhaustion = Math.max(exhaustion, 0.0f);

        if (!Float.isFinite(this.exhaustion)) {
            this.exhaustion = 0.0f;
        }
    }

    @Override
    public void setThirstLevel(int thirst) {
        this.thirst = MathHelper.clamp(thirst, 0, 20);
    }

    @Override
    public void setThirstSaturation(float saturation) {
        this.saturation = MathHelper.clamp(saturation, 0.0f, 20.0f);

        if (!Float.isFinite(this.saturation)) {
            this.saturation = 0.0f;
        }
    }

    @Override
    public void setThirstTickTimer(int ticktimer) {
        this.ticktimer = ticktimer;
    }

    @Override
    public void setThirstDamageCounter(int damagecounter) {
        this.damagecounter = damagecounter;
    }

    @Override
    public void addThirstExhaustion(float exhaustion) {
        this.setThirstExhaustion(this.getThirstExhaustion() + exhaustion);
    }

    @Override
    public void addThirstLevel(int thirst) {
        this.setThirstLevel(this.getThirstLevel() + thirst);
    }

    @Override
    public void addThirstSaturation(float saturation) {
        this.setThirstSaturation(this.getThirstSaturation() + saturation);
    }

    @Override
    public void addThirstTickTimer(int ticktimer) {
        this.setThirstTickTimer(this.getThirstTickTimer() + ticktimer);
    }

    @Override
    public void addThirstDamageCounter(int damagecounter) {
        this.setThirstDamageCounter(this.getThirstDamageCounter() + damagecounter);
    }

    @Override
    public boolean isThirsty() {
        return this.getThirstLevel() < 20;
    }

    @Override
    public int getPacketTimer() {
        return packetTimer;
    }
}