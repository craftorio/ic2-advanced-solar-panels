# Advanced Solar Panels — 1.20.1 port

A Minecraft **1.20.1 / Forge** port of the classic **Advanced Solar Panels** IndustrialCraft 2
addon, running against the modern **IC2: Refactored** fork.

- Original mod (… 1.12.2), by Icedfire / SeNtiMeL / Chocohead:
  <https://www.curseforge.com/minecraft/mc-mods/advanced-solar-panels>
- Target IC2 for 1.20.1+ (the fork this port depends on):
  <https://github.com/HalfCooler/ic2>

Advanced Solar Panels adds a tier ladder of solar generators plus the machinery and materials that
go with them.

## Content

| Block / item | Notes |
|---|---|
| Advanced Solar Panel | 8 EU/t day, 1 EU/t night, tier 1 |
| Hybrid Solar Panel | 64 EU/t day, 8 EU/t night, tier 2 |
| Ultimate Hybrid Solar Panel | 512 EU/t day, 64 EU/t night, tier 3 |
| Quantum Solar Panel | 4096 EU/t day, 2048 EU/t night, tier 5 |
| Molecular Transformer | pours EU into an item to transmute it into another |
| Quantum Generator | redstone-gated high-tier free generator |
| Advanced / Hybrid / Ultimate Solar Helmet | chargeable armour that generates EU and charges your inventory |
| Crafting components | sunnarium, iridium, irradiant parts, cores, … |

## Branches

| Branch | Contents |
|---|---|
| `forge/1.20.1` | **default** — the live 1.20.1 port (this) |
| `forge/1.12.2` | the original mod, decompiled & deobfuscated, kept as a study reference |

## Requirements

| | |
|---|---|
| Minecraft | 1.20.1 |
| Forge | 47.4.20 |
| IC2 | [IC2: Refactored](https://github.com/HalfCooler/ic2) `2.10.26-ex120` or newer |

## Building

This mod compiles against IC2's internal classes, so it needs a **dev jar** of IC2 — a jar of
IC2's compiled, official-named (dev-mappings) classes and resources. It is **not** committed
here; build it from a checkout of the IC2 fork next to this repo:

```sh
# 1. Build IC2 (produces build/classes + build/resources under official mappings)
cd ../ic2
./gradlew build

# 2. Assemble the dev jar and drop it into this project's libs/
jar cf ../advanced-solar-panels/libs/ic2-forge-2.10.26-ex120-dev.jar \
    -C build/classes/java/main . \
    -C build/resources/main .

# 3. Build the addon
cd ../advanced-solar-panels
./gradlew build
```

Output: `build/libs/advanced_solar_panels-4.3.0.jar`. To test, install it into a 1.20.1 Forge
profile alongside IC2, or run `./gradlew runClient` with IC2 on the mod classpath.

> **Why a dev jar and not `fg.deobf` on the release jar?** IC2's release jar is reobfuscated,
> so its overrides of Minecraft methods are stored under SRG names, and `fg.deobf` does not
> reliably remap those inherited-override names — compilation breaks. The dev classes are already
> official-named, so they are consumed with a plain `files(...)` dependency. Re-vendor the jar
> whenever IC2 changes.

## How the port works

IC2: Refactored removed the addon platform the original hooked into (the `TeBlock` registry, the
profile/texture system, `TeBlockFinalCallEvent` and the config-driven recipe loader). The processing
core survives, so this port reuses IC2's generator/machine bases, the dynamic GUI system
(`DynamicContainer` + `guidef/*.xml`) and the energy API, and reimplements registration with a Forge
`DeferredRegister`. See **PORTING.md** for the full list of decisions and the features that were
simplified or dropped.

## Credits & licensing

Advanced Solar Panels was created by **Icedfire** and maintained by **SeNtiMeL** and **Chocohead**;
see the [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/advanced-solar-panels) for the
original. This port targets [HalfCooler's IC2: Refactored](https://github.com/HalfCooler/ic2).
IndustrialCraft 2 and Advanced Solar Panels are the property of their respective authors; this
repository is a compatibility port and is bound by their licensing terms.
