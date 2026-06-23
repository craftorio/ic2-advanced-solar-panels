package com.chocohead.advsolar.gui;

import java.util.Locale;

import ic2.core.gui.Gauge;
import ic2.core.gui.Gauge.GaugePropertyBuilder.GaugeOrientation;
import ic2.core.gui.Gauge.GaugeStyle;

import net.minecraft.resources.ResourceLocation;

/**
 * The custom gauge styles used by this addon's guidef GUIs (the molecular transformer progress arrow
 * and the solar panel energy bar). Registered during client setup via {@link #addStyles()}.
 */
public enum ProgressBars implements Gauge.IGaugeStyle {
	PROGRESS_MOLECULAR_TRANSFORMER(new Gauge.GaugePropertyBuilder(221, 7, 10, 15, GaugeOrientation.Down)
			.withTexture(new ResourceLocation("advanced_solar_panels", "textures/gui/molecular_transformer.png"))),
	PROGRESS_JEI_MOLECULAR_TRANSFORMER(new Gauge.GaugePropertyBuilder(176, 2, 12, 11, GaugeOrientation.Down)
			.withTexture(new ResourceLocation("advanced_solar_panels", "textures/gui/molecular_transformer_jei.png"))),
	ENERGY_ADVANCED_SOLAR(new Gauge.GaugePropertyBuilder(195, 0, 24, 14, GaugeOrientation.Right)
			.withTexture(new ResourceLocation("advanced_solar_panels", "textures/gui/advanced_solar_panel.png")));

	private final String name = this.name().toLowerCase(Locale.ENGLISH);
	private final Gauge.GaugeProperties properties;

	ProgressBars(Gauge.GaugePropertyBuilder properties) {
		this.properties = properties.build();
	}

	@Override
	public Gauge.GaugeProperties getProperties() {
		return this.properties;
	}

	public static void addStyles() {
		for (ProgressBars bar : values()) {
			GaugeStyle.addStyle(bar.name, bar);
		}
	}
}
