package com.chocohead.advsolar.tiles;

import com.chocohead.advsolar.IMolecularTransformerRecipeManager;
import com.chocohead.advsolar.gui.TransparentDynamicGUI;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.network.IGrowingBuffer;
import ic2.api.network.INetworkCustomEncoder;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.MachineRecipeResult;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.Localization;
import ic2.core.network.DataEncoder;
import ic2.core.network.GuiSynced;
import ic2.core.network.DataEncoder.EncodedType;
import ic2.core.util.StackUtil;
import ic2.core.util.Tuple;
import ic2.core.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMolecularAssembler extends TileEntityInventory implements IEnergySink, IHasGui, IGuiValueProvider {
   protected static final List<AxisAlignedBB> AABBs = Arrays.asList(new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.05D, 0.0D, 0.2D, 0.6D, 1.0D, 0.8D));
   protected static final byte MAX_TIME_WAIT = 40;
   public final InvSlotProcessable<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack> inputSlot = new InvSlotProcessable<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack>(this, "input", 1, IMolecularTransformerRecipeManager.RECIPES) {
      protected ItemStack getInput(ItemStack stack) {
         return stack;
      }

      protected void setInput(ItemStack input) {
         this.put(input);
      }
   };
   public final InvSlotOutput outputSlot = new InvSlotOutput(this, "output", 1);
   @GuiSynced
   protected Tuple.T2<ItemStack, TileEntityMolecularAssembler.MolecularOutput> currentRecipe;
   private boolean addedToEnet;
   protected double energyIn;
   protected double energyGiven;
   @GuiSynced
   protected double lastEnergyGiven;
   @GuiSynced
   protected double energyUsed;
   protected byte wait;

   public TileEntityMolecularAssembler() {
      this.comparator.setUpdate(() -> this.currentRecipe == null ? 0 : (int)Util.lerp(0.0D, 15.0D, this.energyUsed / (double)((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).totalEU));
   }

   protected void onLoaded() {
      super.onLoaded();
      if (!this.world.isRemote) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
         this.addedToEnet = true;
      }

   }

   protected void onUnloaded() {
      super.onUnloaded();
      if (this.addedToEnet) {
         MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
         this.addedToEnet = false;
      }

   }

   public void readFromNBT(NBTTagCompound nbt) {
      super.readFromNBT(nbt);
      this.energyUsed = nbt.getDouble("energyUsed");
      if (nbt.hasKey("recipe")) {
         ItemStack input = new ItemStack(nbt.getCompoundTag("recipe"));
         if (input != null) {
            MachineRecipeResult<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack> output = IMolecularTransformerRecipeManager.RECIPES.apply(input, false);
            if (output != null) {
               this.currentRecipe = new Tuple.T2(input, new TileEntityMolecularAssembler.MolecularOutput(output));
            }
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      super.writeToNBT(nbt);
      nbt.setDouble("energyUsed", this.energyUsed);
      if (this.currentRecipe != null) {
         nbt.setTag("recipe", ((ItemStack)this.currentRecipe.a).writeToNBT(new NBTTagCompound()));
      }

      return nbt;
   }

   public List<String> getNetworkedFields() {
      List<String> out = super.getNetworkedFields();
      out.add("energyUsed");
      out.add("energyIn");
      return out;
   }

   protected void updateEntityServer() {
      super.updateEntityServer();
      boolean nextActive = this.getActive();
      boolean updateInv = false;
      if (this.currentRecipe == null) {
         if (!this.inputSlot.isEmpty()) {
            nextActive = updateInv = this.canWork();
         } else {
            nextActive = false;
         }
      } else {
         nextActive = true;
      }

      this.lastEnergyGiven = this.energyGiven;
      this.energyGiven = 0.0D;
      if (nextActive) {
         if (this.energyIn <= 0.0D) {
            this.energyIn = 0.0D;
            if (this.wait++ >= 40) {
               nextActive = false;
            }
         } else {
            this.wait = 0;
            double energyLeft = this.getDemandedEnergy();
            if (energyLeft > this.energyIn) {
               this.energyUsed += this.energyIn;
               this.energyIn = 0.0D;
            } else {
               this.energyIn -= energyLeft;
               this.outputSlot.add(((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).output);
               this.currentRecipe = null;
               this.energyUsed = 0.0D;
               updateInv = true;
            }
         }
      }

      if (this.getActive() != nextActive) {
         this.setActive(nextActive);
         this.getWorld().checkLightFor(EnumSkyBlock.BLOCK, this.pos);
      }

      if (updateInv) {
         this.markDirty();
      }

   }

   protected boolean canWork() {
      MachineRecipeResult<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack> result = this.inputSlot.process();
      if (result != null && this.outputSlot.canAdd(StackUtil.copy((ItemStack)result.getOutput()))) {
         this.currentRecipe = new Tuple.T2(this.inputSlot.get().copy(), new TileEntityMolecularAssembler.MolecularOutput(result));
         this.inputSlot.consume(result);
         return true;
      } else {
         return false;
      }
   }

   protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
      return AABBs;
   }

   @SideOnly(Side.CLIENT)
   protected boolean shouldSideBeRendered(EnumFacing side, BlockPos otherPos) {
      return false;
   }

   public boolean canRenderBreaking() {
      return true;
   }

   protected int getLightValue() {
      return this.getActive() ? 12 : 0;
   }

   public void onNetworkUpdate(String field) {
      super.onNetworkUpdate(field);
      if (field.equals("active")) {
         this.getWorld().checkLightFor(EnumSkyBlock.BLOCK, this.pos);
      }

   }

   public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
      return true;
   }

   public int getSinkTier() {
      return 14;
   }

   public double getDemandedEnergy() {
      return this.currentRecipe == null ? (this.energyGiven = 0.0D) : (double)((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).totalEU - this.energyUsed;
   }

   public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
      this.energyGiven += amount;
      double wanted = this.getDemandedEnergy();
      if (wanted == 0.0D) {
         return amount;
      } else if (wanted >= amount) {
         this.energyIn += amount;
         return 0.0D;
      } else {
         double in = amount - wanted;
         this.energyIn += in;
         return amount - in;
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, List<String> tooltip, ITooltipFlag advanced) {
      super.addInformation(stack, tooltip, advanced);
      tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", new Object[]{this.getSinkTier()}));
   }

   public ContainerBase<TileEntityMolecularAssembler> getGuiContainer(EntityPlayer player) {
      return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
      return TransparentDynamicGUI.create(this, player, GuiParser.parse(this.teBlock));
   }

   public void onGuiClosed(EntityPlayer player) {
   }

   public double getGuiValue(String name) {
      if ("progress".equals(name)) {
         return this.currentRecipe == null ? 0.0D : this.energyUsed / (double)((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).totalEU;
      } else {
         throw new IllegalArgumentException("Unexpected GUI value requested: " + name);
      }
   }

   @SideOnly(Side.CLIENT)
   public String getInput() {
      return this.currentRecipe == null ? "" : ((ItemStack)this.currentRecipe.a).getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getOutput() {
      return this.currentRecipe == null ? "" : ((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).output.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getEnergyNeeded() {
      return this.currentRecipe == null ? "" : String.format("%,d %s", ((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).totalEU, Localization.translate("ic2.generic.text.EU"));
   }

   @SideOnly(Side.CLIENT)
   public String getEU() {
      return this.currentRecipe == null ? "" : String.format("%,.0f %s", this.lastEnergyGiven, Localization.translate("ic2.generic.text.EUt"));
   }

   @SideOnly(Side.CLIENT)
   public String getPercent() {
      return this.currentRecipe == null ? "" : String.format("%,.0f%%", this.energyUsed * 100.0D / (double)((TileEntityMolecularAssembler.MolecularOutput)this.currentRecipe.b).totalEU);
   }

   public static final class MolecularOutput implements INetworkCustomEncoder {
      public final ItemStack output;
      public final int totalEU;

      public MolecularOutput(MachineRecipeResult<IMolecularTransformerRecipeManager.Input, ItemStack, ItemStack> result) {
         this((ItemStack)result.getOutput(), ((IMolecularTransformerRecipeManager.Input)result.getRecipe().getInput()).totalEU);
      }

      private MolecularOutput(ItemStack output, int totalEU) {
         this.output = output;
         this.totalEU = totalEU;
      }

      public static void registerNetwork() {
         DataEncoder.addNetworkEncoder(TileEntityMolecularAssembler.MolecularOutput.class, new TileEntityMolecularAssembler.MolecularOutput((ItemStack)null, 0));
      }

      public boolean isThreadSafe() {
         return true;
      }

      public void encode(IGrowingBuffer buffer, Object instance) throws IOException {
         TileEntityMolecularAssembler.MolecularOutput mo = (TileEntityMolecularAssembler.MolecularOutput)instance;
         DataEncoder.encode(buffer, mo.output, false);
         DataEncoder.encode(buffer, mo.totalEU, false);
      }

      public Object decode(IGrowingBuffer buffer) throws IOException {
         return new TileEntityMolecularAssembler.MolecularOutput((ItemStack)DataEncoder.decode(buffer, EncodedType.ItemStack), (Integer)DataEncoder.decode(buffer, EncodedType.Integer));
      }
   }
}
