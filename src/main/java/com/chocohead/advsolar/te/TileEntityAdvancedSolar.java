package com.chocohead.advsolar.te;

import com.chocohead.advsolar.AdvSolarBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityAdvancedSolar extends TileEntitySolarPanel {
	public TileEntityAdvancedSolar(BlockPos pos, BlockState state) {
		// dayPower, nightPower, storage, tier — classic Advanced Solar Panel defaults.
		super(AdvSolarBlocks.BE_ADVANCED_SOLAR_PANEL.get(), pos, state, 8, 1, 32000, 1);
	}
}
