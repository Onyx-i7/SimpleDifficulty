# SimpleDifficulty

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.12.2-green.svg)](https://minecraft.net)
[![Forge Version](https://img.shields.io/badge/Forge-14.23.5.2838-red.svg)](https://files.minecraftforge.net/)
[![Version](https://img.shields.io/badge/Version-0.7.7-orange.svg)](https://github.com/onyx-i7/SimpleDifficulty/releases)

This is a performance-focused fork of SimpleDifficulty for Underdog. You can find the original Underdog fork here: https://github.com/juraj-hrivnak/SimpleDifficulty

## Overview

The SimpleDifficulty mod introduces a **temperature and thirst system to Minecraft 1.12.2**, building upon the core mechanics of legacy survival mods. This maintained fork focuses on bug fixes, expanding mod compatibility, and applying technical optimizations to ensure smooth gameplay in large modpacks.

> **Note**: Feedback and technical suggestions are always welcome. If you have feature requests or ideas for the API, please open an issue in the repository tracking system.

---

## Features

### Core Mechanics
* **Thirst System**: Requires players to manage hydration levels to avoid negative status effects.
* **Temperature Dynamics**: Environmental biomes, blocks, and armor affect the player's temperature.
* **Water Variants**: 
  * *Regular Water*: Standard hydration source.
  * *Saltwater*: Increases dehydration rates if consumed.
  * *Purified Water*: Safe, clean hydration with optimal saturation values.

### Refined Water & Item Systems
* **Canteen Overhaul**: Corrected durability logic and thirst replenishment scaling. Purified water consumption accurately updates items and sources.
* **Bottle Interactions**: Empty bottles dynamically detect specific environmental fluid types (Salt, Normal, or Spring water) rather than defaulting exclusively to vanilla water blocks.
* **Consistent Smelting**: Corrected cooking logic to prevent special fluid containers (like spring water bottles) from downgrading back to standard variants during processing.
* **Recipe Sanitization**: Removed redundant or broken shapeless recipes for canteens and filters to prevent crafting conflicts.
* **Visual Adjustments**: Fluid color rendering adapts organically based on biome locations, featuring customized depth fog settings.
* **Ice Mechanics**: Generates natural ice structures in applicable environments. Fully compatible with freeze/melt block states.

---

## Technical Optimizations

Designed specifically to reduce server overhead and client-side stuttering:

### Memory Management
* **Leak Resolution**: Identified and closed outstanding memory leaks related to positional world tracking.
* **Data Footprint**: Streamlined internal telemetry storage inside the core temperature subsystem to lower global memory usage.

### Processing Efficiency
* **Execution Gates**: Implemented early-exit logic across major processing loops, reducing unnecessary tick evaluations when specific tracking systems are inactive.
* **Subsystem Tuning**: Optimized calculations for environmental water-block scanning, canteen consumption actions, and tick-by-tick thirst calculations.
* **Garbage Collection**: Lowered raw object instantiation rates during standard gameplay loops to reduce overhead on the Java Garbage Collector.

---

## Mod Integrations
### Fully Supported Mods List
* Animania 
* Armor Underwear 
* Baubles
* Biomes O' Plenty
* Cave Generator
* DynamicSurroundings
* EnhancedVisuals
* First Aid
* Fluidlogged API
* Greenery
* Harvest Festival Legacy
* Inspirations
* Lycanites Mobs
* Ore Excavation
* Pams HarvestCraft
* Potion Core
* Pyrotech
* Realistic Torches
* Rustic
* Serene Seasons
* Simple Camp Fire
* Streams
* Tinkers Construct
* The Betweenlands
* SurvivalTools
* Weather2 Remastered

### Additional Compatibility
- Cave Generator
- Fluidlogged API
- Baubles
- Inspirations

> If you want it to be compatible with a mod, let me know. I'll try to make it work.

---

## Installation

1. Ensure your instance is running **Minecraft 1.12.2** with **Forge 14.23.5.2838** or higher
2. Drop the downloaded `SimpleDifficulty` `.jar` file into your local `mods` folder
3. Launch the application to generate standard configuration templates

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

## License & Credits
### License: Distributed under the permissive MIT License.
### Authors:
- Charles445: Created the original version of SimpleDifficulty
- juraj-hrivnak: Created the Underdog fork
- Onyx_i7: Maintained current development branch, optimizations, and modern fork compatibility

---

## Support

- Report bugs and request new features in the GitHub issue tracker
- Send us your feedback and suggestions
- Ask about mod compatibility
