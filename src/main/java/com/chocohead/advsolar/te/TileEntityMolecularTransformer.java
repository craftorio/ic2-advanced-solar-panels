package com.chocohead.advsolar.te;

import com.chocohead.advsolar.AdvSolarBlocks;
import com.chocohead.advsolar.gui.GuiDefs;
import com.chocohead.advsolar.recipe.MTRecipe;
import com.chocohead.advsolar.recipe.MolecularTransformerRecipes;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.tileentity.TileEntityInventory;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.GuiSynced;
import ic2.core.ref.Ic2ScreenHandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Molecular Transformer: an extreme-tier energy sink that converts one item into another by
 * pouring a recipe-defined total amount of EU into it. Rebuilt on {@link TileEntityElectricMachine}
 * (an IC2 energy-sink buffer) for 1.20.1, with a code-defined recipe table.
 */
public class TileEntityMolecularTransformer extends TileEntityElectricMachine implements IHasGui, IGuiValueProvider {
	private static final int BUFFER = 100000;
	private static final int SINK_TIER = 14;

	public final InvSlot inputSlot = new InvSlot(this, "input", InvSlot.Access.IO, 1) {
		@Override
		public boolean accepts(ItemStack stack) {
			return MolecularTransformerRecipes.find(stack) != null;
		}
	};
	public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 1);

	@GuiSynced
	public ItemStack currentOutput = ItemStack.EMPTY;
	@GuiSynced
	public int totalEU;
	@GuiSynced
	public double energyUsed;
	@GuiSynced
	public double lastEnergyIn;

	public TileEntityMolecularTransformer(BlockPos pos, BlockState state) {
		super(AdvSolarBlocks.BE_MOLECULAR_TRANSFORMER.get(), pos, state, BUFFER, SINK_TIER);
		this.comparator.setUpdate(() -> this.totalEU == 0 ? 0 : (int) (this.energyUsed * 15.0D / this.totalEU));
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		this.energyUsed = nbt.getDouble("energyUsed");
		this.totalEU = nbt.getInt("totalEU");
		this.currentOutput = ItemStack.of(nbt.getCompound("output"));
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putDouble("energyUsed", this.energyUsed);
		nbt.putInt("totalEU", this.totalEU);
		if (!this.currentOutput.isEmpty()) {
			nbt.put("output", this.currentOutput.save(new CompoundTag()));
		}
	}

	@Override
	protected void updateEntityServer() {
		super.updateEntityServer();
		boolean active = false;
		double energyThisTick = 0.0D;

		if (this.currentOutput.isEmpty() && !this.inputSlot.isEmpty()) {
			MTRecipe recipe = MolecularTransformerRecipes.find(this.inputSlot.get(0));
			if (recipe != null && this.outputSlot.canAdd(recipe.output)) {
				this.currentOutput = recipe.output.copy();
				this.totalEU = recipe.totalEU;
				this.energyUsed = 0.0D;
				consumeOneInput();
			}
		}

		if (!this.currentOutput.isEmpty()) {
			double need = this.totalEU - this.energyUsed;
			double use = Math.min(this.energy.getEnergy(), need);
			if (use > 0.0D && this.energy.useEnergy(use)) {
				this.energyUsed += use;
				energyThisTick = use;
				active = true;
			}

			if (this.energyUsed >= this.totalEU) {
				this.outputSlot.add(this.currentOutput);
				this.currentOutput = ItemStack.EMPTY;
				this.totalEU = 0;
				this.energyUsed = 0.0D;
				super.setChanged();
			}
		}

		this.lastEnergyIn = energyThisTick;

		if (active) {
			if (!this.getActive()) {
				this.activate(false);
			}
		} else if (this.getActive()) {
			this.shutdown(false);
		}
	}

	private void consumeOneInput() {
		ItemStack in = this.inputSlot.get(0).copy();
		in.shrink(1);
		this.inputSlot.put(0, in.isEmpty() ? ItemStack.EMPTY : in);
	}

	@Override
	public ContainerBase<?> createServerScreenHandler(int syncId, Player player) {
		return DynamicContainer.create(Ic2ScreenHandlers.DYNAMIC_BE, syncId, player.getInventory(), (TileEntityInventory) this, GuiDefs.parse(this));
	}

	@Override
	public ContainerBase<?> createClientScreenHandler(int syncId, Inventory inventory, GrowingBuffer data) {
		return DynamicContainer.create(Ic2ScreenHandlers.DYNAMIC_BE, syncId, inventory, (TileEntityInventory) this, GuiDefs.parse(this));
	}

	@Override
	public double getGuiValue(String name) {
		if ("progress".equals(name)) {
			return this.totalEU == 0 ? 0.0D : this.energyUsed / this.totalEU;
		}

		throw new IllegalArgumentException("Unknown GUI value: " + name);
	}

	public String getInput() {
		return this.inputSlot.isEmpty() ? "" : this.inputSlot.get(0).getHoverName().getString();
	}

	public String getOutput() {
		return this.currentOutput.isEmpty() ? "" : this.currentOutput.getHoverName().getString();
	}

	public String getEnergyNeeded() {
		return this.totalEU == 0 ? "" : String.format("%,d %s", this.totalEU, Component.translatable("ic2.generic.text.EU").getString());
	}

	public String getEU() {
		return this.currentOutput.isEmpty() ? "" : String.format("%,.0f %s", this.lastEnergyIn, Component.translatable("ic2.generic.text.EUt").getString());
	}

	public String getPercent() {
		return this.totalEU == 0 ? "" : String.format("%,.0f%%", this.energyUsed * 100.0D / this.totalEU);
	}
}
