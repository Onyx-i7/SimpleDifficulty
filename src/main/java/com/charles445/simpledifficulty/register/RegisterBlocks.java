package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.block.*;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegisterBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SimpleDifficulty.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SimpleDifficulty.MODID);

    public static final RegistryObject<Block> CAMPFIRE = BLOCKS.register("campfire",
            () -> new BlockCampfire(AbstractBlock.Properties.of(Material.WOOD).strength(2.0f).sound(SoundType.WOOD).lightLevel((state) -> state.getValue(BlockCampfire.BURNING) ? 15 : 0)));

    public static final RegistryObject<Block> RAIN_COLLECTOR = BLOCKS.register("rain_collector",
            () -> new BlockRainCollector(AbstractBlock.Properties.of(Material.METAL).strength(2.0f).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Block> HEATER = BLOCKS.register("heater", 
        () -> new BlockTemperature(1.0f, AbstractBlock.Properties.of(Material.METAL).strength(0.5f).sound(SoundType.METAL).lightLevel(state -> state.getValue(BlockTemperature.ENABLED) ? 7 : 0)));

    public static final RegistryObject<Block> CHILLER = BLOCKS.register("chiller", 
        () -> new BlockTemperature(-1.0f, AbstractBlock.Properties.of(Material.METAL).strength(0.5f).sound(SoundType.METAL).lightLevel(state -> state.getValue(BlockTemperature.ENABLED) ? 7 : 0)));

    public static final RegistryObject<Block> SPIT = BLOCKS.register("spit",
            () -> new BlockSpit(AbstractBlock.Properties.of(Material.WOOD).strength(0.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> ICE_PURIFIED_WATER = BLOCKS.register("purifiedwater_ice",
            () -> new BlockIceBasic(AbstractBlock.Properties.of(Material.ICE).strength(0.5f).friction(0.98f).sound(SoundType.GLASS), "purifiedwater"));

    public static final RegistryObject<Block> ICE_SALT_WATER = BLOCKS.register("saltwater_ice",
            () -> new BlockIceBasic(AbstractBlock.Properties.of(Material.ICE).strength(0.5f).friction(0.98f).sound(SoundType.GLASS), "saltwater"));

    // Tile Entities
    public static final RegistryObject<TileEntityType<TileEntitySpit>> SPIT_TILE_ENTITY = TILE_ENTITIES.register("campfirespit",
            () -> TileEntityType.Builder.of(TileEntitySpit::new, SPIT.get()).build(null));

    public static final RegistryObject<TileEntityType<TileEntityTemperature>> TEMPERATURE_TILE_ENTITY = TILE_ENTITIES.register("temperaturechanged",
            () -> TileEntityType.Builder.of(TileEntityTemperature::new, HEATER.get(), CHILLER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        TILE_ENTITIES.register(eventBus);
    }
}