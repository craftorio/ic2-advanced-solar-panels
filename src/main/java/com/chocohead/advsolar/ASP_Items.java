package com.chocohead.advsolar;

import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import com.chocohead.advsolar.items.ItemCraftingThings;
import com.chocohead.advsolar.items.ItemDoubleSlab;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.IMultiItem;
import ic2.core.ref.ItemName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum ASP_Items {
   ADVANCED_SOLAR_HELMET,
   HYBRID_SOLAR_HELMET,
   ULTIMATE_HYBRID_SOLAR_HELMET,
   CRAFTING,
   DOUBLE_STONE_SLAB;

   private Item instance;

   public <T extends Item & IItemModelProvider> T getInstance() {
      return (T)this.instance;
   }

   public <T extends Enum<T> & IIdProvider> ItemStack getItemStack(T variant) {
      if (this.instance == null) {
         return null;
      } else if (this.instance instanceof IMultiItem) {
         IMultiItem<T> multiItem = (IMultiItem)this.instance;
         return multiItem.getItemStack(variant);
      } else if (variant == null) {
         return new ItemStack(this.instance);
      } else {
         throw new IllegalArgumentException("Not applicable");
      }
   }

   public <T extends Item & IItemModelProvider> void setInstance(T instance) {
      if (this.instance != null) {
         throw new IllegalStateException("Duplicate instances!");
      } else {
         this.instance = instance;
      }
   }

   static void buildItems(Side side) {
      ADVANCED_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED));
      HYBRID_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.HYBRID));
      ULTIMATE_HYBRID_SOLAR_HELMET.setInstance(new ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes.ULTIMATE));
      CRAFTING.setInstance(new ItemCraftingThings());
      DOUBLE_STONE_SLAB.setInstance(new ItemDoubleSlab());
      if (side == Side.CLIENT) {
         doModelGuf();
      }

   }

   @SideOnly(Side.CLIENT)
   private static void doModelGuf() {
      for(ASP_Items item : values()) {
         ((IItemModelProvider)item.getInstance()).registerModels((ItemName)null);
      }

   }
}
