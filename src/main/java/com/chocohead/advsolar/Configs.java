package com.chocohead.advsolar;

import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import com.chocohead.advsolar.renders.PrettyMolecularTransformerTESR;
import com.chocohead.advsolar.tiles.TileEntityAdvancedSolar;
import com.chocohead.advsolar.tiles.TileEntityHybridSolar;
import com.chocohead.advsolar.tiles.TileEntityQuantumGenerator;
import com.chocohead.advsolar.tiles.TileEntityQuantumSolar;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import com.chocohead.advsolar.tiles.TileEntityUltimateHybridSolar;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.IOUtils;

final class Configs {
   private static final String GENERAL = "general";
   private static final String SOLARS = "solars";
   private static final String QUANTUM_GENERATOR = "quantum generator";
   private static final String CRAFTING = "recipes settings";
   static boolean hardRecipes;
   static boolean easyASPRecipe;
   static boolean canCraftDoubleSlabs;
   static boolean canCraftMT;
   static boolean canCraftASP;
   static boolean canCraftHSP;
   static boolean canCraftUHSP;
   static boolean canCraftQSP;
   static boolean canCraftASH;
   static boolean canCraftHSH;
   static boolean canCraftUHSH;
   private static final String NEW_LINE = System.getProperty("line.separator");
   private static final String CONFIG_VERSION = "2.0";
   static MTRecipe[] MTRecipes;

   static void loadConfig(File config, boolean client) {
      AdvancedSolarPanels.log.info("Loading ASP Config from " + config.getAbsolutePath());
      loadNormalConfig(config, client);

      try {
         loadMolecularTransformerConfig(config.getParentFile(), config.getName());
      } catch (ParseException var3) {
         MolecularTransformerRecipeManager.showError("Error reading Molecular Transformer recipes file:" + NEW_LINE + var3.toString());
      }

   }

   private static void loadNormalConfig(File configFile, boolean client) {
      Configuration config = new Configuration(configFile);

      try {
         config.load();
         TileEntityAdvancedSolar.settings = new TileEntitySolarPanel.SolarConfig(config.get("solars", "AdvancedSPGenDay", 8).getInt(8), config.get("solars", "AdvancedSPGenNight", 1).getInt(1), config.get("solars", "AdvancedSPStorage", 32000).getInt(32000), config.get("solars", "AdvancedSPTier", 1).getInt(1));
         TileEntityHybridSolar.settings = new TileEntitySolarPanel.SolarConfig(config.get("solars", "HybrydSPGenDay", 64).getInt(64), config.get("solars", "HybrydSPGenNight", 8).getInt(8), config.get("solars", "HybrydSPStorage", 100000).getInt(100000), config.get("solars", "HybrydSPTier", 2).getInt(2));
         TileEntityUltimateHybridSolar.settings = new TileEntitySolarPanel.SolarConfig(config.get("solars", "UltimateHSPGenDay", 512).getInt(512), config.get("solars", "UltimateHSPGenNight", 64).getInt(64), config.get("solars", "UltimateHSPStorage", 1000000).getInt(1000000), config.get("solars", "UltimateHSPTier", 3).getInt(3));
         TileEntityQuantumSolar.settings = new TileEntitySolarPanel.SolarConfig(config.get("solars", "QuantumSPGenDay", 4096).getInt(4096), config.get("solars", "QuantumSPGenNight", 2048).getInt(2048), config.get("solars", "QuantumSPStorage", 10000000).getInt(10000000), config.get("solars", "QuantumSPTier", 5).getInt(5));
         TileEntityQuantumGenerator.settings = new TileEntityQuantumGenerator.QuantumGeneratorConfig(config.get("quantum generator", "quantumGeneratorDefaultProduction", 512).getInt(512), config.get("quantum generator", "quantumGeneratorDefaultTier", 3).getInt(3));
         if (client) {
            PrettyMolecularTransformerTESR.drawActiveCore = config.get("general", "Draw Molecular Transformer active animation", false).getBoolean(false);
         }

         ItemArmourSolarHelmet.chargeWholeInventory = config.get("general", "Should solar helmets charge armour only?", true).getBoolean(true);
         hardRecipes = config.get("recipes settings", "Enable hard recipes", true).getBoolean(true);
         easyASPRecipe = config.get("recipes settings", "Enable simple Advanced Solar Panel recipe", false).getBoolean(false);
         canCraftDoubleSlabs = !config.get("recipes settings", "Disable Double-Slab recipe", false).getBoolean(false);
         canCraftMT = !config.get("recipes settings", "Disable Molecular Transformer recipe", false).getBoolean(false);
         canCraftASP = !config.get("recipes settings", "Disable Advanced Solar Panel recipe", false).getBoolean(false);
         canCraftASH = !config.get("recipes settings", "Disable Advanced Solar Helmet recipe", false).getBoolean(false);
         canCraftHSP = !config.get("recipes settings", "Disable Hybrid Solar Panel recipe", false).getBoolean(false);
         canCraftHSH = !config.get("recipes settings", "Disable Hybrid Solar Helmet recipe", false).getBoolean(false);
         canCraftUHSP = !config.get("recipes settings", "Disable Ultimate Solar Panel recipe", false).getBoolean(false);
         canCraftUHSH = !config.get("recipes settings", "Disable Ultimate Solar Helmet recipe", false).getBoolean(false);
         canCraftQSP = !config.get("recipes settings", "Disable QuantumSolarPanel recipe", false).getBoolean(false);
      } catch (Exception var7) {
         AdvancedSolarPanels.log.fatal("Fatal error reading config file.", var7);
         throw new RuntimeException(var7);
      } finally {
         if (config.hasChanged()) {
            config.save();
         }

      }

   }

