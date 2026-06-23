package com.chocohead.advsolar.slots;

import ic2.api.energy.tile.IChargingSlot;
import ic2.api.item.ElectricItem;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlot.InvSide;
import net.minecraft.item.ItemStack;

public class InvSlotMultiCharge extends InvSlot implements IChargingSlot {
   public final int tier;

   public InvSlotMultiCharge(TileEntityInventory base, int tier, int slotNumbers, InvSlot.Access access) {
      super(base, "charge", access, slotNumbers, InvSide.TOP);
      this.tier = tier;
   }

   public boolean accepts(ItemStack stack) {
      return ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, this.tier, false, true) > 0.0D;
   }

   public double charge(double amount) {
      if (amount <= 0.0D) {
         throw new IllegalArgumentException("Amount must be > 0.");
      } else {
         double charged = 0.0D;

         for(ItemStack stack : this) {
            if (stack != null) {
               double energyIn = ElectricItem.manager.charge(stack, amount, this.tier, false, false);
               amount -= energyIn;
               charged += energyIn;
               if (amount <= 0.0D) {
                  break;
               }
            }
         }

         return charged;
      }
   }
}
