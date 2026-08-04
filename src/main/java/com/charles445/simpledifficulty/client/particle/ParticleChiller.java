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

    @Override
    protected float getU0() { return 0; }
    @Override
    protected float getU1() { return 1; }
    @Override
    protected float getV0() { return 0; }
    @Override
    protected float getV1() { return 1; }
}