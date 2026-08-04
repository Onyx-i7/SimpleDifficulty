package com.charles445.simpledifficulty.client.particle;

import net.minecraft.client.world.ClientWorld;

/**
 * Particle for chiller blocks.
 */
public class ParticleChiller extends ParticleTemperature {

    public ParticleChiller(ClientWorld world, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ) {
        super(world, xPos, yPos, zPos, motionX, motionY, motionZ);
        this.age = this.random.nextInt(2);
    }
}