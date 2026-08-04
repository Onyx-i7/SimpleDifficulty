package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import net.minecraftforge.fml.ModList;

/**
 * Utility class for mod compatibility checks.
 */
public class CompatUtil {

    /**
     * Checks if a mod can be used (is loaded and not disabled).
     *
     * @param modid The mod ID to check.
     * @return true if the mod is loaded and not disabled.
     */
    public static boolean canUseMod(String modid) {
        return ModList.get().isLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid);
    }
}