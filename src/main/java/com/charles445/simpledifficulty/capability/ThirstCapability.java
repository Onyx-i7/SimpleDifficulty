package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ThirstCapability implements IThirstCapability {
    private float exhaustion = 0.0f;
    private int thirst = 20;
    private float saturation = 5.0f;
    private int ticktimer = 0;
    private int damagecounter = 0;
    
    // Unsaved data - using primitives instead of Vector3d to avoid object allocation
    private int oldthirst = 0;
    private float oldsaturation = 0.0f;
    private double posX, posY, posZ;
    private boolean positionInitialized = false;
    private int packetTimer = 0;
    
    @Override
    public void tickUpdate(EntityPlayer player, World world, TickEvent.Phase phase) {
        // Allowing sprinting, for now, I don't see a reliable way to actually disable player sprinting
        // Stop sprinting when thirsty (same as hunger)
        if (phase == TickEvent.Phase.START) {
            // checkSprint(player);
            packetTimer++;
            return;
        }
        
        // Initialize position
        if (!positionInitialized) {
            posX = player.posX;
            posY = player.posY;
            posZ = player.posZ;
            positionInitialized = true;
        }
        
        // Calculate movement distance
        double dx = player.posX - posX;
        double dy = player.posY - posY;
        double dz = player.posZ - posZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        
        // Use Math.sqrt() for accurate distance calculation (much faster than Vector3d allocation)
        double distance = Math.sqrt(distanceSquared);
        int moveDistance = (int) Math.round(distance * 100);
        
        // Update position
        posX = player.posX;
        posY = player.posY;
        posZ = player.posZ;
        
        // Avoid getting thirsty on teleport (if you can move 10 blocks in a tick, you win!)
        if (moveDistance > 1000) {
            moveDistance = 0;
        }
        
        if (moveDistance > 0) {
            // Manage exhaustion
            float moveSensitivity = (float) ModConfig.server.thirst.thirstBaseMovement;
            if (player.isInWater() || player.isInsideOfMaterial(Material.WATER)) {
                moveSensitivity = (float) ModConfig.server.thirst.thirstSwimmingMovement;
            } else if (player.onGround) {
                if (player.isSprinting()) {
                    moveSensitivity = (float) ModConfig.server.thirst.thirstSprintingMovement;
                } else {
                    moveSensitivity = (float) ModConfig.server.thirst.thirstWalkingMovement;
                }
            }
            // Sensitive to every hundredth of a block, so multiply by 1/100
            this.addThirstExhaustion(moveSensitivity * 0.01f * moveDistance);
        }
        
        // Process exhaustion to determine whether to make thirsty
        if (this.getThirstExhaustion() > (float) ModConfig.server.thirst.thirstExhaustionLimit) {
            // Exhausted, do a thirst tick
            this.addThirstExhaustion(-1.0f * (float) ModConfig.server.thirst.thirstExhaustionLimit);
            
            if (this.getThirstSaturation() > 0.0f) {
                // Exhaust from saturation
                this.addThirstSaturation(-1.0f);
            } else if (DamageUtil.isModDangerous(world)) {
                // Exhaust from thirst
                this.addThirstLevel(-1);
            }
        }
        
        // Hurt ticking
        if (this.getThirstLevel() <= 0) {
            this.addThirstTickTimer(1);
            if (this.getThirstTickTimer() >= 80) {
                this.setThirstTickTimer(0);
                
                if (DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player)) {
                    float thirstDamageToApply = 1.0f + (1.0f * (float) this.getThirstDamageCounter() * (float) ModConfig.server.thirst.thirstDamageScaling);
                    player.attackEntityFrom(SDDamageSources.DEHYDRATION, thirstDamageToApply);
                    this.addThirstDamageCounter(1);
                }
            }
        } else {
            // Reset the timer if not dying of thirst
            this.setThirstTickTimer(0);
            this.setThirstDamageCounter(0);
        }
        
        // checkSprint(player);
    }
    
    private void checkSprint(EntityPlayer player) {
        // Server side sprinting check
        if (player.isSprinting() && this.getThirstLevel() <= 6) {
            player.setSprinting(false);
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
