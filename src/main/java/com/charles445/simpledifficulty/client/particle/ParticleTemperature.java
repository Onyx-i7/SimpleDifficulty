package com.charles445.simpledifficulty.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;

/**
 * Abstract base class for temperature-related particles (heater/chiller)
 */
public abstract class ParticleTemperature extends net.minecraft.client.particle.TexturedParticle {

    public ParticleTemperature(ClientWorld world, double xPos, double yPos, double zPos, double motionInX, double motionInY, double motionInZ) {
        super(world, xPos, yPos, zPos);

        this.xd = this.xd * 0.01D + motionInX;
        this.yd = this.yd * 0.01D + motionInY;
        this.zd = this.zd * 0.01D + motionInZ;
        this.xd += (this.random.nextDouble() * 0.02D) - 0.01D;
        this.yd += (this.random.nextDouble() * 0.02D) - 0.01D;
        this.zd += (this.random.nextDouble() * 0.02D) - 0.01D;

        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.lifetime = (int) (8.0D / (this.random.nextDouble() * 0.8D + 0.2D)) + 4;
        this.gravity = 0.015F;
        this.quadSize = 0.5F;
    }

    @Override
    public void tick() {
        super.tick();
        // Frame animation logic would go here if using animated sprites
    }

    @Override
    public int getLightColor(float partialTicks) {
        return 15728880; // Full brightness
    }
}