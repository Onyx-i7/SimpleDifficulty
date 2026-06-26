package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Method;

public class CompatUtil {
    private static final Class<?> EVENT_BUS_CLASS;
    private static final Method REGISTER_METHOD;
    
    static {
        Class<?> eventBusClass = null;
        Method registerMethod = null;
        
        try {
            eventBusClass = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
            registerMethod = eventBusClass.getDeclaredMethod("register", 
                Class.class, Object.class, Method.class, ModContainer.class);
            registerMethod.setAccessible(true);
        } catch (Exception e) {
            SimpleDifficulty.logger.error("Failed to initialize CompatUtil reflection", e);
        }
        
        EVENT_BUS_CLASS = eventBusClass;
        REGISTER_METHOD = registerMethod;
    }
    
    public static boolean canUseMod(String modid) {
        return Loader.isModLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid);
    }
    
    /**
     * Manual EVENT_BUS registering
     * Don't forget to add the SubscribeEvent annotation
     * But do not register the whole class to the event bus!
     *
     * @param clazz (Event) Event Class
     * @param thiz (this) Event Handler Object
     * @param thiz_toCall (this.onEvent(Object event)) Method to be invoked
     * @return true if registration succeeded, false otherwise
     */
    public static boolean subscribeEventManually(Class<?> clazz, Object thiz, Method thiz_toCall) {
        // Validation
        if (clazz == null || thiz == null || thiz_toCall == null) {
            SimpleDifficulty.logger.error("subscribeEventManually: Parameters cannot be null");
            return false;
        }
        
        if (REGISTER_METHOD == null) {
            SimpleDifficulty.logger.error("CompatUtil reflection initialization failed");
            return false;
        }
        
        if (!thiz_toCall.isAnnotationPresent(SubscribeEvent.class)) {
            SimpleDifficulty.logger.error("Method needs a SubscribeEvent annotation, go add one.");
            return false;
        }
        
        try {
            REGISTER_METHOD.invoke(MinecraftForge.EVENT_BUS, clazz, thiz, thiz_toCall, 
                Loader.instance().getMinecraftModContainer());
            return true;
        } catch (Exception e) {
            SimpleDifficulty.logger.error("subscribeEventManually error.", e);
            return false;
        }
    }
}
