package com.chocohead.advsolar.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

/**
 * The Molecular Transformer recipe table. In the 1.12.2 mod these were loaded from an editable
 * {@code _MTRecipes} config file; on 1.20.1 they are defined in code (see {@link AdvSolarRecipes}).
 */
public final class MolecularTransformerRecipes {
	private static final List<MTRecipe> RECIPES = new ArrayList<>();

	private MolecularTransformerRecipes() {
	}

	public static void add(MTRecipe recipe) {
		RECIPES.add(recipe);
	}

	public static MTRecipe find(ItemStack input) {
		for (MTRecipe recipe : RECIPES) {
			if (recipe.matches(input)) {
				return recipe;
			}
		}

		return null;
	}

	public static List<MTRecipe> all() {
		return RECIPES;
	}
}
