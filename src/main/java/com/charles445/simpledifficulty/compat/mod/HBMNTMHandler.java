package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.config.compat.ConfigServerCompatibility;
import com.charles445.simpledifficulty.compat.ModNames;
import com.hbm.capability.HbmLivingProps;
import com.hbm.config.RadiationConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Compatibilidad con HBM's Nuclear Tech Mod: Community Edition
 */
public class HBMNTMHandler {

    // Nombres de registro de trajes hazmat
    private static final String[] HAZMAT_PIECES = {
        "hazmat_helmet", "hazmat_plate", "hazmat_legs", "hazmat_boots",
        "hazmat_helmet_red", "hazmat_plate_red", "hazmat_legs_red", "hazmat_boots_red",
        "hazmat_helmet_grey", "hazmat_plate_grey", "hazmat_legs_grey", "hazmat_boots_grey",
        "hazmat_paa_helmet", "hazmat_paa_plate", "hazmat_paa_legs", "hazmat_paa_boots"
    };

    // Trajes PAA (Power Armor) - no penalizan temperatura si tienen energia
    private static final String[] PAA_PIECES = {
        "hazmat_paa_helmet", "hazmat_paa_plate", "hazmat_paa_legs", "hazmat_paa_boots"
    };

    private static boolean hbmLoaded = false;

    public static void init() {
        hbmLoaded = net.minecraftforge.fml.common.Loader.isModLoaded(ModNames.HBMNTM);
        if (hbmLoaded) {
            registerMachineTemperatures();
        }
    }

    public static boolean isHBMLoaded() {
        return hbmLoaded;
    }

