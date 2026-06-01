//Serene Seasons Compatibility
	@Override
	public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		if (world.rand.nextInt(Math.max(1, ModConfig.server.miscellaneous.rainCollectorFillChance)) == 0 && world.isRaining() && world.canSeeSky(pos.up()))
		{
			float f = world.getBiome(pos).getTemperature(pos);

			if (world.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F)
			{
				IBlockState iblockstate = world.getBlockState(pos);

				if (iblockstate.getValue(LEVEL) < 3)
				{
					world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 2);
				}
			}
		}
	}
