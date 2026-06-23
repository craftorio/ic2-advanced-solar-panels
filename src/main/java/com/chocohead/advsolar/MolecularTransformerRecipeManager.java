package com.chocohead.advsolar;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.core.init.MainConfig;
import ic2.core.recipe.MachineRecipeHelper;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

class MolecularTransformerRecipeManager extends MachineRecipeHelper<IMolecularTransformerRecipeManager.Input, ItemStack> implements IMolecularTransformerRecipeManager {
   static void showError(String message) {
      if (MainConfig.ignoreInvalidRecipes) {
         AdvancedSolarPanels.log.warn(message);
      } else {
         throw new RuntimeException(message);
      }
   }

   public boolean addRecipe(IRecipeInput input, int totalEU, ItemStack output, boolean replace) {
      return this.addRecipe(new IMolecularTransformerRecipeManager.Input(input, totalEU), output, (NBTTagCompound)null, replace);
   }

   public boolean addRecipe(IMolecularTransformerRecipeManager.Input input, ItemStack output, NBTTagCompound metadata, boolean replace) {
      if (input == null) {
         showError("Invalid recipe input: null");
         return false;
      } else if (StackUtil.isEmpty(output)) {
         showError("Invalid recipe output: " + StackUtil.toStringSafe(output));
         return false;
      } else if (!input.input.matches(output) || metadata != null && metadata.hasKey("ignoreSameInputOutput")) {
         for(ItemStack is : input.input.getInputs()) {
            MachineRecipe<IMolecularTransformerRecipeManager.Input, ItemStack> recipe = this.getRecipe(is);
            if (recipe != null) {
               if (!replace) {
                  return false;
               }

               while(true) {
                  this.recipes.remove(input);
                  this.removeCachedRecipes(input);
                  recipe = this.getRecipe(is);
                  if (recipe == null) {
                     break;
                  }
               }
            }
         }

         MachineRecipe<IMolecularTransformerRecipeManager.Input, ItemStack> recipe = new MachineRecipe(input, output.copy(), metadata);
         this.recipes.put(input, recipe);
         this.addToCache(recipe);
         return true;
      } else {
         showError("The output ItemStack " + StackUtil.toStringSafe(output) + " is the same as the recipe input " + input + ".");
         return false;
      }
   }

   protected IRecipeInput getForInput(IMolecularTransformerRecipeManager.Input input) {
      return input.input;
   }

   public int getTotalEUNeeded(ItemStack input) {
      IMolecularTransformerRecipeManager.Input recipe = (IMolecularTransformerRecipeManager.Input)this.getRecipe(input).getInput();
      return recipe == null ? -1 : recipe.totalEU;
   }
}
