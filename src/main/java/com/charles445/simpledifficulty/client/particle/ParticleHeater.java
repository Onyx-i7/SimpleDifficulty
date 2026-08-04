package com.charles445.simpledifficulty.client.particle;

import net.minecraft.client.world.ClientWorld;

/**
 * Particle for heater blocks.
 */
public class ParticleHeater extends ParticleTemperature {

    public ParticleHeater(ClientWorld world, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ) {
        super(world, xPos, yPos, zPos, motionX, motionY, motionZ);
        this.age = this.random.nextInt(2);
    }
}