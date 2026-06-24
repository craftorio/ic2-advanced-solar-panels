package com.chocohead.advsolar.client;

import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.gui.ProgressBars;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Client-only setup: registers this addon's custom gauge styles for the dynamic GUIs. */
@Mod.EventBusSubscriber(modid = AdvancedSolarPanels.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AdvSolarClient {
	private AdvSolarClient() {
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(ProgressBars::addStyles);
	}
}
