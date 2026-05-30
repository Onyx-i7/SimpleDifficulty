package com.charles445.simpledifficulty.api.thirst;

public enum ThirstEnum
{
	NORMAL	("normal",		6,	0.05f, 	0.00f),
	SALT	("salt",		-1,	-0.3f, 	1.00f),
	RAIN	("rain",		1,	0.05f,	0.0f),
	POTION	("potion",		2,	0.2f,	0.0f),
	PURIFIED("purified",	3,	0.3f,	0.75f),
	CLEAN	("clean",		4,	0.4f,	0.0f);
	
	private final String name;
	private final int thirst;
	private final float saturation;
	private final float dirty;

	ThirstEnum(String name, int thirst, float saturation, float dirty)
	{
		this.name = name;
		this.thirst=thirst;
		this.saturation=saturation;
		this.dirty=dirty;
	}

	public String getName()
	{
		return this.name;
	}
	
	public int getThirst()
	{
		return thirst;
	}
	
	public float getSaturation()
	{
		return saturation;
	}
	
	public float getThirstyChance()
	{
		return dirty;
	}

	public String toString()
	{
		return this.getName();
	}
	
}