    // ==========================================
    // 3. DESHIDRATACIÓN POR RADIACION
    // ==========================================

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side == Side.CLIENT) return;

        EntityPlayer player = event.player;
        if (player.isCreative() || player.isSpectator()) return;

        ConfigServerCompatibility.ConfigHBM cfg = ModConfig.server.compatibility.hbm;
        if (!cfg.enabled) return;

        // --- Radiación → Sed ---
        if (cfg.radiationThirst && RadiationConfig.enableContamination) {
            processRadiationThirst(player, cfg.radiationThirstMultiplier);
        }

        // --- Hazmat -> Temperatura
    }

    private void processRadiationThirst(EntityPlayer player, double multiplier) {
        double rads = HbmLivingProps.getRadiation(player);

        if (rads <= 0.0d) return;

        IThirstCapability thirstCap = SDCapabilities.getThirstData(player);
        if (thirstCap == null) return;

        // Escalado:
        // 0-100 rads:     sin efecto (exposicion minima)
        // 100-500 rads:   deshidratación leve (0.001 exhaustion/tick)
        // 500-2000 rads:  deshidratación moderada (0.005 exhaustion/tick)
        // 2000-10000:     deshidratación severa (0.015 exhaustion/tick)
        // 10000+:         deshidratación crítica (0.04 exhaustion/tick)
        float exhaustion = 0.0f;

        if (rads > 10000.0d) {
            exhaustion = 0.04f;
        } else if (rads > 2000.0d) {
            exhaustion = 0.015f;
        } else if (rads > 500.0d) {
            exhaustion = 0.005f;
        } else if (rads > 100.0d) {
            exhaustion = 0.001f;
        }

        if (exhaustion > 0.0f) {
            thirstCap.addThirstExhaustion(exhaustion * (float) multiplier);
        }
    }

    // ==========================================
    // 1. TRAJES HAZMAT -> TEMPERATURA
    // ==========================================

    /**
     * Calcula el modificador de temperatura por llevar trajes hazmat
     * Llamado desde HBMTemperatureModifier (un ITemperatureModifier)
     */
    public static float getHazmatTemperatureModifier(EntityPlayer player) {
        ConfigServerCompatibility.ConfigHBM cfg = ModConfig.server.compatibility.hbm;
        if (!cfg.enabled || !cfg.hazmatHeat) return 0.0f;

        int hazmatPieces = 0;
        int paaPieces = 0;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;

            ItemStack armorStack = player.getItemStackFromSlot(slot);
            if (armorStack.isEmpty()) continue;

            ResourceLocation regName = armorStack.getItem().getRegistryName();
            if (regName == null || !regName.getNamespace().equals(ModNames.HBMNTM)) continue;

            String itemName = regName.getPath();

            if (isHazmatPiece(itemName)) {
                hazmatPieces++;
                if (isPaaPiece(itemName)) {
                    paaPieces++;
                }
            }
        }

        if (hazmatPieces == 0) return 0.0f;

        // Los trajes PAA con energía no penalizan
        // Si el jugador lleva 4 piezas PAA, pensamos control climático activo
        if (paaPieces == 4) return 0.0f;

        // Cada pieza hazmat no-PAA añade calor corporal
        int effectivePieces = hazmatPieces - paaPieces;
        float heatPerPiece = (float) cfg.hazmatHeatPerPiece;

        return effectivePieces * heatPerPiece;
    }

    private static boolean isHazmatPiece(String itemName) {
        for (String piece : HAZMAT_PIECES) {
            if (piece.equals(itemName)) return true;
        }
        return false;
    }

    private static boolean isPaaPiece(String itemName) {
        for (String piece : PAA_PIECES) {
            if (piece.equals(itemName)) return true;
        }
        return false;
    }

    // ============================================
    // 2. MAQUINARIA -> TEMPERATURA AMBIENTAL
    // ============================================

    /**
     * Registra las temperaturas de bloques de HBM en el sistema JSON
     * Se llama una vez en init() si HBM está cargado
     */
    private static void registerMachineTemperatures() {
        ConfigServerCompatibility.ConfigHBM cfg = ModConfig.server.compatibility.hbm;;

        if (cfg.machineHeat) {
            // --- ZONAS DE CALOR EXTREMO ---

            // Reactores RBMK
            JsonConfig.registerBlockTemperature("hbm:rbmk_boiler", 15.0f);
            JsonConfig.registerBlockTemperature("hbm:rbmk_heater", 12.0f);
            JsonConfig.registerBlockTemperature("hbm:rbmk_debris_burning", 20.0f);
            JsonConfig.registerBlockTemperature("hbm:rbmk_debris_radiating", 10.0f);

            // PWR y otros reactores
            JsonConfig.registerBlockTemperature("hbm:machine_pwr_controller", 12.0f);
            JsonConfig.registerBlockTemperature("hbm:reactor_research", 8.0f);
            JsonConfig.registerBlockTemperature("hbm:reactor_zirnox", 10.0f);
            JsonConfig.registerBlockTemperature("hbm:watz", 14.0f);

            // Calderas y hornos industriales
            JsonConfig.registerBlockTemperature("hbm:machine_boiler", 8.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_heat_boiler", 10.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_heat_boiler_industrial", 14.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_blast_furnace", 12.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_arc_furnace_large", 10.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_di_furnace", 11.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_electric_furnace", 7.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_pyro_oven", 9.0f);

            // Generadores y turbinas (calor residual)
            JsonConfig.registerBlockTemperature("hbm:machine_generator", 4.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_diesel", 5.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_turbine", 3.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_large_turbine", 5.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_industrial_turbine", 7.0f);

            // Chimeneas industriales
            JsonConfig.registerBlockTemperature("hbm:machine_chimney_brick", 6.0f);
            JsonConfig.registerBlockTemperature("hbm:machine_chimney_industrial", 9.0f);

            // Fluidos peligrosos CALIENTES
            JsonConfig.registerBlockTemperature("hbm:corium_block", 25.0f);
            JsonConfig.registerBlockTemperature("hbm:volcanic_lava_block", 18.0f);
            JsonConfig.registerBlockTemperature("hbm:rad_lava_block", 20.0f);

            // Volcan
            JsonConfig.registerBlockTemperature("hbm:volcano_core", 20.0f);
            JsonConfig.registerBlockTemperature("hbm:volcano_rad_core", 22.0f);
        }

        if (cfg.cryoCold) {
            // --- ZONAS DE FRIO EXTREMO ---

            // Fluidos toxicos y ácidos (frios por evaporación química)
            JsonConfig.registerBlockTemperature("hbm:toxic_block", -5.0f);
            JsonConfig.registerBlockTemperature("hbm:acid_block", -4.0f);
            JsonConfig.registerBlockTemperature("hbm:sulfuric_acid_block", -6.0f);
            JsonConfig.registerBlockTemperature("hbm:schrabidic_block", -8.0f);

            // Gases (frios por expansion)
            JsonConfig.registerBlockTemperature("hbm:gas_radon", -3.0f);
            JsonConfig.registerBlockTemperature("hbm:gas_radon_dense", -5.0f);
            JsonConfig.registerBlockTemperature("hbm:gas_meltdown", -7.0f);

            // Decon (descontaminación - usa refrigeracion)
            JsonConfig.registerBlockTemperature("hbm:decon", -3.0f);
        }
    }
}
