package com.chocohead.advsolar.te;

import com.chocohead.advsolar.AdvSolarBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityQuantumSolar extends TileEntitySolarPanel {
	public TileEntityQuantumSolar(BlockPos pos, BlockState state) {
		super(AdvSolarBlocks.BE_QUANTUM_SOLAR_PANEL.get(), pos, state, 4096, 2048, 10000000, 5);
	}
}
