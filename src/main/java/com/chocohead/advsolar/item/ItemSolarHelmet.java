package com.chocohead.advsolar.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IMetalArmor;
import ic2.core.item.armor.ItemArmorElectric;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A chargeable solar helmet. While worn under the open sky it generates EU (full output in daylight,
 * reduced at night/rain) and pours it into the wearer's other electric armour and — like the classic
 * mod — the rest of their inventory. The hybrid and ultimate variants also refill the wearer's air.
 *
 * <p>Built on IC2: Refactored's {@link ItemArmorElectric}. The classic HUD overlay and helmet dyeing
 * are not reimplemented (see PORTING.md).
 */
public class ItemSolarHelmet extends ItemArmorElectric implements IMetalArmor {
	public enum SolarHelmetType {
		ADVANCED("advanced_solar_helmet", 8, 1, 3, 1_000_000.0D, 3_000.0D, 800, 0.9D),
		HYBRID("hybrid_solar_helmet", 64, 8, 4, 10_000_000.0D, 10_000.0D, 2_000, 1.0D),
		ULTIMATE("ultimate_solar_helmet", 512, 64, 4, 10_000_000.0D, 10_000.0D, 2_000, 1.0D);

		public final String name;
		public final int dayEU;
		public final int nightEU;
		public final int tier;
		public final double maxCharge;
		public final double transferLimit;
		public final int energyPerDamage;
		public final double damageAbsorptionRatio;

		SolarHelmetType(String name, int dayEU, int nightEU, int tier, double maxCharge, double transferLimit,
				int energyPerDamage, double damageAbsorptionRatio) {
			this.name = name;
			this.dayEU = dayEU;
			this.nightEU = nightEU;
			this.tier = tier;
			this.maxCharge = maxCharge;
			this.transferLimit = transferLimit;
			this.energyPerDamage = energyPerDamage;
			this.damageAbsorptionRatio = damageAbsorptionRatio;
		}
	}

	private final SolarHelmetType type;
	private int ticker;

	public ItemSolarHelmet(SolarHelmetType type) {
		super(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Properties(), type.maxCharge, type.transferLimit, type.tier);
		this.type = type;
	}

	@Override
	public int getEnergyPerDamage() {
		return this.type.energyPerDamage;
	}

	@Override
	public double getDamageAbsorptionRatio() {
		return this.type.damageAbsorptionRatio;
	}

	@Override
	public boolean isMetalArmor(ItemStack stack, Player player) {
		return true;
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player) {
		if (world.isClientSide) {
			return;
		}

		if (this.type != SolarHelmetType.ADVANCED) {
			int air = player.getAirSupply();
			if (air < 100 && ElectricItem.manager.canUse(stack, 1000.0D)) {
				player.setAirSupply(air + 200);
				ElectricItem.manager.use(stack, 1000.0D, player);
			}
		}

		if (this.ticker++ % 128 != 0) {
			return;
		}

		int output = generationOutput(world, player);
		if (output <= 0) {
			return;
		}

		// Top up other electric armour first, then the rest of the inventory, then this helmet.
		for (ItemStack armour : player.getInventory().armor) {
			output = chargeInto(armour, output);
			if (output <= 0) {
				return;
			}
		}
		for (ItemStack held : player.getInventory().offhand) {
			output = chargeInto(held, output);
			if (output <= 0) {
				return;
			}
		}
		for (ItemStack held : player.getInventory().items) {
			output = chargeInto(held, output);
			if (output <= 0) {
				return;
			}
		}

		ElectricItem.manager.charge(stack, output, Integer.MAX_VALUE, true, false);
	}

	private int chargeInto(ItemStack stack, int output) {
		if (!stack.isEmpty() && stack.getItem() instanceof IElectricItem && stack.getItem() != this) {
			output -= (int) ElectricItem.manager.charge(stack, output, this.type.tier, false, false);
		}

		return output;
	}

	private int generationOutput(Level world, Player player) {
		if (!world.dimensionType().hasSkyLight() || !world.canSeeSky(player.blockPosition().above())) {
			return 0;
		}

		boolean night = !world.isDay() || world.isRaining() || world.isThundering();
		return night ? this.type.nightEU : this.type.dayEU;
	}
}
