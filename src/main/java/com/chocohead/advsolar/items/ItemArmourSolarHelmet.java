package com.chocohead.advsolar.items;

import com.chocohead.advsolar.tiles.TileEntityAdvancedSolar;
import com.chocohead.advsolar.tiles.TileEntityHybridSolar;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import com.chocohead.advsolar.tiles.TileEntityUltimateHybridSolar;
import com.google.common.base.CaseFormat;
import ic2.api.item.ElectricItem;
import ic2.api.item.HudMode;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudProvider;
import ic2.api.item.IMetalArmor;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.ElectricItemManager;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.Locale;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmourSolarHelmet extends ItemArmor implements IItemModelProvider, IElectricItem, IMetalArmor, ISpecialArmor, IItemHudProvider {
   protected static final int DEFAULT_COLOUR = -1;
   protected final ItemArmourSolarHelmet.SolarHelmetTypes type;
   public static boolean chargeWholeInventory = false;
   protected TileEntitySolarPanel.GenerationState state;
   protected int ticker;

   public ItemArmourSolarHelmet(ItemArmourSolarHelmet.SolarHelmetTypes type) {
      super(ArmorMaterial.DIAMOND, -1, EntityEquipmentSlot.HEAD);
      ((ItemArmourSolarHelmet)BlocksItems.registerItem(this, new ResourceLocation("advanced_solar_panels", type.getName()))).setTranslationKey(type.getLocalisedName());
      this.setCreativeTab(IC2.tabIC2);
      this.setMaxDamage(27);
      this.type = type;
   }

   public String getTranslationKey() {
      return "advanced_solar_panels." + super.getTranslationKey().substring(5);
   }

   public String getTranslationKey(ItemStack stack) {
      return this.getTranslationKey();
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return Localization.translate(this.getTranslationKey(stack));
   }

   public int getMetadata(ItemStack stack) {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public void registerModels(ItemName name) {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("advanced_solar_panels:" + CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.type.getName()), (String)null));
   }

   public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
      return "advanced_solar_panels:textures/armour/" + this.type.getName() + (type != null ? "Overlay" : "") + ".png";
   }

   public boolean canBeDyed() {
      return this.type != ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED;
   }

   public void setColor(ItemStack stack, int colour) {
      this.getDisplayNbt(stack, true).setInteger("colour", colour);
   }

   public boolean hasColor(ItemStack stack) {
      return this.getColor(stack) != -1;
   }

   public int getColor(ItemStack stack) {
      NBTTagCompound nbt = this.getDisplayNbt(stack, false);
      return nbt != null && nbt.hasKey("colour", 3) ? nbt.getInteger("colour") : -1;
   }

   public void removeColor(ItemStack stack) {
      NBTTagCompound nbt = this.getDisplayNbt(stack, false);
      if (nbt != null && nbt.hasKey("colour", 3)) {
         nbt.removeTag("colour");
         if (nbt.isEmpty()) {
            stack.getTagCompound().removeTag("display");
         }

      }
   }

   protected NBTTagCompound getDisplayNbt(ItemStack stack, boolean create) {
      NBTTagCompound nbt = stack.getTagCompound();
      if (nbt == null) {
         if (!create) {
            return null;
         }

         nbt = new NBTTagCompound();
         stack.setTagCompound(nbt);
      }

      NBTTagCompound out;
      if (!nbt.hasKey("display", 10)) {
         if (!create) {
            return null;
         }

         out = new NBTTagCompound();
         nbt.setTag("display", out);
      } else {
         out = nbt.getCompoundTag("display");
      }

      return out;
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
      if (!this.HUDstuff(world.isRemote, player, stack)) {
         if (this.ticker++ % this.tickRate() == 0) {
            this.checkTheSky(world, player.getPosition());
         }

         if (this.type != ItemArmourSolarHelmet.SolarHelmetTypes.ADVANCED) {
            int airLevel = player.getAir();
            if (ElectricItem.manager.canUse(stack, 1000.0D) && airLevel < 100) {
               player.setAir(airLevel + 200);
               ElectricItem.manager.use(stack, 1000.0D, player);
            }
         }

         int output;
         switch (this.state) {
            case DAY:
               output = this.type.dayEU;
               break;
            case NIGHT:
               output = this.type.nightEU;
               break;
            case NONE:
            default:
               return;
         }

         for(ItemStack playerStack : player.inventory.armorInventory.subList(0, player.inventory.armorInventory.size() - 1)) {
            if (!StackUtil.isEmpty(playerStack) && playerStack.getItem() instanceof IElectricItem) {
               output = (int)((double)output - ElectricItem.manager.charge(playerStack, (double)output, this.type.tier, false, false));
               if (output <= 0) {
                  return;
               }
            }
         }

         if (chargeWholeInventory) {
            for(ItemStack playerStack : player.inventory.offHandInventory) {
               if (!StackUtil.isEmpty(playerStack) && playerStack.getItem() instanceof IElectricItem) {
                  output = (int)((double)output - ElectricItem.manager.charge(playerStack, (double)output, this.type.tier, false, false));
                  if (output <= 0) {
                     return;
                  }
               }
            }

            for(ItemStack playerStack : player.inventory.mainInventory) {
               if (!StackUtil.isEmpty(playerStack) && playerStack.getItem() instanceof IElectricItem) {
                  output = (int)((double)output - ElectricItem.manager.charge(playerStack, (double)output, this.type.tier, false, false));
                  if (output <= 0) {
                     return;
                  }
               }
            }
         }

         ElectricItem.manager.charge(stack, (double)output, Integer.MAX_VALUE, true, false);
      }
   }

   protected boolean HUDstuff(boolean isRemote, EntityPlayer player, ItemStack stack) {
      NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
      byte toggleTimer = nbt.getByte("toggleTimer");
      if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
         byte hubmode = nbt.getByte("hudMode");
         toggleTimer = 10;
         if (hubmode == HudMode.getMaxMode()) {
            hubmode = 0;
         } else {
            ++hubmode;
         }

         if (!isRemote) {
            nbt.setByte("hudMode", hubmode);
            IC2.platform.messagePlayer(player, Localization.translate(HudMode.getFromID(hubmode).getTranslationKey()), new Object[0]);
         }
      }

      if (!isRemote && toggleTimer > 0) {
         --toggleTimer;
         nbt.setByte("toggleTimer", toggleTimer);
      }

      return isRemote;
   }

   protected int tickRate() {
      return 128;
   }

   public void checkTheSky(World world, BlockPos pos) {
      if (!world.provider.isNether() && world.canBlockSeeSky(pos)) {
         if (!world.isDaytime() || (world.getBiome(pos).canRain() || world.getBiome(pos).getRainfall() > 0.0F) && (world.isRaining() || world.isThundering())) {
            this.state = TileEntitySolarPanel.GenerationState.NIGHT;
         } else {
            this.state = TileEntitySolarPanel.GenerationState.DAY;
         }
      } else {
         this.state = TileEntitySolarPanel.GenerationState.NONE;
      }

   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ElectricItemManager.addChargeVariants(this, items);
      }

   }

   public EnumRarity getRarity(ItemStack stack) {
      return this.type.rarity;
   }

   public boolean isMetalArmor(ItemStack stack, EntityPlayer player) {
      return true;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armour, DamageSource source, double damage, int slot) {
      return source.isUnblockable() ? new ISpecialArmor.ArmorProperties(0, 0.0D, 0) : new ISpecialArmor.ArmorProperties(0, 0.15D * this.type.damageAbsorptionRatio, (int)(25.0D * ElectricItem.manager.getCharge(armour) / (double)this.type.energyPerDamage));
   }

   public int getArmorDisplay(EntityPlayer player, ItemStack armour, int slot) {
      return ElectricItem.manager.getCharge(armour) >= (double)this.type.energyPerDamage ? (int)Math.round(3.0D * this.type.damageAbsorptionRatio) : 0;
   }

   public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
      ElectricItem.manager.discharge(stack, (double)(damage * this.type.energyPerDamage), Integer.MAX_VALUE, true, false, false);
   }

   public boolean canProvideEnergy(ItemStack stack) {
      return false;
   }

   public int getTier(ItemStack stack) {
      return this.type.tier;
   }

   public double getMaxCharge(ItemStack stack) {
      return this.type.maxCharge;
   }

   public double getTransferLimit(ItemStack stack) {
      return this.type.transferLimit;
   }

   public boolean doesProvideHUD(ItemStack stack) {
      return ElectricItem.manager.getCharge(stack) > 0.0D;
   }

   public HudMode getHudMode(ItemStack stack) {
      return HudMode.getFromID(StackUtil.getOrCreateNbtData(stack).getByte("hudMode"));
   }

   public static enum SolarHelmetTypes {
      ADVANCED(EnumRarity.UNCOMMON, TileEntityAdvancedSolar.settings.dayPower, TileEntityAdvancedSolar.settings.nightPower, 3, 1000000.0D, 3000.0D, 800, 0.9D),
      HYBRID(EnumRarity.RARE, TileEntityHybridSolar.settings.dayPower, TileEntityHybridSolar.settings.nightPower, 4, 1.0E7D, 10000.0D, 2000, 1.0D),
      ULTIMATE(EnumRarity.EPIC, TileEntityUltimateHybridSolar.settings.dayPower, TileEntityUltimateHybridSolar.settings.nightPower, 4, 1.0E7D, 10000.0D, 2000, 1.0D);

      public final double maxCharge;
      public final double transferLimit;
      public final double damageAbsorptionRatio;
      public final int dayEU;
      public final int nightEU;
      public final int tier;
      public final int energyPerDamage;
      public final EnumRarity rarity;
      private final String name = this.name().toLowerCase(Locale.ENGLISH);

      private SolarHelmetTypes(EnumRarity rarity, int dayEU, int nightEU, int tier, double maxCharge, double transferLimit, int energyPerDamage, double damageAbsorptionRatio) {
         this.rarity = rarity;
         this.dayEU = dayEU;
         this.nightEU = nightEU;
         this.tier = tier;
         this.maxCharge = maxCharge;
         this.transferLimit = transferLimit;
         this.energyPerDamage = energyPerDamage;
         this.damageAbsorptionRatio = damageAbsorptionRatio;

         assert damageAbsorptionRatio > 0.0D;

      }

      public String getName() {
         return this.name + "SolarHelmet";
      }

      protected String getLocalisedName() {
         return "solar_helmets." + this.name;
      }
   }
}
