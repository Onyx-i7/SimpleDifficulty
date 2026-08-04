package com.charles445.simpledifficulty.client.particle;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.world.ClientWorld;

public class ParticleHeater extends ParticleTemperature {

    public ParticleHeater(ClientWorld world, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ) {
        super(world, xPos, yPos, zPos, motionX, motionY, motionZ);
        this.age = this.random.nextInt(2);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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