package com.charles445.simpledifficulty.api.config;

public enum ServerOptions implements IConfigOption
{
	/*Boolean*/	DEBUG			("debug"),
	/*Boolean*/	THIRST_ENABLED	("thirstEnabled"),
	/*Boolean*/ THIRST_DRINK_BLOCKS ("thirstDrinkBlocks"),
	/*Boolean*/ THIRST_DRINK_RAIN ("thirstDrinkRain"),
	/*Boolean*/ PEACEFUL_DANGER ("peacefulDanger"),
	/*Boolean*/ TEMPERATURE_ENABLED ("temperatureEnabled"),
	/*Boolean*/ TEMPERATURE_TE_ENABLED ("temperatureTEEnabled"),
	/*Integer*/ CANTEEN_DOSES ("canteenDoses"),
	/*Boolean*/ STRICT_HEATERS ("strictHeaters"),
	/*Integer*/ IRON_CANTEEN_DOSES ("ironCanteenDoses"),
	/*Boolean*/ INFINITE_PURIFIED_WATER ("infinitePurifiedWater"),
	/*Boolean*/ PURIFIED_WATER_OPACITY ("purifiedWaterOpacity"),
	/*Double*/  THIRST_EXHAUSTION_MULTIPLIER ("thirstExhaustionMultiplier");
	
	
	String name;
	
	private ServerOptions(String name)
	{
		this.name=name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return this.getName();
	}
}
