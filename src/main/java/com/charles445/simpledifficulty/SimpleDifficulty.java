package com.charles445.simpledifficulty;

import com.charles445.simpledifficulty.command.CommandSimpleDifficulty;
import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.proxy.IProxy;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod
(
	modid = SimpleDifficulty.MODID, 
	name = SimpleDifficulty.NAME, 
	version = SimpleDifficulty.VERSION,
	acceptedMinecraftVersions = "[1.12, 1.13)",
	dependencies = "required-after:forge@[14.23.5.2847,);",
	updateJSON = "https://raw.githubusercontent.com/juraj-hrivnak/SimpleDifficulty/master/modupdatechecker.json"
)
public class SimpleDifficulty 
{
	public static final String MODID = "simpledifficulty";
	public static final String NAME = "SimpleDifficulty";
	public static final String VERSION = "0.8.5";
	
	@Mod.Instance(SimpleDifficulty.MODID)
	public static SimpleDifficulty instance;
	
	public static Logger logger = LogManager.getLogger("SimpleDifficulty");
	
	@SidedProxy(clientSide = "com.charles445.simpledifficulty.proxy.ClientProxy",
				serverSide = "com.charles445.simpledifficulty.proxy.ServerProxy")
	public static IProxy proxy;
	
	public static File jsonDirectory;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		jsonDirectory = new File(event.getModConfigurationDirectory(), SimpleDifficulty.MODID);
		
		PacketHandler.init();
		proxy.preInit();
		
		com.charles445.simpledifficulty.compat.mod.Weather2Compat.init();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit();
	}
	
	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		DebugVerifier verifier = new DebugVerifier();
		verifier.verify();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandSimpleDifficulty());
	}
	
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
}
