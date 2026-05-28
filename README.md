# SimpleDifficulty

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-green.svg)](https://minecraft.net)
[![Forge Version](https://img.shields.io/badge/Forge-14.23.5.2847-red.svg)](https://files.minecraftforge.net/)
[![Version](https://img.shields.io/badge/Version-0.7.1-orange.svg)](https://github.com/onyx-i7/SimpleDifficulty/releases)

This is a fork of SimpleDifficulty for Underdog. You can find the SimpleDifficulty for Underdog at this link: https://github.com/juraj-hrivnak/SimpleDifficulty

## Overview

The SimpleDifficulty mod adds a **temperature and thirst system to Minecraft**. This system is like the one in the Tough as Nails mod. This version of SimpleDifficulty is still being worked on. Has some new features. It also works better with mods and runs more smoothly.

> Note: Some of the pictures in the game may not be final. We want you to test the game and give us your thoughts. If you have ideas for the API we would love to hear them.

---

## Features

### Core Mechanics by [juraj-hrivnak](https://github.com/juraj-hrivnak)
- The thirst system forces players to drink water to survive
- The temperature system affects players' mood and health
- There are different types of water:
  * Regular water is like the water in the standard Minecraft game
  * Saltwater increases players' thirst
  * Purified water is clean and safe to drink

### Improved Water System by [juraj-hrivnak](https://github.com/juraj-hrivnak)
- Saltwater and normal water have been added
- The appearance of water varies depending on the different areas of the game
- Players can adjust the brightness or darkness of the water
- The fog over the water looks nicer and varies in each area
- The game manages the mix of saltwater and freshwater more intelligently
- Ice blocks have been added that players can melt or freeze
- The game can generate ice formations in the world
- The game is compatible with the Serene Seasons mod for ice
- It is also compatible with the Dynamic Trees mod for water blocks
- It is compatible with the Fluidlogged API

---

## Performance Optimizations
This version of SimpleDifficulty has some big improvements:

### Memory Leak Fixes
- An issue that was consuming a lot of memory has been resolved
- I've optimized the temperature system so it uses less memory

### Lag Reduction
- The game has been optimized to run faster when players drink from a canteen
- The game has been optimized to run faster when searching for water blocks
- The game has been optimized to run faster when calculating thirst
- Some debug code that was slowing down the game has been removed

### General Improvements
- The load on the garbage collector has been reduced
- The game now exits before certain loops to save time
- Some data has been stored in a way that makes it easier to access

---

## Compatibility

This mod works with these mods:

### Supported
- Animania
- Armor Underwear
- Biomes O' Plenty
- DynamicSurroundings
- EnhancedVisuals
- First Aid
- Harvest Festival Legacy
- Lycanites Mobs
- Ore Excavation
- Pams HarvestCraft
- Potion Core
- Pyrotech
- Realistic Torches
- Rustic
- Serene Seasons
- Simple Camp Fire
- Tinkers Construct
- Streams
- Greenery
- Dynamic Trees

### Additional Compatibility
- Cave Generator
- Fluidlogged API
- Baubles
- Inspirations

> If you want it to be compatible with a mod, let me know. I'll try to make it work.

---

## Configuration
Players can change things in the game:
- How fast players get thirsty
- What players can drink
- How temperature affects players
- How mods work together
- How water behaves

---

## Installation
1. Get Minecraft 1.12.2 and Forge 14.23.5.2847 or higher
2. Download the SimpleDifficulty jar file
3. Put the jar in your mods folder
4. Start the game. Have fun

---

## API Usage

SimpleDifficulty has an API that other mods can use:
```java
// Get the players thirst data
IThirstCapability thirst = SDCapabilities.getThirstData(player);

// Make the player drink
ThirstUtil.takeDrink(player, thirstAmount, saturation, dirtyChance);

// Find a water source
ThirstEnumBlockPos waterSource = ThirstUtil.traceWater(player);

```

See the api package for information.

---

## Building From Source
```bash
git clone https://github.com/Onyx-i7/SimpleDifficulty.git
cd SimpleDifficulty
./gradlew build
```

The built jars will be in the build/libs folder.

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

---

## Credits

### Authors
- Charles445 created the original version of SimpleDifficulty
- juraj-hrivnak created the Underdog fork
- I created this fork and improved its performance

### Contributors (Original version of SimpleDifficulty)
- CreativeMD and fonnymunkey contributed to EnhancedVisuals
- ichttt contributed to First Aid

---

## Support

- Report bugs and request new features in the GitHub issue tracker
- Send us your feedback and suggestions
- Ask about mod compatibility
