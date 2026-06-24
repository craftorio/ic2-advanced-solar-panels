package com.chocohead.advsolar.recipe;

import com.chocohead.advsolar.AdvSolarBlocks;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Registers the Molecular Transformer recipe table. The classic mod shipped an editable config file
 * of recipes (many referencing IC2 items and the old ore dictionary); this port defines a
 * representative, vanilla-mappable subset in code. See PORTING.md.
 */
public final class AdvSolarRecipes {
	private AdvSolarRecipes() {
	}

	public static void registerRecipes() {
		add(Items.WITHER_SKELETON_SKULL, Items.NETHER_STAR, 250_000_000);
		add(Items.NETHERRACK, new ItemStack(Items.GUNPOWDER, 2), 70_000);
		add(Items.SAND, Items.GRAVEL, 50_000);
		add(Items.DIRT, Items.CLAY_BALL, 50_000);
		add(Items.CHARCOAL, Items.COAL, 60_000);
		add(Items.YELLOW_WOOL, Items.GLOWSTONE, 500_000);
		add(Items.BLUE_WOOL, Items.LAPIS_BLOCK, 500_000);
		add(Items.RED_WOOL, Items.REDSTONE_BLOCK, 500_000);
		add(Items.GLOWSTONE_DUST, crafting("sunnarium_part"), 1_000_000);
		add(Items.GLOWSTONE, crafting("sunnarium"), 9_000_000);
		add(Items.COAL, Items.DIAMOND, 9_000_000);
		add(Items.GOLD_INGOT, crafting("iridium_ingot"), 9_000_000);
	}

	private static Item crafting(String name) {
		return AdvSolarBlocks.CRAFTING.get(name).get();
	}

	private static void add(ItemLike input, ItemLike output, int totalEU) {
		add(input, new ItemStack(output), totalEU);
	}

	private static void add(ItemLike input, ItemStack output, int totalEU) {
		MolecularTransformerRecipes.add(new MTRecipe(Ingredient.of(input), output, totalEU));
	}
}
