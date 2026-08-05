package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.config.json.JsonPropertyTemperature;
import com.charles445.simpledifficulty.api.temperature.ITemperatureTileEntity;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;

import java.util.List;
import java.util.Map;

public class ModifierBlocksTiles extends ModifierBase {
    private float coldestValue = 0.0f;
    private float hottestValue = 0.0f;
    private float hotTotal = 0.0f;
    private float coldTotal = 0.0f;

    public ModifierBlocksTiles() {
        super("BlocksTiles");
    }

    @Override
    public float getWorldInfluence(World world, BlockPos pos) {
        resetHeat();
        doBlocksRoutine(world, pos);

        if (ModConfig.SERVER.blocksTilesSeparate.get()) {
            float result = consolidateHeat();
            resetHeat();
            doTileEntitiesRoutine(world, pos);
            return result + consolidateHeat();
        } else {
            doTileEntitiesRoutine(world, pos);
            return consolidateHeat();
        }
    }

    private void resetHeat() {
        coldestValue = 0.0f;
        hottestValue = 0.0f;
        hotTotal = 0.0f;
        coldTotal = 0.0f;
    }

    private float consolidateHeat() {
        if (!ModConfig.SERVER.stackingTemperature.get()) {
            return hottestValue + coldestValue;
        }

        hotTotal -= hottestValue;
        coldTotal -= coldestValue;

        float hotLogValue = hottestValue * (float) Math.sqrt(easyLog(hotTotal));
        float coldLogValue = coldestValue * (float) Math.sqrt(easyLog(coldTotal));

        float result = hotLogValue + coldLogValue;

        if (result > hottestValue) {
            return Math.min(hottestValue + ModConfig.SERVER.stackingTemperatureLimit.get().floatValue(), result);
        } else if (result < coldestValue) {
            return Math.max(coldestValue - ModConfig.SERVER.stackingTemperatureLimit.get().floatValue(), result);
        } else {
            return result;
        }
    }

    private void doBlocksRoutine(World world, BlockPos pos) {
        for (int x = -4; x <= 4; x++) {
            for (int y = -3; y <= 1; y++) {
                for (int z = -4; z <= 4; z++) {
                    final BlockPos blockpos = pos.offset(x, y, z);
                    
                    if (!world.isLoaded(blockpos)) continue;
                    
                    final BlockState blockstate = world.getBlockState(blockpos);
                    final Block block = blockstate.getBlock();

                    if (block.getRegistryName() == null) continue;

                    List<JsonPropertyTemperature> tempInfoList = JsonConfig.blockTemperatures.get(block.getRegistryName().toString());

                    if (tempInfoList != null) {
                        for (JsonPropertyTemperature tempInfo : tempInfoList) {
                            if (tempInfo == null)
                                continue;

                            float blockTemp = tempInfo.temperature;

                            if (blockTemp == 0.0f)
                                continue;

                            if (tempInfo.matchesState(blockstate)) {
                                processTemp(blockTemp);
                                break;
                            }
                        }
                    } else {
                        if (blockstate.getMaterial() == Material.FIRE) {
                            processTemp(JsonConfigInternal.materialTemperature.fire);
                        }
                    }
                }
            }
        }
    }

    private void doTileEntitiesRoutine(World world, BlockPos pos) {
        if (!ServerConfig.instance.getBoolean(ServerOptions.TEMPERATURE_TE_ENABLED))
            return;

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                checkChunkAndProcess(world, pos.offset(x * 16, 0, z * 16), pos);
            }
        }
    }

    private void processTemp(float blockTemp) {
        if (blockTemp == 0.0f)
            return;

        if (blockTemp >= 0.0f)
            processHot(blockTemp);
        else
            processCold(blockTemp);
    }

    private void processHot(float blockTemp) {
        hotTotal += blockTemp;
        if (blockTemp > hottestValue) {
            hottestValue = blockTemp;
        }
    }

    private void processCold(float blockTemp) {
        coldTotal += blockTemp;
        if (blockTemp < coldestValue) {
            coldestValue = blockTemp;
        }
    }

    private void checkChunkAndProcess(World world, BlockPos pos, BlockPos selfPos) {
        if (WorldUtil.isChunkLoaded(world, pos)) {
            net.minecraft.world.chunk.IChunk chunk = world.getChunk(pos);
            for (Map.Entry<BlockPos, TileEntity> entry : chunk.getTileEntitiesMap().entrySet()) {
                processTemp(checkTileEntity(world, entry.getKey(), entry.getValue(), selfPos));
            }
        }
    }

    private float checkTileEntity(World world, BlockPos pos, TileEntity tileEntity, BlockPos selfPos) {
        double distance = pos.distSqr(selfPos);

        if (distance < 2500.0d) {
            if (tileEntity instanceof ITemperatureTileEntity) {
                return ((ITemperatureTileEntity) tileEntity).getInfluence(selfPos, distance);
            }
        }
        return 0.0f;
    }

    private float easyLog(float f) {
        if (f >= 0.0f) {
            return (float) Math.log10(f + 10.0f);
        } else {
            return (float) Math.log10(-1.0f * f + 10.0f);
        }
    }
}