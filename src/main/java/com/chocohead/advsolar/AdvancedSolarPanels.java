package com.chocohead.advsolar;

import com.chocohead.advsolar.gui.ProgressBars;
import com.chocohead.advsolar.items.ItemCraftingThings;
import com.chocohead.advsolar.renders.PrettyMolecularTransformerTESR;
import com.chocohead.advsolar.tiles.TEs;
import com.chocohead.advsolar.tiles.TileEntityMolecularAssembler;
import ic2.api.event.TeBlockFinalCallEvent;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TeBlockRegistry;
import ic2.core.util.ReflectionUtil;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
@Mod(
   modid = "advanced_solar_panels",
   name = "Advanced Solar Panels",
   dependencies = "required-after:ic2@[2.8.74,);",
   version = "4.3.0",
   acceptedMinecraftVersions = "[1.12,1.12.2]"
)
public final class AdvancedSolarPanels {
   public static final String MODID = "advanced_solar_panels";
   public static Logger log;
   public static BlockTileEntity machines;

   @SubscribeEvent
   public static void register(TeBlockFinalCallEvent event) {
      TeBlockRegistry.addAll(TEs.class, TEs.IDENTITY);
      TeBlockRegistry.setDefaultMaterial(TEs.IDENTITY, Material.IRON);
   }

   @EventHandler
   public void load(FMLPreInitializationEvent event) {
      log = event.getModLog();
      Configs.loadConfig(event.getSuggestedConfigurationFile(), event.getSide().isClient());
      machines = TeBlockRegistry.get(TEs.IDENTITY);
      if (event.getSide().isClient()) {
         setupRenderingGuf();
      }

      ASP_Items.buildItems(event.getSide());
      OreDictionary.registerOre("ingotUranium", ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.URANIUM_INGOT));
      OreDictionary.registerOre("ingotIridium", ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.IRIDIUM_INGOT));
      OreDictionary.registerOre("craftingSolarPanelHV", machines.getItemStack(TEs.ultimate_solar_panel));
      OreDictionary.registerOre("craftingSunnariumPart", ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM_PART));
      OreDictionary.registerOre("craftingSunnarium", ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUNNARIUM));
      OreDictionary.registerOre("craftingMTCore", ASP_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.MT_CORE));
      OreDictionary.registerOre("craftingMolecularTransformer", machines.getItemStack(TEs.molecular_transformer));
   }

   @SideOnly(Side.CLIENT)
   private static void setupRenderingGuf() {
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMolecularAssembler.class, new PrettyMolecularTransformerTESR());
      ForgeHooksClient.registerTESRItemStack(machines.getItem(), TEs.molecular_transformer.getId(), TEs.molecular_transformer.getTeClass());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Recipes.addCraftingRecipes();
      Recipes.addMachineRecipes();
      Recipes.addMolecularTransformerRecipes();
      TEs.buildDummies();
      ProgressBars.addStyles();
      TileEntityMolecularAssembler.MolecularOutput.registerNetwork();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void doColourThings(ColorHandlerEvent.Item event) {
      ItemColors colours = event.getItemColors();
      IItemColor armourColouring = (IItemColor)((Map)ReflectionUtil.getFieldValue(ReflectionUtil.getField(ItemColors.class, Map.class), colours)).get(Items.LEATHER_BOOTS.delegate);
      colours.registerItemColorHandler(armourColouring, new Item[]{ASP_Items.HYBRID_SOLAR_HELMET.getInstance(), ASP_Items.ULTIMATE_HYBRID_SOLAR_HELMET.getInstance()});
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }
}
