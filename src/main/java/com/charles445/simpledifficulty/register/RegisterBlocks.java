package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.*;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SimpleDifficulty.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SimpleDifficulty.MODID);

    public static final RegistryObject<Block> CAMPFIRE = BLOCKS.register("campfire", 
        () -> new BlockCampfire(AbstractBlock.Properties.create(Material.WOOD)));
    public static final RegistryObject<Block> RAIN_COLLECTOR = BLOCKS.register("rain_collector", BlockRainCollector::new);
    public static final RegistryObject<Block> HEATER = BLOCKS.register("heater", () -> new BlockTemperature(1.0f));
    public static final RegistryObject<Block> CHILLER = BLOCKS.register("chiller", () -> new BlockTemperature(-1.0f));
    public static final RegistryObject<Block> SPIT = BLOCKS.register("spit", BlockSpit::new);
    public static final RegistryObject<Block> ICE_PURIFIED_WATER = BLOCKS.register("purifiedwater_ice", () -> new BlockIceBasic("purifiedwater"));
    public static final RegistryObject<Block> ICE_SALT_WATER = BLOCKS.register("saltwater_ice", () -> new BlockIceBasic("saltwater"));

    // Tile Entities
    public static final RegistryObject<TileEntityType<TileEntitySpit>> SPIT_TILE_ENTITY = TILE_ENTITIES.register("campfirespit", 
            () -> TileEntityType.Builder.create(TileEntitySpit::new, SPIT.get()).build(null)
    
    public static final RegistryObject<TileEntityType<TileEntityTemperature>> TEMPERATURE_TILE_ENTITY = TILE_ENTITIES.register("temperaturechanged", 
            () -> TileEntityType.Builder.create(TileEntityTemperature::new, HEATER.get(), CHILLER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
    }
}