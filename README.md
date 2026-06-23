# Advanced Solar Panels (decompiled / deobfuscated)

Buildable source project for **Advanced Solar Panels 4.3.0** — an IC2 Experimental addon
by Icedfire / SeNtiMeL / Chocohead. Recreated from `Advanced Solar Panels-4.3.0.jar` by
decompiling and deobfuscating, so it can be studied and later ported.

> ⚠️ This is the **original Minecraft 1.12.2** mod. It is *not* the 1.20.1 codebase yet.
> It targets the classic IC2 Experimental (`ex112`) API. Porting to the 1.20.1
> "IC2: Refactored" in `../ic2` is a separate follow-up step.

## What it adds

Advanced, Hybrid, Ultimate Hybrid and Quantum **solar panels** (escalating EU/t and
internal storage), the **Molecular Transformer** (the molecular assembler that turns one
item into another by pouring huge amounts of EU into it), the **Quantum Generator**, three
chargeable **Solar Helmets** (armour), and the crafting components (sunnarium, iridium,
irradiant parts, cores, …).

## How it was reconstructed

1. **Decompiled** the jar with [ForgeFlower](https://github.com/MinecraftForge/ForgeFlower)
   2.0.629.0.
2. **Deobfuscated** SRG names (`field_*` / `func_*`) back to MCP names using the
   `mcp_stable-39-1.12` mapping CSVs (330 references). The build mapping (`stable_39`)
   **must** match the CSVs used here, or some method names won't line up (e.g.
   `NBTTagCompound.isEmpty()` in stable_39 vs `hasNoTags()` in snapshot_20171003).
3. **Resources** (`assets/`, `mcmod.info`) copied verbatim from the jar.

A handful of casts the decompiler dropped were restored by hand (the dyeable-armour
`hasColor` override, the `MolecularOutput` network decode, and the two dynamic-GUI
factory methods).

## Toolchain (legacy 1.12.2)

| | |
|---|---|
| Minecraft | 1.12.2 |
| Forge | 14.23.5.2847 |
| ForgeGradle | 2.3-SNAPSHOT |
| Gradle | 4.9 (wrapper) |
| Mappings | `stable_39` |
| **Java** | **8 (required)** — FG 2.3 / Gradle 4.9 will not run on JDK 17/21 |

## Dependencies (vendored in `libs/`)

These are fetched at build time only as local files, because the IC2 maven is plain HTTP:

- `libs/ic2-2.8.222-ex112-dev.jar` — from <http://maven.ic2.player.to/>
  (`net.industrial-craft:industrialcraft-2:2.8.222-ex112:dev`). Already MCP-named;
  provides the `ic2.core.*` internals the mod uses.
- `libs/jei_1.12.2-4.16.1.1013.jar` — from <https://maven.blamejared.com/>
  (`mezz.jei:jei_1.12.2`). Needed by `JEICompat`.

## Building

```sh
# Point JAVA_HOME at a JDK 8 install, e.g. Temurin 8:
export JAVA_HOME=/path/to/jdk8
./gradlew build
```

Output: `build/libs/advanced-solar-panels-4.3.0.jar` (reobfuscated to runtime SRG names,
matching the original distribution).

To set up an IDE workspace: `./gradlew setupDecompWorkspace` then import as a Gradle
project (using the JDK 8 above).

## Links

- Original mod: <https://www.curseforge.com/minecraft/mc-mods/advanced-solar-panels>
- Target IC2 for the 1.20.1 port: <https://github.com/HalfCooler/ic2>
