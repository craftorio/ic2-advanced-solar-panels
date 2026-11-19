# Advanced Solar Panels

Minecraft **1.20.1 / Forge 47.4.20** port of the Advanced Solar Panels IC2 addon
(mod id `advanced_solar_panels`, version 4.3.2, Java 17). It targets
[IC2: Refactored](https://github.com/HalfCooler/ic2) `2.10.26-ex120` or newer.
The 1.12.2 original lives on `forge/1.12.2`; this branch is `forge/1.20.1`.

The mod adds a tier of solar generators, matching solar helmets, the molecular
transformer, the quantum generator, and the crafting materials those need.
Solar output needs a view of the sky and drops at night or in rain. Machines
are IC2 tile entities (`Ic2TileEntityBlock`) registered in `AdvSolarBlocks`.

## Layout

- `src/main/java/com/chocohead/advsolar/` — mod code. Tile entities in `te/`,
  recipes in `recipe/`, dynamic GUIs in `gui/`, JEI in `compat/`.
- `src/main/resources/assets/advanced_solar_panels/` — blockstates, models,
  lang, guidef XML, textures.
- `src/main/resources/data/advanced_solar_panels/recipes/` — `ic2:shaped` /
  `ic2:shapeless` and machine recipes.
- `PORTING.md` — decisions from the 1.12.2 port. Read it before changing
  registration, GUIs, or assets.
- `libs/ic2-forge-*-dev.jar` — vendored IC2 dev jar, not committed. Build steps
  are in `README.md`.

## Build

```sh
./gradlew build
```

Compile depends on the IC2 dev jar named in `gradle.properties` (`ic2_version`).
JEI is `compileOnly` / `runtimeOnly`. Do not run the reobfuscated IC2 release
jar through `fg.deobf`.

## Attribution

Never write a co-author trailer, or the words "co-authored by", anywhere:
commit messages, code, docs, comments, or pull requests. This includes the
git trailer and any spaced or capitalized spelling of it.

`.githooks/pre-commit` rejects it in staged files. `.githooks/commit-msg`
rejects it in the commit message. This file and `.githooks/` are the only
exceptions, because they name the ban. Link them once per clone:

```sh
ln -sfn ../../.githooks/pre-commit .git/hooks/pre-commit
ln -sfn ../../.githooks/commit-msg .git/hooks/commit-msg
```