   private static void loadMolecularTransformerConfig(File configFolder, String configFile) throws ParseException {
      int fileExtensionMarker = configFile.lastIndexOf(46);
      File config = new File(configFolder, configFile.substring(0, fileExtensionMarker) + "_MTRecipes" + configFile.substring(fileExtensionMarker));
      AdvancedSolarPanels.log.info("Loading MT Recipes from " + config.getAbsolutePath());
      if (!config.exists()) {
         fillDefault(config);
      }

      FileInputStream stream = null;
      BufferedReader reader = null;
      List<MTRecipe> recipes = new ArrayList(20);

      try {
         stream = new FileInputStream(config);
         reader = new BufferedReader(new InputStreamReader(stream));
         int lineNumber = 0;

         String line;
         while((line = reader.readLine()) != null) {
            ++lineNumber;
            line = line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
               if (line.startsWith("version=")) {
                  String version = line.substring(line.indexOf(61) + 1);
                  if (!"2.0".equals(version)) {
                     throw new ParseException("Advanced Solars expected a file version of 2.0, but the config is " + version, line.indexOf(61) + 1);
                  }
               } else {
                  MTRecipe recipe = new MTRecipe(lineNumber, line);
                  if (recipe.isValid()) {
                     recipes.add(recipe);
                  } else {
                     AdvancedSolarPanels.log.warn("Skipping line {} as it is has the wrong format (expected length 3, found {})", lineNumber, recipe.parts.length);
                  }
               }
            }
         }
      } catch (IOException var13) {
         AdvancedSolarPanels.log.fatal("RIP MT Config!", var13);
         throw new RuntimeException("Fatal error reading Molecular Transformer recipe file", var13);
      } finally {
         IOUtils.closeQuietly(reader);
         IOUtils.closeQuietly(stream);
      }

      MTRecipes = (MTRecipe[])recipes.toArray(new MTRecipe[recipes.size()]);
   }

   private static void fillDefault(File config) {
      FileOutputStream stream = null;
      BufferedWriter writer = null;

      try {
         config.createNewFile();
         stream = new FileOutputStream(config);
         writer = new BufferedWriter(new OutputStreamWriter(stream));
         write(writer, "##################################################################################################");
         write(writer, "#                        AdvancedSolarPanels Molecular Transformer Recipes                       #");
         write(writer, "##################################################################################################");
         write(writer, "# Format of recipe: \"inputItem*stackSize;outputItem*outputStackSize;energy\"                      #");
         write(writer, "# InputItem (and outputItem) format:                                                             #");
         write(writer, "# \"OreDict:forgeOreDictName\" or \"minecraft:item_name@meta\" or \"modID:item_name@meta\"             #");
         write(writer, "# New line = new recipe.                                                                         #");
         write(writer, "# Add \"#\" before line to skip parsing line/recipe                                                #");
         write(writer, "##################################################################################################");
         writer.write("version=2.0" + NEW_LINE);
         write(writer, "##################################################################################################");
         write(writer, "minecraft:skull@1; minecraft:nether_star; 250000000");
         write(writer, "minecraft:iron_ingot@*; ic2:misc_resource#iridium_ore; 9000000");
         write(writer, "minecraft:netherrack@*; minecraft:gunpowder*2; 70000");
         write(writer, "minecraft:sand@*; minecraft:gravel; 50000");
         write(writer, "minecraft:dirt@*; minecraft:clay; 50000");
         write(writer, "minecraft:coal@1; minecraft:coal@0; 60000");
         write(writer, "minecraft:glowstone_dust@*; advanced_solar_panels:crafting@1; 1000000");
         write(writer, "minecraft:glowstone@*; advanced_solar_panels:crafting@0; 9000000");
         write(writer, "minecraft:wool@4; minecraft:glowstone; 500000");
         write(writer, "minecraft:wool@11; minecraft:lapis_block; 500000");
         write(writer, "minecraft:wool@14; minecraft:redstone_block; 500000");
         write(writer, "minecraft:dye@4; OreDict:gemSapphire; 5000000");
         write(writer, "minecraft:redstone@*; OreDict:gemRuby; 5000000");
         write(writer, "minecraft:coal@0; ic2:crafting#industrial_diamond; 9000000");
         write(writer, "ic2:crafting#industrial_diamond; minecraft:diamond; 1000000");
         write(writer, "OreDict:dustTitanium; OreDict:dustChrome; 500000");
         write(writer, "OreDict:ingotTitanium; OreDict:ingotChrome; 500000");
         write(writer, "OreDict:gemNetherQuartz; OreDict:gemCertusQuartz; 500000");
         write(writer, "OreDict:ingotCopper; OreDict:ingotNickel; 300000");
         write(writer, "OreDict:ingotTin; OreDict:ingotSilver; 500000");
         write(writer, "OreDict:ingotSilver; OreDict:ingotGold; 500000");
         write(writer, "OreDict:ingotGold; OreDict:ingotPlatinum; 9000000");
      } catch (IOException var7) {
         AdvancedSolarPanels.log.fatal("RIP MT Config!", var7);
         throw new RuntimeException("Fatal error writing Molecular Transformer recipe file", var7);
      } finally {
         IOUtils.closeQuietly(writer);
         IOUtils.closeQuietly(stream);
      }

   }

   private static void write(BufferedWriter writer, String line) throws IOException {
      writer.write(line);
      writer.newLine();
   }
}
