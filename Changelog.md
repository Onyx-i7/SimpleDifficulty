> **Note: This was created using Gemini based on the list of commits and the pull request's own Markdown, since it's impossible to list 50,000 commits and changes, but it serves to provide an overview of the changes, as the pull request is a mess.**

# Changelog

**Statistics:**
- Lines modified: +9,543 / -9,465
- Files changed: 146
- Commits: 289

## Added

### Mod Compatibility
- **Weather2 Remastered**: Implemented native integration with rain detection algorithm, campfire extinction logic, and hail-induced hypothermia effect
- **The Betweenlands**: Added support for Clean Water and Swamp Water (85% contamination rate) with -3 global temperature modifier
- **HBM's Nuclear Tech CE**: Added compatibility methods and population handlers
- **Traveler's Backpacks**: Fixed creative tab lag and improved general integration
- **Cleanroom Loader**: Added support for Cleanroom 0.6.0-alpha
- **Serene Seasons**: Implemented reflection bridge for temperature handling

### Configuration Options
- `THIRST_EXHAUSTION_MULTIPLIER` - Server-side multiplier for thirst exhaustion
- `SALT_WATER_THIRST` - Configuration option for salt water thirst values
- `THIRST_HUD_X` / `THIRST_HUD_Y` - Configurable HUD positioning offsets

### Features
- Dynamic bottle filling interaction with mod fluid blocks (Purified, Salt, Spring water)
- Complete JavaDoc documentation for public API (`SDCapabilities`, `IThirstCapability`, `IItemCanteen`, etc.)
- Bottle result method for purified water
- Global server-side scheduler (20-tick interval) for block state updates

## Changed

### Architecture & Build System
- Migrated from ForgeGradle 2.3 to RetroFuturaGradle 2.0.2 (GTNH community fork)
- Upgraded Gradle from 4.10.3 to 9.6.1 with parallel execution and configuration caching
- Replaced ASM bytecode manipulation with Mixin framework (MixinBooter dependency)
- Modernized Gradle syntax: `archivesBaseName` → `base { archivesName }` block
- Added GTNH Maven repository for plugin resolution
- ForceLoadAsMod attribute added to JAR manifest

### Code Refactoring
- `CommandSimpleDifficulty.java`: Reduced from 903 to 563 lines (duplicate code removal)
- `OreDictUtil.java`: Replaced static field caching with real-time getter methods while maintaining deprecated fields for backward compatibility
- `ThirstHandler.java`: Changed `.equals()` to `==` for singleton comparisons
- `ServerProxy.java`: Removed redundant `preInit()`, `init()`, `postInit()` methods
- Centralized reflection initialization in `CompatUtil.java` using static blocks

### Fluid & Container Systems
- Synchronized container capabilities with active world fluid blocks
- Empty bottles now dynamically detect Salt, Normal, and Spring water instead of defaulting to vanilla water
- Smelting logic locked to prevent spring water downgrade
- Rain Collector algorithm changed from `isRainingAt(pos.up())` to `isRaining() && canSeeSky(pos.up())`
- Removed legacy config-based random scalar constraint for rain collector

## Fixed

### Critical Crashes
- **Dedicated Server**: Removed `@SideOnly(Side.CLIENT)` annotation from `ConfigHandler.onPlayerJoin()` that caused `NoSuchMethodError` on server-side `PlayerLoggedInEvent`
- **NullPointerException Prevention**: Added null checks for `getRegistryName()` in `TemperatureHandler` and `ThirstHandler` before calling `.toString()`
- **Unsafe Cast**: Added `instanceof` check before casting to `EntityPlayerMP` in `ConfigHandler`
- **Client Proxy**: Added null checks in `isClientConnectedToServer()` for `getConnection()` and `getNetworkManager()`
- **ASM Failsafe**: Override `ClassWriter#getCommonSuperClass` to intercept `ClassNotFoundException` and fallback to `java/lang/Object`

### Gameplay & Mechanics
- **JEI Integration**: Corrected NBT serialization in `FixedBrewingOreRecipe.java` to allow dynamic potion recipe registration
- **Thirst Bar Rendering**: Fixed positioning issue where thirst bar rendered above hunger bar
- **LemonSkin**: Fixed texture positioning bug
- **Canteen Durability**: Fixed inconsistent durability depletion and erratic hydration scaling
- **Weather Detection**: Campfires now extinguish under global vanilla rain or Weather2 localized storms

### Metadata & Localization
- Restructured bucket registry localized entries in `en_us.lang` to remove broken unlocalized strings
- Added Spanish (Spain) `es_es`, Spanish (Mexico) `es_mx` translations
- Updated missing entries in Russian `ru_ru.lang` template

## Performance

