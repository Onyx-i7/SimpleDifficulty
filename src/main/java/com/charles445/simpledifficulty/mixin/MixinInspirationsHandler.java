package com.charles445.simpledifficulty.mixin;

import com.charles445.simpledifficulty.compat.ModNames;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = com.charles445.simpledifficulty.compat.mod.InspirationsHandler.class, remap = false)
public class MixinInspirationsHandler {

    @Inject(method = "<init>", at = @At("HEAD"), cancellable = true)
    private void onInit(CallbackInfo ci) {
        if (!Loader.isModLoaded(ModNames.INSPIRATIONS)) {
            ci.cancel();
        }
    }
}
