package com.chocohead.advsolar.tiles;

public class TileEntityHybridSolar extends TileEntitySolarPanel {
   public static TileEntitySolarPanel.SolarConfig settings;

   public TileEntityHybridSolar() {
      super(settings);
   }
}
