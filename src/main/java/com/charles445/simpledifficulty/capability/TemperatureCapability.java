package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.temperature.*;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.WorldUtil;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class TemperatureCapability implements ITemperatureCapability {
    private int temperature = 12;
    private int ticktimer = 0;
    private int damagecounter = 0;

    private final Map<String, TemporaryModifier> temporaryModifiers = new HashMap<>();

    // Unsaved data
    private int oldtemperature = 0;
    private int updatetimer = 500;
    private int targettemp = 0;
    private int debugtimer = 0;
    private boolean manualDirty = false;
    private int oldmodifiersize = 0;
    private int packetTimer = 0;

    @Override
    public int getTemperatureLevel() {
        return temperature;
    }

    @Override
    public int getTemperatureTickTimer() {
        return ticktimer;
    }

    @Override
    public int getTemperatureDamageCounter() {
        return damagecounter;
    }

    @Override
    public void setTemperatureLevel(int temperature) {
        this.temperature = TemperatureUtil.clampTemperature(temperature);
    }

    @Override
    public void setTemperatureTickTimer(int ticktimer) {
        this.ticktimer = ticktimer;
    }

    @Override
    public void setTemperatureDamageCounter(int damagecounter) {
        this.damagecounter = damagecounter;
    }

    @Override
    public void addTemperatureLevel(int temperature) {
        this.setTemperatureLevel(this.getTemperatureLevel() + temperature);
    }

    @Override
    public void addTemperatureTickTimer(int ticktimer) {
        this.setTemperatureTickTimer(this.getTemperatureTickTimer() + ticktimer);
    }

    @Override
    public void addTemperatureDamageCounter(int damagecounter) {
        this.setTemperatureDamageCounter(this.getTemperatureDamageCounter() + damagecounter);
    }

    @Override
    public void tickUpdate(PlayerEntity player, World world, TickEvent.Phase phase) {
        if (phase == TickEvent.Phase.START) {
            packetTimer++;
            return;
        }

        debugtimer++;
        if (debugtimer >= 40 && ServerConfig.instance.getBoolean(ServerOptions.DEBUG)) {
            debugtimer = 0;
            debugRoutine(player, world);
        }

        updatetimer++;
        if (updatetimer >= 5) {
            updatetimer = 0;
            targettemp = TemperatureUtil.getPlayerTargetTemperature(player);
        }

        addTemperatureTickTimer(1);

        boolean appliedEffect = false;

        if (getTemperatureTickTimer() >= getTemperatureTickLimit()) {
            setTemperatureTickTimer(0);
            int destinationTemp = TemperatureUtil.clampTemperature(targettemp);
            if (getTemperatureLevel() != destinationTemp) {
                if (getTemperatureLevel() > destinationTemp)
                    addTemperatureLevel(-1);
                else
                    addTemperatureLevel(1);
            }

            TemperatureEnum tempEnum = getTemperatureEnum();
            if (tempEnum == TemperatureEnum.BURNING) {
                if (TemperatureEnum.BURNING.getMiddle() < getTemperatureLevel() 
                        && !player.hasEffect(SDPotions.heat_resist.get()) 
                        && !player.isSpectator() 
                        && !player.isCreative()) {
                    applyTemperatureEffect(player, SDPotions.hyperthermia.get(), SDDamageSources.HYPERTHERMIA);
                    appliedEffect = true;
                }
            } else if (tempEnum == TemperatureEnum.FREEZING) {
                if (TemperatureEnum.FREEZING.getMiddle() >= getTemperatureLevel() 
                        && !player.hasEffect(SDPotions.cold_resist.get()) 
                        && !player.isSpectator() 
                        && !player.isCreative()) {
                    applyTemperatureEffect(player, SDPotions.hypothermia.get(), SDDamageSources.HYPOTHERMIA);
                    appliedEffect = true;
                }
            }
        }

        if (!appliedEffect) {
            if (this.getTemperatureDamageCounter() != 0) {
                boolean hasHypothermia = player.hasEffect(SDPotions.HYPOTHERMIA.get());
                boolean hasHyperthermia = player.hasEffect(SDPotions.HYPERTHERMIA.get());
                if (!hasHypothermia && !hasHyperthermia) {
                    this.setTemperatureDamageCounter(0);
                }
            }
        }

        int activeModifierCount = 0;

        Iterator<Map.Entry<String, TemporaryModifier>> iterator = temporaryModifiers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TemporaryModifier> entry = iterator.next();
            TemporaryModifier tm = entry.getValue();

            if (tm.duration > 0) {
                tm.duration--;
                activeModifierCount++;
            } else {
                iterator.remove();
            }
        }

        if (oldmodifiersize != activeModifierCount) {
            this.manualDirty = true;
        }

        oldmodifiersize = activeModifierCount;
    }

    private void debugRoutine(PlayerEntity player, World world) {
        DebugUtil.clientMessage(player, "----------------");
        BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

        float cumulative = 0;
        for (ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
            float modsum = 0;
            long nanotime = System.nanoTime();
            modsum += modifier.getWorldInfluence(world, pos);
            modsum += modifier.getPlayerInfluence(player);
            long nanotime2 = System.nanoTime();
            DebugUtil.clientMessage(player, "" + (nanotime2 - nanotime) + " : " + modifier.getName() + " - " + modsum);
            cumulative += modsum;
        }
        for (ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
            float oldcumulative = cumulative;
            long nanotime = System.nanoTime();
            cumulative = dynmodifier.applyDynamicWorldInfluence(world, pos, cumulative);
            cumulative = dynmodifier.applyDynamicPlayerInfluence(player, cumulative);
            long nanotime2 = System.nanoTime();
            DebugUtil.clientMessage(player, "" + (nanotime2 - nanotime) + " : " + dynmodifier.getName() + " - " + (cumulative - oldcumulative));
        }

        DebugUtil.clientMessage(player, "( " + TemperatureUtil.getPlayerTargetTemperature(player) + " )");
        DebugUtil.clientMessage(player, "TempTickLimit: " + getTemperatureTickLimit());
    }

    private int getTemperatureTickLimit() {
        int tickrange = ModConfig.SERVER.temperatureTickMax.get() - ModConfig.SERVER.temperatureTickMin.get();
        int temprange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound();
        int currentrange = Math.abs(getTemperatureLevel() - targettemp);
        boolean escapingDanger = getTemperatureLevel() <= targettemp 
                ? getTemperatureEnum() == TemperatureEnum.FREEZING 
                : getTemperatureEnum() == TemperatureEnum.BURNING;

        return Math.max(ModConfig.SERVER.temperatureTickMin.get(), 
                ModConfig.SERVER.temperatureTickMax.get() 
                        - ((currentrange * tickrange) / temprange) 
                        - (escapingDanger ? ModConfig.SERVER.temperatureTickDangerBoost.get() : 0));
    }

    private void applyTemperatureEffect(PlayerEntity player, Effect potionIn, DamageSource damageSource) {
        int amplifier = 0;
        float existingModifier = 0.0F;

        ModifiableAttributeInstance attributeInstance = player.getAttribute(net.minecraft.entity.ai.attributes.Attributes.MAX_HEALTH);

        if (attributeInstance != null) {
            for (AttributeModifier modifier : attributeInstance.getModifiers(net.minecraft.entity.ai.attributes.AttributeModifier.Operation.ADDITION)) {
                if (!modifier.getName().equals("Health Gained from Trying New Foods")) {
                    existingModifier += (float) modifier.getAmount();
                }
            }
        }

        EffectInstance activeEffect = player.getEffect(potionIn);
        if (activeEffect != null) {
            int activeAmplifier = activeEffect.getAmplifier();

            if (player.getMaxHealth() - (existingModifier + activeAmplifier * 2.0F) - 2.0F > 2.0F
                    && attributeInstance != null
                    && attributeInstance.getModifiers(net.minecraft.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_BASE).size() == 0
                    && attributeInstance.getModifiers(net.minecraft.entity.ai.attributes.AttributeModifier.Operation.MULTIPLY_TOTAL).size() == 0) {
                amplifier = activeAmplifier + 1;
            } else {
                amplifier = activeAmplifier;
            }
        }

        player.removeEffect(potionIn);
        player.addEffect(new EffectInstance(potionIn, ModConfig.SERVER.temperatureDamageDuration.get(), amplifier));
    }

    @Override
    public boolean isDirty() {
        return manualDirty || this.temperature != this.oldtemperature;
    }

    @Override
    public void setClean() {
        this.oldtemperature = this.temperature;
        this.manualDirty = false;
    }

    @Override
    public TemperatureEnum getTemperatureEnum() {
        return TemperatureUtil.getTemperatureEnum(getTemperatureLevel());
    }

    @Override
    public ImmutableMap<String, TemporaryModifier> getTemporaryModifiers() {
        return ImmutableMap.copyOf(temporaryModifiers);
    }

    @Override
    public void setTemporaryModifier(String name, float temp, int duration) {
        if (temp == 0.0f || !Float.isFinite(temp))
            return;

        if (this.temporaryModifiers.containsKey(name)) {
            this.manualDirty = true;
        }
        this.temporaryModifiers.put(name, new TemporaryModifier(temp, duration));
    }

    @Override
    public void clearTemporaryModifiers() {
        this.temporaryModifiers.clear();
    }

    @Override
    public int getPacketTimer() {
        return packetTimer;
    }
}