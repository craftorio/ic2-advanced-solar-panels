package com.chocohead.advsolar;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;

public interface IMolecularTransformerRecipeManager extends IMachineRecipeManager<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack> {
   IMolecularTransformerRecipeManager RECIPES = new MolecularTransformerRecipeManager();

   boolean addRecipe(IRecipeInput var1, int var2, ItemStack var3, boolean var4);

   int getTotalEUNeeded(ItemStack var1);

   public static final class Input {
      public final IRecipeInput input;
      public final int totalEU;

      public Input(IRecipeInput input, int totalEU) {
         this.input = input;
         this.totalEU = totalEU;
      }

      public String toString() {
         return "MTInput<" + this.input + ", " + this.totalEU + '>';
      }
   }
}
