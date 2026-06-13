package com.charles445.simpledifficulty.debug;

/**
 * Basically, this was a temporary notepad file that the original author (charles445) 
 * used to test how other mod developers could disable built-in support via reflection, 
 * without having to compile against the API directly.
 */
public class DebugScratchpad {
    /*
    public static DebugScratchpad instance = new DebugScratchpad();
    
    public void init() {
        System.out.println(disableBuiltInModCompatibility("modid_goes_here"));
    }
    
    // preInit or init
    public boolean disableBuiltInModCompatibility(String modid) {
        try {
            Class<?> sdCompatibility = Class.forName("com.charles445.simpledifficulty.api.SDCompatibility");
            java.lang.reflect.Method disableBuiltInModCompatibility = sdCompatibility.getDeclaredMethod("disableBuiltInModCompatibility", String.class);
            disableBuiltInModCompatibility.invoke(null, modid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    */
}
