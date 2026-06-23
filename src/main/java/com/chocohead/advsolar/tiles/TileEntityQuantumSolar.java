package com.chocohead.advsolar.tiles;

public class TileEntityQuantumSolar extends TileEntitySolarPanel {
   public static TileEntitySolarPanel.SolarConfig settings;

   public TileEntityQuantumSolar() {
      super(settings);
   }
}
