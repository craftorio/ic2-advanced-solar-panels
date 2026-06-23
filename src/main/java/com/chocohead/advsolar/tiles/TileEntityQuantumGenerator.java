package com.chocohead.advsolar.tiles;

import com.chocohead.advsolar.gui.BackgroundlessDynamicGUI;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.init.Localization;
import ic2.core.network.GuiSynced;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityQuantumGenerator extends TileEntityInventory implements IMultiEnergySource, IHasGui, INetworkClientTileEntityEventListener {
   public static TileEntityQuantumGenerator.QuantumGeneratorConfig settings;
   @GuiSynced
   public int production;
   @GuiSynced
   public int tier;
   protected Redstone redstone;
   private boolean addedToEnet;

   public TileEntityQuantumGenerator() {
      this.production = settings.production;
      this.tier = settings.tier;
      this.redstone = (Redstone)this.addComponent(new Redstone(this));
      this.redstone.subscribe(new Redstone.IRedstoneChangeHandler() {
         public void onRedstoneChange(int newLevel) {
            TileEntityQuantumGenerator.this.setActive(newLevel <= 0);
         }
      });
   }

   protected void onLoaded() {
      super.onLoaded();
      if (!this.world.isRemote) {
         this.addedToEnet = !MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
      }

   }

   protected void onUnloaded() {
      super.onUnloaded();
      if (this.addedToEnet) {
         this.addedToEnet = MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
      }

   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.production = nbt.getInteger("production");
      this.tier = nbt.getInteger("tier");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setInteger("production", this.production);
      nbt.setInteger("tier", this.tier);
      return nbt;
   }

   public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
      super.onPlaced(stack, placer, facing);
      if (!this.world.isRemote) {
         this.setActive(true);
      }

   }

   public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing side) {
      return true;
   }

   public int getSourceTier() {
      return this.tier;
   }

   public double getOfferedEnergy() {
      return this.getActive() ? (this.sendMultipleEnergyPackets() ? (double)this.production / (double)this.getMultipleEnergyPacketAmount() : (double)this.production) : 0.0D;
   }

   public void drawEnergy(double amount) {
   }

   public boolean sendMultipleEnergyPackets() {
      return (double)this.production - EnergyNet.instance.getPowerFromTier(this.tier) > 0.0D;
   }

   public int getMultipleEnergyPacketAmount() {
      return (int)Math.ceil((double)this.production / EnergyNet.instance.getPowerFromTier(this.tier));
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, List<String> tooltip, ITooltipFlag advanced) {
      super.addInformation(stack, tooltip, advanced);
      tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", new Object[]{"Variable"}));
   }

   public ContainerBase<TileEntityQuantumGenerator> getGuiContainer(EntityPlayer player) {
      return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
      return BackgroundlessDynamicGUI.create(this, player, GuiParser.parse(this.teBlock));
   }

   public void onGuiClosed(EntityPlayer player) {
   }

   public void onNetworkEvent(EntityPlayer player, int event) {
      switch (event / 10) {
         case 0:
            switch (event % 10) {
               case 0:
                  this.changeProduction(-100);
                  return;
               case 1:
                  this.changeProduction(-10);
                  return;
               case 2:
                  this.changeProduction(-1);
                  return;
               case 3:
                  this.changeProduction(1);
                  return;
               case 4:
                  this.changeProduction(10);
                  return;
               case 5:
                  this.changeProduction(100);
                  return;
               default:
                  return;
            }
         case 1:
            switch (event % 10) {
               case 0:
                  this.changeProduction(-500);
                  return;
               case 1:
                  this.changeProduction(-50);
                  return;
               case 2:
                  this.changeProduction(-5);
                  return;
               case 3:
                  this.changeProduction(5);
                  return;
               case 4:
                  this.changeProduction(50);
                  return;
               case 5:
                  this.changeProduction(500);
                  return;
               default:
                  return;
            }
         case 2:
            this.tier = event % 10 + 1;
      }

   }

   protected void changeProduction(int value) {
      this.production += value;
      if (this.production < 0) {
         this.production = 0;
      }

   }

   public String getTier() {
      return this.tier > 5 ? Localization.translate("advanced_solar_panels.gui.max") : Integer.toString(this.tier);
   }

   public static final class QuantumGeneratorConfig {
      final int production;
      final int tier;

      public QuantumGeneratorConfig(int production, int tier) {
         this.production = production;
         this.tier = tier;
      }
   }
}
