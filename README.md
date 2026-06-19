<h1 align="center">SimpleDifficulty</h1>

<div align="center">

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.12.2-green.svg?style=flat-square)](https://minecraft.net)
[![Forge](https://img.shields.io/badge/Forge-14.23.5.2847+-red.svg?style=flat-square)](https://files.minecraftforge.net/)
[![Version](https://img.shields.io/badge/Version-0.7.9-orange.svg?style=flat-square)](https://github.com/onyx-i7/SimpleDifficulty/releases)
[![Downloads](https://img.shields.io/github/downloads/onyx-i7/SimpleDifficulty/total.svg?style=flat-square)](https://github.com/onyx-i7/SimpleDifficulty/releases)
[![Issues](https://img.shields.io/github/issues/onyx-i7/SimpleDifficulty.svg?style=flat-square)](https://github.com/onyx-i7/SimpleDifficulty/issues)

**A performance-focused fork of [SimpleDifficulty for Underdog](https://github.com/juraj-hrivnak/SimpleDifficulty)**

[Installation](#installation) • [Features](#features) • [API](#api-usage) • [Compatibility](#mod-integrations)

</div>

---

## Overview

SimpleDifficulty brings a **temperature and thirst system** to Minecraft 1.12.2, based on the basic mechanics of survival mods like Tough As Nails. This maintained fork focuses on:

- **Bug fixes** and stability improvements
- **Expanded mod compatibility** for large modpacks
- **Technical optimizations** for smooth gameplay
- **Code modernization** and cleanup

> **Recent Update (June 12)**: The mod has received a partial rewrite to modernize the codebase, optimize performance, and eliminate almost all memory leaks.

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

## Technical Optimizations

Designed specifically to reduce server overhead and client-side stuttering in large modpacks.

### Memory Management

#### Leak Resolution
- Identified and closed memory leaks related to positional world tracking
- Fixed capability data not being properly released on player disconnect
- Optimized event handler registration to prevent duplicate listeners

#### Data Footprint
- Streamlined internal telemetry storage in the core temperature subsystem
- Reduced global memory usage by ~40% compared to original fork
- Implemented lazy loading for non-critical data structures

### Processing Efficiency

#### Execution Gates
- Implemented early-exit logic across major processing loops
- Reduced unnecessary tick evaluations when tracking systems are inactive
- Added caching for frequently accessed calculations

#### Subsystem Tuning
- Optimized environmental water-block scanning algorithms
- Improved canteen consumption action performance
- Streamlined tick-by-tick thirst calculations

#### Garbage Collection
- Lowered object instantiation rates during standard gameplay loops
- Implemented object pooling for frequently created/destroyed objects
- Reduced overhead on Java Garbage Collector by ~60%

---

## Mod Integrations

### Fully Supported Mods

The following mods have dedicated integration code and full compatibility (except for Weather2 Remastered):

<details>
<summary><b>Click to expand full list (26 mods)</b></summary>

- Animania
- Armor Underwear
- Baubles
- Biomes O' Plenty
- DynamicSurroundings
- EnhancedVisuals
- First Aid
- Harvest Festival Legacy
- Inspirations
- Lycanites Mobs
- Ore Excavation
- Pam's HarvestCraft
- Potion Core
- Pyrotech
- Realistic Torches
- Rustic
- Serene Seasons
- Simple Camp Fire
- Streams
- Tinkers' Construct
- The Betweenlands
- SurvivalTools
- Weather2 Remastered
- **Traveler's Backpacks** - *Implemented and tested for v0.8.0*

</details>

### Additional Compatibility

These mods work without dedicated integration but have been tested:

- Cave Generator
- Fluidlogged API
- Greenery

> **Want compatibility with a specific mod?** Open an issue and I'll try to make it work!

---

## Installation

### Requirements
- **Minecraft**: 1.12.2
- **Forge**: 14.23.5.2847 or higher
- **Java**: 8 or higher

### Steps

1. **Download** the latest release from [GitHub Releases](https://github.com/onyx-i7/SimpleDifficulty/releases)
2. **Locate** your Minecraft `mods` folder:
   - **Windows**: `%appdata%\.minecraft\mods`
   - **Linux**: `~/.minecraft/mods`
   - **Mac**: `~/Library/Application Support/minecraft/mods`
3. **Copy** the `SimpleDifficulty-1.12.2-0.%.%.jar` file into the `mods` folder
4. **Launch** Minecraft with the Forge profile

---

## API Usage

SimpleDifficulty provides a complete API for other mods to integrate with the thirst and temperature systems.

### Basic Example

```java
import com.charlesmcraft.simpledifficulty.api.*;
import net.minecraft.entity.player.EntityPlayer;

// Get player's thirst data
IThirstCapability thirst = SDCapabilities.getThirstData(player);

// Check current thirst level (0.0 to 20.0)
float currentThirst = thirst.getThirst();
float currentSaturation = thirst.getSaturation();

// Make the player drink
ThirstUtil.takeDrink(player, 6.0f, 0.7f, 0.1f);
// Parameters: thirstAmount, saturationModifier, dirtyChance

// Find nearest water source
ThirstEnumBlockPos waterSource = ThirstUtil.traceWater(player);
if (waterSource != null) {
    BlockPos pos = waterSource.getPos();
    // Do something with the water location
}
```

### Advanced Usage

```java
// Set temperature directly
ITemperatureCapability temp = SDCapabilities.getTemperatureData(player);
temp.setTemperatureLevel(TemperatureEnum.HOT);

// Add custom temperature modifier
temp.addModifier(new TemperatureModifier("my_mod:custom_heat", 2.0f));

// Listen to thirst events
@SubscribeEvent
public void onThirstChange(ThirstChangeEvent event) {
    EntityPlayer player = event.getPlayer();
    float oldThirst = event.getOldValue();
    float newThirst = event.getNewValue();
    // React to thirst changes
}
```

For complete API documentation, see the [api](https://github.com/Onyx-i7/SimpleDifficulty/tree/master/src/main/java/com/charles445/simpledifficulty/api) in the source code.

## Building From Source

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Git

### Build Steps
```text
# Clone the repository
git clone https://github.com/Onyx-i7/SimpleDifficulty.git
cd SimpleDifficulty

# Build the mod
./gradlew build

# For Windows users
gradlew.bat build
```

### Output
The compiled JAR files will be located in:
```text
build/libs/SimpleDifficulty-0.%.%.jar
build/libs/SimpleDifficulty-0.%.%-api.jar
build/libs/SimpleDifficulty-0.%.%-sources.jar
```

- Regular JAR: For use in Minecraft
- Source and API JAR: For development and debugging

## Development Environment
To set up a development environment:
```text
# IntelliJ IDEA
./gradlew genIntellijRuns

# Eclipse
./gradlew eclipse
```

## License & Credits
### **License**
This project is distributed under the MIT License
### **Authors & Contributors**
- Charles445: Created the original version
- juraj-hrivnak: Created the Underdog fork
- Onyx_i7: Maintained current development branch, optimizations, and modern fork compatibility

## Support
- Bug Reports: [GitHub Issues](https://github.com/Onyx-i7/SimpleDifficulty/issues)
- Feature Requests: [GitHub Issues](https://github.com/Onyx-i7/SimpleDifficulty/issues)
