package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.compat.ModNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Modificador de temperatura para trajes Hazmat de HBM NTM CE
 * Añade calor corporal cuando el jugador lleva un traje hazmat
 */
public class HBMTemperatureModifier implements ITemperatureModifier {

    @Override
    public float getPlayerInfluence(EntityPlayer player) {
        if (!HBMNTMHandler.isHBMLoaded()) return 0.0f;
        return HBMNTMHandler.getHazmatTemperatureModifier(player);
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        // Este modificador no afecta al mundo, solo al jugador
        return 0.0f;
    }

    @Override
    public String getName() {
        return ModNames.HBMNTM + ":hazmat_suit";
    }
}
