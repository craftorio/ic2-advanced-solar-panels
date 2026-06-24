package com.chocohead.advsolar.te;

import com.chocohead.advsolar.AdvSolarBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityUltimateHybridSolar extends TileEntitySolarPanel {
	public TileEntityUltimateHybridSolar(BlockPos pos, BlockState state) {
		super(AdvSolarBlocks.BE_ULTIMATE_SOLAR_PANEL.get(), pos, state, 512, 64, 1000000, 3);
	}
}
