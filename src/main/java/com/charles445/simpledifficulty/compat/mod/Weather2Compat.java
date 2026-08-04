package com.charles445.simpledifficulty.compat.mod;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Stub for Weather2 compatibility.
 * TODO: Implement full compatibility with Weather2 Remastered
 */
public class Weather2Compat {
    
    public static void init() {
        // Stub
    }
    
    public static boolean isRainingAt(World world, BlockPos pos) {
        // Fallback to vanilla weather check
        return world.isRainingAt(pos);
    }
}