package com.chocohead.advsolar.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * A single Molecular Transformer recipe: an input ingredient that is consumed one at a time, the
 * resulting output stack, and the total EU that must be poured in to complete one conversion.
 */
public final class MTRecipe {
	public final Ingredient input;
	public final ItemStack output;
	public final int totalEU;

	public MTRecipe(Ingredient input, ItemStack output, int totalEU) {
		this.input = input;
		this.output = output;
		this.totalEU = totalEU;
	}

	public boolean matches(ItemStack stack) {
		return !stack.isEmpty() && this.input.test(stack);
	}
}
