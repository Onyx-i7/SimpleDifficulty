package com.charles445.simpledifficulty.client.particle;

import net.minecraft.world.World;

public class ParticleHeater extends ParticleTemperature {

    public ParticleHeater(World world, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ) {
        super(world, xPos, yPos, zPos, motionX, motionY, motionZ);
        
        // Optimized: Use the internal inherited random instance for particle initialization consistency
        this.particleAge = this.rand.nextInt(2);
    }

    @Override
    int getFrameMax() {
        return 8;
    }

    @Override
    int getParticleTextureY() {
        return 0;
    }
}
