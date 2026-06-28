package com.chocohead.advsolar.te;

import com.chocohead.advsolar.SolarPanelType;
import com.chocohead.advsolar.gui.GuiDefs;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.tileentity.TileEntityInventory;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.GuiSynced;
import ic2.core.ref.Ic2ScreenHandlers;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base for the four Advanced Solar Panels. A solar panel is an IC2 energy source that fills an
 * internal buffer from sunlight: full {@code dayPower} EU/t in daylight, reduced {@code nightPower}
 * at night or in the rain, and emits at the configured tier. Rebuilt on top of IC2: Refactored's
 * {@link TileEntityBaseGenerator} for 1.20.1.
 */
public abstract class TileEntitySolarPanel extends TileEntityBaseGenerator implements IHasGui, IGuiValueProvider {
	public static final int STATE_NONE = 0;
	public static final int STATE_NIGHT = 1;
	public static final int STATE_DAY = 2;

	protected final int dayPower;
	protected final int nightPower;
	protected final int tier;
	protected final int maxStorage;
	@GuiSynced
	public int generationState = STATE_NONE;
	private int ticker;
	private boolean hasSky;
	private boolean canRain;

	protected TileEntitySolarPanel(BlockEntityType<? extends TileEntitySolarPanel> type, BlockPos pos, BlockState state,
			SolarPanelType panelType) {
		this(type, pos, state, panelType.dayPower, panelType.nightPower, panelType.maxStorage, panelType.tier);
	}

	protected TileEntitySolarPanel(BlockEntityType<? extends TileEntitySolarPanel> type, BlockPos pos, BlockState state,
			int dayPower, int nightPower, int maxStorage, int tier) {
		super(type, pos, state, dayPower, tier, maxStorage);
		this.dayPower = dayPower;
		this.nightPower = nightPower;
		this.maxStorage = maxStorage;
		this.tier = tier;
	}

	@Override
	protected void onLoaded() {
		super.onLoaded();
		if (!this.getLevel().isClientSide) {
			this.hasSky = this.getLevel().dimensionType().hasSkyLight();
			this.canRain = this.getLevel().getBiome(this.worldPosition).value().getPrecipitationAt(this.worldPosition)
					!= net.minecraft.world.level.biome.Biome.Precipitation.NONE;
		}
	}

	@Override
	public boolean gainEnergy() {
		if (this.ticker++ % 128 == 0) {
			this.checkTheSky();
		}

		int power = switch (this.generationState) {
			case STATE_DAY -> this.dayPower;
			case STATE_NIGHT -> this.nightPower;
			default -> 0;
		};

		if (power > 0 && this.energy.getFreeEnergy() > 0.0D) {
			this.energy.addEnergy(power);
			return true;
		}

		return false;
	}

	private void checkTheSky() {
		Level world = this.getLevel();
		if (this.hasSky && world.canSeeSky(this.worldPosition.above())) {
			if (!world.isDay() || this.canRain && (world.isRaining() || world.isThundering())) {
				this.generationState = STATE_NIGHT;
			} else {
				this.generationState = STATE_DAY;
			}
		} else {
			this.generationState = STATE_NONE;
		}
	}

	@Override
	public boolean gainFuel() {
		return false;
	}

	@Override
	public boolean needsFuel() {
		return false;
	}

	@Override
	protected boolean delayActiveUpdate() {
		return true;
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
			double capacity = this.energy.getCapacity();
			return capacity > 0.0D ? this.energy.getEnergy() / capacity : 0.0D;
		}

		throw new IllegalArgumentException("Unknown GUI value: " + name);
	}

	@Override
	public boolean getGuiState(String name) {
		if ("sunlight".equals(name)) {
			return this.generationState == STATE_DAY;
		}
		if ("moonlight".equals(name)) {
			return this.generationState == STATE_NIGHT;
		}

		return super.getGuiState(name);
	}

	public int getStorage() {
		return (int) this.energy.getEnergy();
	}

	public int getMaxStorage() {
		return this.maxStorage;
	}

	public String getMaxOutput() {
		return String.format("%s %d %s", tr("advanced_solar_panels.gui.maxOutput"),
				(int) ic2.api.energy.EnergyNet.instance.getPowerFromTier(this.tier), tr("ic2.generic.text.EUt"));
	}

	public String getOutput() {
		int power = switch (this.generationState) {
			case STATE_DAY -> this.dayPower;
			case STATE_NIGHT -> this.nightPower;
			default -> 0;
		};
		return String.format("%s %d %s", tr("advanced_solar_panels.gui.generating"), power, tr("ic2.generic.text.EUt"));
	}

	private static String tr(String key) {
		return Component.translatable(key).getString();
	}
}
