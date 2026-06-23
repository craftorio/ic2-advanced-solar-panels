package com.chocohead.advsolar.items;

import ic2.core.block.state.IIdProvider;
import ic2.core.init.BlocksItems;
import ic2.core.item.ItemMulti;
import ic2.core.ref.ItemName;
import java.util.Locale;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCraftingThings extends ItemMulti<ItemCraftingThings.CraftingTypes> {
   protected static final String NAME = "crafting";

   public ItemCraftingThings() {
      super((ItemName)null, ItemCraftingThings.CraftingTypes.class);
      ((ItemCraftingThings)BlocksItems.registerItem(this, new ResourceLocation("advanced_solar_panels", "crafting"))).setTranslationKey("crafting");
   }

   @SideOnly(Side.CLIENT)
   protected void registerModel(int meta, ItemName name, String extraName) {
      ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation("advanced_solar_panels:crafting/" + ItemCraftingThings.CraftingTypes.getFromID(meta).getName(), (String)null));
   }

   public String getTranslationKey() {
      return "advanced_solar_panels." + super.getTranslationKey().substring(4);
   }

   public static enum CraftingTypes implements IIdProvider {
      SUNNARIUM(0),
      SUNNARIUM_PART(1),
      SUNNARIUM_ALLOY(2),
      IRRADIANT_URANIUM(3),
      ENRICHED_SUNNARIUM(4),
      ENRICHED_SUNNARIUM_ALLOY(5),
      IRRADIANT_GLASS_PANE(6),
      IRIDIUM_IRON_PLATE(7),
      REINFORCED_IRIDIUM_IRON_PLATE(8),
      IRRADIANT_REINFORCED_PLATE(9),
      IRIDIUM_INGOT(10),
      URANIUM_INGOT(11),
      MT_CORE(12),
      QUANTUM_CORE(13);

      private final String name = this.name().toLowerCase(Locale.ENGLISH);
      private final int ID;
      private static final ItemCraftingThings.CraftingTypes[] VALUES = values();

      private CraftingTypes(int ID) {
         this.ID = ID;
      }

      public String getName() {
         return this.name;
      }

      public int getId() {
         return this.ID;
      }

      public static ItemCraftingThings.CraftingTypes getFromID(int ID) {
         return VALUES[ID % VALUES.length];
      }
   }
}
