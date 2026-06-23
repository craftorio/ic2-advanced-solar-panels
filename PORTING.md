# Porting notes — Advanced Solar Panels 1.12.2 → 1.20.1 / IC2: Refactored

This file records the non-obvious decisions made porting the classic 1.12.2 addon
(`forge/1.12.2` branch) to Minecraft 1.20.1 against [IC2: Refactored](https://github.com/HalfCooler/ic2)
(`forge/1.20.1`, the default branch). It is the sibling of `advanced-machines`'s PORTING.md and
shares most of the same gotchas.

## What IC2: Refactored removed (and how this port copes)

The 1.12.2 mod plugged into IC2's addon platform, which no longer exists:

- **`TeBlock` / `TeBlockRegistry` / `TeBlockFinalCallEvent`** (one enum registered all blocks) →
  rebuilt as a Forge `DeferredRegister` (`AdvSolarBlocks`): one `Ic2TileEntityBlock` + matching
  `BlockEntityType` + `BlockItem` per machine.
- **`ItemMulti` / metadata sub-items** (`ItemCraftingThings` packed 14 crafting items into one
  metadata item) → 14 individual `Item`s, since 1.20.1 has no item metadata.
- **`Configuration` + the editable `_MTRecipes` file** → solar/quantum values are hard-coded to the
  classic defaults; Molecular Transformer recipes are defined in code (`AdvSolarRecipes`).
- **`Localization.translate`** → `Component.translatable(key).getString()`.

## Tile entities

- **Solar panels** extend IC2's own `TileEntityBaseGenerator` (the base behind IC2's solar
  generator): an energy source with a buffer and a managed charge slot. `gainEnergy()` adds
  `dayPower`/`nightPower` based on sky visibility and day/night, mirroring `TileEntitySolarGenerator`.
- **Molecular Transformer** extends `TileEntityElectricMachine` (an IC2 energy-sink buffer) and pours
  stored energy into the current recipe until the recipe's total EU is met.
- **Quantum Generator** extends `TileEntityBaseGenerator`, gated by a redstone signal.

## GUIs

IC2's `DynamicContainer.create(syncId, inv, base)` auto-loads `guidef/*.xml` via
`GuiParser.class.getResourceAsStream`, which under the Forge 1.20.1 module system only sees IC2's own
jar — never this addon's. So `gui/GuiDefs` reads the XML from the tile's own classloader and feeds it
to the private `GuiParser.parse(InputStream, Class)` via reflection, then calls the
`Ic2ScreenHandlers.DYNAMIC_BE` overload with the parsed node. The original guidef XMLs port directly
(custom gauge styles re-registered in `gui/ProgressBars`).

## Assets

- 1.20.1 stitches `textures/block/` and `textures/item/` (singular). The original used
  `textures/blocks/` and `textures/items/` (plural) **with spaces and capitals** in file names
  (`Advanced Solar Side.png`); all were renamed to lowercase-underscored and every model reference
  re-pointed. GUI textures were renamed to match (`AdvancedSolarPanel.png` → `advanced_solar_panel.png`).
- The single `blockstates/machines.json` (a `type` multiblock variant) became one per-block blockstate
  keyed on `facing` (+ `active` for the molecular transformer and quantum generator).
- `lang/*.lang` → `lang/*.json`, re-rooted to `block.*` / `item.*` / `container.*` (IC2 titles its
  managed-BE GUIs via `container.<ns>.<path>`, not `block.*`) / `itemGroup.*`.
- A root `pack.mcmeta` is **required**. Without it the dev resource pack logged
  "Missing metadata in pack" and every `models/` file failed to resolve at bake time (blocks and
  items rendered as the missing-model placeholder) even though `blockstates/` loaded fine.

## Dropped or simplified (vs. the 1.12.2 original)

- **Molecular Transformer 3D model + TESR** (`PrettyMolecularTransformerTESR`/animated plasma core) →
  a static cube using the model's texture.
- **Custom transparent / background-less GUI rendering** (`TransparentDynamicGUI` /
  `BackgroundlessDynamicGUI`) → the standard dynamic GUI.
- **Quantum Generator's adjustable output** (GUI +/- buttons + tier picker, driven by a network event
  protocol) → a fixed classic-default output (512 EU/t, tier 3). The buttons were removed from its guidef.
- **Solar helmet HUD overlay** (`IItemHudProvider` energy HUD) and **helmet dyeing** → not
  reimplemented; helmets still generate, charge the inventory, refill air (hybrid/ultimate), and absorb
  damage. Worn helmets currently use the vanilla diamond armour texture.
- **The reskinned vanilla double stone slab** (`ItemDoubleSlab`) → dropped.
- **The Molecular Transformer recipe set** was trimmed to a representative, vanilla-mappable subset;
  recipes referencing IC2 items and the old ore dictionary were left out.
- **JEI integration** for the Molecular Transformer category is not yet ported.
- **Crafting/assembly recipes** for the machines/items are not yet defined (obtainable via the creative
  tab); the in-world machinery all works.
