package com.charles445.simpledifficulty;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.capability.TemperatureCapability;
import com.charles445.simpledifficulty.capability.TemperatureStorage;
import com.charles445.simpledifficulty.capability.ThirstCapability;
import com.charles445.simpledifficulty.capability.ThirstStorage;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.register.RegisterBlocks;
import com.charles445.simpledifficulty.register.RegisterFluids;
import com.charles445.simpledifficulty.register.RegisterItems;
import com.charles445.simpledifficulty.register.RegisterPotions;
import com.charles445.simpledifficulty.register.RegisterEnchantments;
import com.charles445.simpledifficulty.temperature.*;
import com.charles445.simpledifficulty.util.internal.TemperatureUtilInternal;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(SimpleDifficulty.MODID)
public class SimpleDifficulty {
    public static final String MODID = "simpledifficulty";
    public static final String NAME = "SimpleDifficulty";
    public static final String VERSION = "0.8.0";

    public static final Logger LOGGER = LogManager.getLogger(NAME);
    
    public static File jsonDirectory;

    public SimpleDifficulty() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register Deferred Registers
        RegisterBlocks.register(modEventBus);
        RegisterItems.register(modEventBus);
        RegisterFluids.register(modEventBus);
        RegisterPotions.register(modEventBus);
        RegisterEnchantments.register(modEventBus);

        // Register Configurations
        ModConfig.register();
        
        // Register mod lifecycle listeners
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("SimpleDifficulty Common Setup");
        
        // Setup configuration directory using modern FMLPaths
        jsonDirectory = new File(FMLPaths.CONFIGDIR.get().toFile(), SimpleDifficulty.MODID);
        if (!jsonDirectory.exists()) {
            jsonDirectory.mkdirs();
        }

        // Setup internal utilities
        TemperatureUtil.internal = new TemperatureUtilInternal();
        ThirstUtil.internal = new ThirstUtilInternal();

        // Register Capabilities
        CapabilityManager.INSTANCE.register(ITemperatureCapability.class, new TemperatureStorage(), TemperatureCapability::new);
        CapabilityManager.INSTANCE.register(IThirstCapability.class, new ThirstStorage(), ThirstCapability::new);

        // Register Temperature Modifiers
        TemperatureRegistry.registerModifier(new ModifierDefault());
        TemperatureRegistry.registerModifier(new ModifierAltitude());
        TemperatureRegistry.registerModifier(new ModifierArmor());
        TemperatureRegistry.registerModifier(new ModifierBiome());
        TemperatureRegistry.registerModifier(new ModifierBlocksTiles());
        TemperatureRegistry.registerModifier(new ModifierDimension());
        TemperatureRegistry.registerModifier(new ModifierHeldItems());
        TemperatureRegistry.registerModifier(new ModifierSnow());
        TemperatureRegistry.registerModifier(new ModifierSprint());
        TemperatureRegistry.registerModifier(new ModifierTemporary());
        TemperatureRegistry.registerModifier(new ModifierTime());
        TemperatureRegistry.registerModifier(new ModifierWet());

        // Network Packets
        PacketHandler.init();
        
        // Mod Configuration setup
        ModConfig.sendLocalServerConfigToAPI();
        ModConfig.sendLocalClientConfigToAPI();
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        DebugVerifier verifier = new DebugVerifier();
        verifier.verify();
    }
}