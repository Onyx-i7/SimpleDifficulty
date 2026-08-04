package com.charles445.simpledifficulty.mixin;

import com.charles445.simpledifficulty.compat.ModNames;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to prevent InspirationsHandler initialization when Inspirations mod is not loaded.
 * This avoids ClassNotFoundException crashes on servers without the Inspirations mod.
 * 
@Mixin(value = com.charles445.simpledifficulty.compat.mod.InspirationsHandler.class, remap = false)
public class MixinInspirationsHandler {

     * Intercepts the constructor and cancels it if Inspirations is not loaded.
     *
     * @param ci The callback info.
    @Inject(method = "<init>", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        if (!ModList.get().isLoaded(ModNames.INSPIRATIONS)) {
            ci.cancel();
        }
    }
}
*/