### Memory Leak Fixes
- **ThirstCapability**: Replaced `javax.vecmath.Vector3d` allocation per tick with primitive `double` coordinates and `distanceSquared` calculation (~90% CPU reduction)
- **FluidHandler**: Changed `ArrayList` to `ConcurrentLinkedQueue` for `scheduledMixtures`, eliminated `indexOf()` O(n²) complexity, added per-tick processing limit
- **TemperatureStorage**: Replaced `Iterator<NBTBase>` with classic `for` loop using `tagCount()` and `getCompoundTagAt(i)` to avoid object allocation during NBT deserialization

### CPU Optimizations
- **MiscHandler Weather Detection**: Reduced scan radius from 32x8x32 (8,192 blocks) to 16x4x16 (1,024 blocks), 87.5% reduction in iterations
- **MutableBlockPos Reuse**: Eliminated `pos.toImmutable()` allocation in inner loops
- **Canteen NBT Caching**: Implemented local caching layers, reduced NBT reads by 50%
- **Static HashSets**: Added hyper-fast fluid lookups with early-exit parameters
- **TooltipHandler**: Cached `TemperatureUtil.getArmorTemperatureTag()` result, made `DecimalFormat` instance `static final`

### Build Performance
- First build time: 10-15 minutes → 4-5 minutes (60-70% faster)
- Subsequent builds: 5-10 minutes → 30-60 seconds (80-90% faster)
- Minecraft decompilation with Fernflower: 5-10 minutes → 115 seconds
- Incremental compilation enabled (only changed files recompiled)

## Removed

- Complete ASM directory (`com.charles445.simpledifficulty.asm`)
- Legacy compatibility folders (`HasShadow`, `compat/shadow`)
- Experimental Frost items and Dragon Canteen (added and removed during PR development)
- Legacy random scalar constraint `ModConfig.server.miscellaneous.rainCollectorFillChance`
- Redundant proxy initialization methods

## Build & CI/CD

- Implemented GitHub Actions workflow (`buildmod.yml`) for automated builds
- Added `gradle-wrapper.jar` for automated builds
- Updated `gradle-wrapper.properties` for Gradle 9.x
- Applied 525 MCP patches automatically via RetroFuturaGradle
- Java 21+ required for Gradle execution (Java 8 downloaded automatically by toolchain for compilation)

---

<details>
<summary>Technical Details by File</summary>

### Core Handlers
- `MiscHandler.java`: Global scheduler, weather detection optimization, `MAX_PLAYERS_PER_TICK = 5` limit
- `TemperatureHandler.java`: Hail damage hook mapped to `TemporaryModifierGroupEnum.DRINK.group()`
- `ThirstHandler.java`: Singleton comparisons, NPE prevention
- `ConfigHandler.java`: Dedicated server crash fix, JSON error logging to console

### Capabilities & Utilities
- `ThirstCapability.java`: Primitive coordinates, squared distance calculation
- `FluidHandler.java`: Thread-safe queue, O(1) operations, cleanup logic
- `OreDictUtil.java`: Real-time getters with deprecated static fields
- `CompatUtil.java`: Static reflection initialization, parameter validation
- `TemperatureStorage.java`: Iterator elimination in NBT deserialization

### ASM/Mixin Migration
- `ObfRemappingClassWriter.java`: `getCommonSuperClass` override with fallback
- `InsnComparator.java`: Null-safe evaluation blocks for wildcards
- `ObfHelper.java`: Precompiled `Pattern` for regex operations
- `mixins.simpledifficulty.json`: New Mixin configuration file
- MixinBooter dependency added

### Blocks & Items
- `BlockRainCollector.java`: Sky visibility check, Weather2 compatibility
- `BlockCampfire.java`: Weather-based extinction, thread-safe `extinguishCampfire` method
- `BlockFluidBasic.java`: Dynamic bottle filling, water type detection
- `BlockFluidSaltWater.java`: New salt water fluid class
- `ItemCanteen.java`: NBT caching, dose handling refactor
- `ItemDragonCanteen.java`: ExtraItem integration for capacity

### Debug & Testing
- `DebugUtil.java`: Bounding checks for timers, division-by-zero prevention
- `DebugVerifier.java`: Empty registry guards before iterator calls

</details>

---

**Resolved Issues:**
- [#15](https://github.com/juraj-hrivnak/SimpleDifficulty/issues/15)
- [#13](https://github.com/juraj-hrivnak/SimpleDifficulty/issues/13)
- [#12](https://github.com/juraj-hrivnak/SimpleDifficulty/issues/12)
- [#5](https://github.com/juraj-hrivnak/SimpleDifficulty/issues/5)
- A thirst bar in high or unusual resolutions that changes position because it was implemented incorrectly

![image](https://camo.githubusercontent.com/a622e766d5c6dccb62800ecbdc55c738d4663e227f3a06384e512a438939fcca/68747470733a2f2f692e6962622e636f2f4866544d573043372f436170747572612d64652d70616e74616c6c612d323032362d30382d30312d31362d33312d30322e6a7067)
