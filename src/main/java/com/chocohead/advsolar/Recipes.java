package com.chocohead.advsolar;

import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import com.chocohead.advsolar.items.ItemCraftingThings;
import com.chocohead.advsolar.tiles.TEs;
import ic2.api.item.IC2Items;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.IRecipeInputFactory;
import ic2.core.init.Rezepte;
import ic2.core.recipe.ArmorDyeingRecipe;
import ic2.core.recipe.ColourCarryingRecipe;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.text.ParseException;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;

final class Recipes {
   static void addCraftingRecipes() {
      addRecipe(new ArmorDyeingRecipe(new ArmorDyeingRecipe.RecipeInputClass(ItemArmourSolarHelmet.class) {
         protected boolean matches(Item item) {
            return super.matches(item) && ((ItemArmourSolarHelmet)item).canBeDyed();
         }
      }));
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRIDIUM_IRON_PLATE), "III", "IPI", "III", 'I', "plateIron", 'P', "ingotIridium");
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.REINFORCED_IRIDIUM_IRON_PLATE), "ACA", "CIC", "ACA", 'A', IC2Items.getItem("crafting", "alloy"), 'C', IC2Items.getItem("crafting", "carbon_plate"), 'I', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRIDIUM_IRON_PLATE));
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_REINFORCED_PLATE), "RSR", "LIL", "RDR", 'R', Items.REDSTONE, 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_PART), 'L', new ItemStack(Items.DYE, 1, 4), 'I', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.REINFORCED_IRIDIUM_IRON_PLATE), 'D', Items.DIAMOND);
      if (Configs.hardRecipes) {
         if (Configs.canCraftASP) {
            if (Configs.easyASPRecipe) {
               addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), "PPP", "ASA", "CMC", 'P', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_GLASS_PANE), 'A', IC2Items.getItem("crafting", "alloy"), 'S', IC2Items.getItem("te", "solar_generator"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'M', IC2Items.getItem("resource", "advanced_machine"));
            } else {
               addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), "PPP", "ASA", "CIC", 'P', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_GLASS_PANE), 'A', IC2Items.getItem("crafting", "alloy"), 'S', IC2Items.getItem("te", "solar_generator"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'I', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_REINFORCED_PLATE));
            }
         }

         if (Configs.canCraftHSP) {
            addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.hybrid_solar_panel), "PLP", "IAI", "CSC", 'P', IC2Items.getItem("crafting", "carbon_plate"), 'L', Blocks.LAPIS_BLOCK, 'I', IC2Items.getItem("crafting", "iridium"), 'A', AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM));
         }

         if (Configs.canCraftUHSP) {
            addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel), " L ", "CSC", "ECE", 'L', Blocks.LAPIS_BLOCK, 'C', IC2Items.getItem("crafting", "coal_chunk"), 'S', AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), 'E', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM_ALLOY));
         }
      } else {
         if (Configs.canCraftASP) {
            if (Configs.easyASPRecipe) {
               addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), "GGG", "ASA", "CMC", 'G', IC2Items.getItem("glass", "reinforced"), 'A', IC2Items.getItem("crafting", "alloy"), 'S', IC2Items.getItem("te", "solar_generator"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'M', IC2Items.getItem("resource", "advanced_machine"));
            } else {
               addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), "GGG", "ASA", "CPC", 'G', IC2Items.getItem("glass", "reinforced"), 'A', IC2Items.getItem("crafting", "alloy"), 'S', IC2Items.getItem("te", "solar_generator"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'P', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_REINFORCED_PLATE));
            }
         }

         if (Configs.canCraftHSP) {
            addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.hybrid_solar_panel), "PLP", "IAI", "CSC", 'P', IC2Items.getItem("crafting", "carbon_plate"), 'L', Blocks.LAPIS_BLOCK, 'I', IC2Items.getItem("crafting", "iridium"), 'A', AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM));
         }

         if (Configs.canCraftUHSP) {
            addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel), " L ", "CSC", "ECE", 'L', Blocks.LAPIS_BLOCK, 'C', IC2Items.getItem("crafting", "coal_chunk"), 'S', AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), 'E', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_ALLOY));
         }
      }

      if (Configs.canCraftUHSP) {
         addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel), "SSS", "SCS", "SSS", 'S', AdvancedSolarPanels.machines.getItemStack(TEs.hybrid_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"));
         addShapelessRecipe(StackUtil.setSize(AdvancedSolarPanels.machines.getItemStack(TEs.hybrid_solar_panel), 8), AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel));
      }

      if (Configs.canCraftQSP) {
         addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.QUANTUM_CORE), "ANA", "NEN", "ANA", 'A', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM_ALLOY), 'N', Items.NETHER_STAR, 'E', Items.ENDER_EYE);
         addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.quantum_solar_panel), "SSS", "SQS", "SSS", 'S', AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel), 'Q', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.QUANTUM_CORE));
      }

      if (Configs.canCraftASH) {
         addShapedRecipe(new ItemStack(ASP_Items.ADVANCED_SOLAR_HELMET.getInstance()), " S ", "CNC", "GTG", 'S', AdvancedSolarPanels.machines.getItemStack(TEs.advanced_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'N', IC2Items.getItem("nano_helmet"), 'G', IC2Items.getItem("cable", "type:gold,insulation:2"), 'T', IC2Items.getItem("te", "lv_transformer"));
      }

      if (Configs.canCraftHSH) {
         addShapedColourRecipe(new ItemStack(ASP_Items.HYBRID_SOLAR_HELMET.getInstance()), " S ", "CQC", "GTG", 'S', AdvancedSolarPanels.machines.getItemStack(TEs.hybrid_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'Q', IC2Items.getItem("quantum_helmet"), 'G', IC2Items.getItem("cable", "type:glass,insulation:0"), 'T', IC2Items.getItem("te", "hv_transformer"));
      }

      if (Configs.canCraftUHSH) {
         addShapedColourRecipe(new ItemStack(ASP_Items.ULTIMATE_HYBRID_SOLAR_HELMET.getInstance()), " S ", "CQC", "GTG", 'S', AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'Q', IC2Items.getItem("quantum_helmet"), 'G', IC2Items.getItem("cable", "type:glass,insulation:0"), 'T', IC2Items.getItem("te", "hv_transformer"));
         addShapelessRecipe(new ItemStack(ASP_Items.ULTIMATE_HYBRID_SOLAR_HELMET.getInstance()), ASP_Items.HYBRID_SOLAR_HELMET.getInstance(), AdvancedSolarPanels.machines.getItemStack(TEs.ultimate_solar_panel));
      }

      if (Configs.canCraftMT) {
         addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.MT_CORE), "PRP", "P P", "PRP", 'P', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_GLASS_PANE), 'R', IC2Items.getItem("thick_neutron_reflector"));
         addShapedRecipe(AdvancedSolarPanels.machines.getItemStack(TEs.molecular_transformer), "MTM", "CcC", "MTM", 'M', IC2Items.getItem("resource", "advanced_machine"), 'T', IC2Items.getItem("te", "ev_transformer"), 'C', IC2Items.getItem("crafting", "advanced_circuit"), 'c', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.MT_CORE));
      }

      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_URANIUM), " G ", "GUG", " G ", 'G', Items.GLOWSTONE_DUST, 'U', "ingotUranium");
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_GLASS_PANE), "GGG", "UDU", "GGG", 'G', IC2Items.getItem("glass", "reinforced"), 'U', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_URANIUM), 'D', Items.GLOWSTONE_DUST);
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM), "UUU", "USU", "UUU", 'U', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRRADIANT_URANIUM), 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM));
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM_ALLOY), " S ", "SAS", " S ", 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.ENRICHED_SUNNARIUM), 'A', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_ALLOY));
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_ALLOY), "III", "ISI", "III", 'I', IC2Items.getItem("crafting", "iridium"), 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM));
      addShapedRecipe(ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM), "SSS", "SSS", "SSS", 'S', ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_PART));
      if (Configs.canCraftDoubleSlabs) {
         addShapelessRecipe(new ItemStack(ASP_Items.DOUBLE_STONE_SLAB.getInstance(), 1, 0), new ItemStack(Blocks.STONE_SLAB, 1, 0), new ItemStack(Blocks.STONE_SLAB, 1, 0));
      }

   }

   private static void addShapedRecipe(ItemStack output, Object... inputs) {
      ic2.api.recipe.Recipes.advRecipes.addRecipe(output, inputs);
   }

   private static void addShapedColourRecipe(ItemStack output, Object... inputs) {
      ColourCarryingRecipe.addAndRegister(output, inputs);
   }

   private static void addShapelessRecipe(ItemStack output, Object... inputs) {
      ic2.api.recipe.Recipes.advRecipes.addShapelessRecipe(output, inputs);
   }

   private static void addRecipe(IRecipe recipe) {
      ModContainer us = Loader.instance().activeModContainer();
      Loader.instance().getActiveModList().stream().filter((mod) -> "ic2".equals(mod.getModId())).findFirst().ifPresent(Loader.instance()::setActiveModContainer);
      Rezepte.registerRecipe(recipe);
      Loader.instance().setActiveModContainer(us);
   }

   static void addMachineRecipes() {
      IRecipeInputFactory input = ic2.api.recipe.Recipes.inputFactory;
      addCompressorRecipe(input.forStack(IC2Items.getItem("misc_resource", "iridium_ore")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRIDIUM_INGOT));
      addExtrudingRecipe(input.forStack(IC2Items.getItem("crafting", "iridium")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRIDIUM_INGOT));
      addCompressorRecipe(input.forStack(IC2Items.getItem("resource", "uranium_ore")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.URANIUM_INGOT));
      addCompressorRecipe(input.forStack(IC2Items.getItem("crushed", "uranium")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.URANIUM_INGOT));
      addCompressorRecipe(input.forStack(IC2Items.getItem("purified", "uranium")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.URANIUM_INGOT));
      addCompressorRecipe(input.forStack(IC2Items.getItem("nuclear", "uranium")), ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.URANIUM_INGOT));
   }

   private static void addCompressorRecipe(IRecipeInput input, ItemStack output) {
      ic2.api.recipe.Recipes.compressor.addRecipe(input, (NBTTagCompound)null, false, new ItemStack[]{output});
   }

   private static void addExtrudingRecipe(IRecipeInput input, ItemStack output) {
      ic2.api.recipe.Recipes.metalformerExtruding.addRecipe(input, (NBTTagCompound)null, false, new ItemStack[]{output});
   }

   static void addMolecularTransformerRecipes() {
      AdvancedSolarPanels.log.info("Loading Molecular Transformer recipes from file");
      int successes = 0;

      for(MTRecipe recipe : Configs.MTRecipes) {
         try {
            if (decodeLine(recipe.lineNumber, recipe.parts)) {
               ++successes;
            }
         } catch (ParseException var6) {
            AdvancedSolarPanels.log.warn("Skipping line " + recipe.lineNumber + " due to an error parsing", var6);
         }
      }

      AdvancedSolarPanels.log.info("Load complete, successfully loaded {} out of {}.", successes, Configs.MTRecipes.length);
   }

   private static boolean decodeLine(int number, String[] parts) throws ParseException {
      IRecipeInput input = ConfigUtil.asRecipeInputWithAmount(parts[0].trim());
      if (input == null) {
         AdvancedSolarPanels.log.warn("Skipping line {} as the input ({}) cannot be resolved", number, parts[0].trim());
         return false;
      } else {
         ItemStack output = ConfigUtil.asStackWithAmount(parts[1].trim());
         if (output == null) {
            String attempt = parts[1].trim();
            if (attempt.startsWith("OreDict:")) {
               List<ItemStack> potentialOptions = OreDictionary.getOres(attempt.substring(attempt.indexOf(58) + 1).trim());
               if (!potentialOptions.isEmpty()) {
                  output = (ItemStack)potentialOptions.get(0);
                  AdvancedSolarPanels.log.debug("Continued on line {} as the output ({}) could be resolved to {}", number, attempt, output);
               }
            }

            if (output == null) {
               AdvancedSolarPanels.log.warn("Skipping line {} as the output ({}) cannot be resolved", number, attempt);
               return false;
            }
         }

         int energy;
         try {
            energy = Integer.parseInt(parts[2].trim());
         } catch (NumberFormatException var6) {
            AdvancedSolarPanels.log.warn("Skipping line {} as the energy ({}) cannot be resolved to a number", number, parts[2].trim());
            return false;
         }

         if (!IMolecularTransformerRecipeManager.RECIPES.addRecipe(input, energy, output, false)) {
            AdvancedSolarPanels.log.warn("Skipping line {} as the recipe is a duplicate", number);
            return false;
         } else {
            return true;
         }
      }
   }
}
