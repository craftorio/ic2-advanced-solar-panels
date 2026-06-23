package com.chocohead.advsolar.items;

import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDoubleSlab extends ItemBlock implements IItemModelProvider {
   protected final String location;

   public ItemDoubleSlab() {
      super(Blocks.DOUBLE_STONE_SLAB);
      this.setCreativeTab(IC2.tabIC2);
      BlocksItems.registerItem(this, new ResourceLocation("advanced_solar_panels", this.location = ((ResourceLocation)Block.REGISTRY.getNameForObject(Blocks.DOUBLE_STONE_SLAB)).getPath()));
   }

   @SideOnly(Side.CLIENT)
   public void registerModels(ItemName name) {
      ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation("advanced_solar_panels:" + this.location, (String)null));
   }

   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      ItemStack stack = StackUtil.get(player, hand);
      if (!block.isReplaceable(world, pos)) {
         pos = pos.offset(facing);
      }

      if (!StackUtil.isEmpty(stack) && player.canPlayerEdit(pos, facing, stack) && world.mayPlace(block, pos, false, facing, player)) {
         if (this.placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, block.getStateFromMeta(this.getMetadata(stack.getMetadata())))) {
            SoundType soundtype = this.block.getSoundType(state, world, pos, player);
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            StackUtil.consumeOrError(player, hand, 1);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         items.add(new ItemStack(this));
      }

   }
}
