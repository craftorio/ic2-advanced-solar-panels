package com.chocohead.advsolar;

import org.slf4j.Logger;

import com.chocohead.advsolar.recipe.AdvSolarRecipes;

import com.mojang.logging.LogUtils;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Advanced Solar Panels — advanced/hybrid/ultimate/quantum solar panels, the molecular
 * transformer, the quantum generator and chargeable solar helmets. Ported from the classic
 * 1.12.2 IC2 addon to Minecraft 1.20.1 / IC2: Refactored.
 */
@Mod(AdvancedSolarPanels.MODID)
public final class AdvancedSolarPanels {
	public static final String MODID = "advanced_solar_panels";
	public static final Logger LOGGER = LogUtils.getLogger();

	public AdvancedSolarPanels() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		AdvSolarBlocks.register(modBus);
		modBus.addListener(this::commonSetup);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(AdvSolarRecipes::registerRecipes);
		LOGGER.info("Advanced Solar Panels {} loaded.", MODID);
	}
}
