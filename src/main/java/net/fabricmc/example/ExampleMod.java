package net.fabricmc.example;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.items.CrystalMaterial;
import net.fabricmc.example.items.TestAxeItem;
import net.fabricmc.example.items.crystalItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.*;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static ToolItem ITEM = new crystalItem(Items.WOODEN_HOE, CrystalMaterial.INSTANCE,
			new Item.Settings());

	public static AxeItem axeItem = new TestAxeItem(CrystalMaterial.INSTANCE,
			new Item.Settings().fireproof().rarity(Rarity.RARE));

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.markAsRequired();
		PolymerResourcePackUtils.addModAssets("cor");

		Registry.register(Registries.ITEM, new Identifier("cor", "recall_crystal"), ITEM);
		Registry.register(Registries.ITEM, new Identifier("cor", "test_axe"), axeItem);
	}

}
