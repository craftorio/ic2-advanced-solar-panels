package com.chocohead.advsolar.tiles;

public class TileEntityAdvancedSolar extends TileEntitySolarPanel {
   public static TileEntitySolarPanel.SolarConfig settings;

   public TileEntityAdvancedSolar() {
      super(settings);
   }
}
