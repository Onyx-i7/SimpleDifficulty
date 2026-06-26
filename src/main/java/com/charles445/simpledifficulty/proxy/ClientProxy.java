package com.charles445.simpledifficulty.proxy;

import com.charles445.simpledifficulty.client.command.ClientCommandCopy;
import com.charles445.simpledifficulty.client.command.ClientCommandIdentityCopy;
import com.charles445.simpledifficulty.client.gui.TemperatureGui;
import com.charles445.simpledifficulty.client.gui.TemperatureInfoGui;
import com.charles445.simpledifficulty.client.gui.ThirstGui;
import com.charles445.simpledifficulty.client.particle.ParticleChiller;
import com.charles445.simpledifficulty.client.particle.ParticleHeater;
import com.charles445.simpledifficulty.client.render.RenderSpit;
import com.charles445.simpledifficulty.compat.CompatController;
import com.charles445.simpledifficulty.handler.TooltipHandler;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(new TemperatureGui());
        MinecraftForge.EVENT_BUS.register(new TemperatureInfoGui());
        MinecraftForge.EVENT_BUS.register(new ThirstGui());
        MinecraftForge.EVENT_BUS.register(new TooltipHandler());
    }
    
    @Override
    public void init() {
        super.init();
        
        // Register Client TE Renderers
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySpit.class, new RenderSpit());
    }
    
    @Override
    public void postInit() {
        super.postInit();
        
        // Setup Mod Compatibility
        CompatController.setupClient();
        
        // Commands
        ClientCommandHandler.instance.registerCommand(new ClientCommandCopy());
        ClientCommandHandler.instance.registerCommand(new ClientCommandIdentityCopy());
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public EntityPlayer getClientMinecraftPlayer() {
        // Try not to call this on logical server
        // Check world.isRemote if available
        return Minecraft.getMinecraft().player;
    }

    @Override
    public Boolean isClientConnectedToServer() {
        // Try not to call this on logical server
        // Check world.isRemote if available
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getConnection() == null || mc.getConnection().getNetworkManager() == null) {
            return false;
        }
        return mc.getConnection().getNetworkManager().isChannelOpen();
    }
    
    @Override
    public void spawnClientParticle(World world, String type, double xPos, double yPos, double zPos, 
                                    double motionX, double motionY, double motionZ) {
        if (!world.isRemote) {
            return;
        }
        
        Particle particle = null;
        
        switch (type) {
            case "HEATER":
                particle = new ParticleHeater(world, xPos, yPos, zPos, motionX, motionY, motionZ);
                break;
            case "CHILLER":
                particle = new ParticleChiller(world, xPos, yPos, zPos, motionX, motionY, motionZ);
                break;
            default:
                break;
        }
        
        if (particle != null) {
            Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        }
    }
}
