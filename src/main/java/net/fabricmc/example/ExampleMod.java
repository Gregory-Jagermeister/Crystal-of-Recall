package net.fabricmc.example;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.blocks.CrystalAnchor;
import net.fabricmc.example.items.CrystalAnchorItem;
import net.fabricmc.example.items.CrystalMaterial;
import net.fabricmc.example.items.CrystalUpgradeItem;
import net.fabricmc.example.items.TestAxeItem;
import net.fabricmc.example.items.crystalItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

	// ITEMS
	public static ToolItem ITEM = new crystalItem(Items.ENDER_PEARL, CrystalMaterial.INSTANCE,
			new Item.Settings().rarity(Rarity.EPIC));

	public static AxeItem axeItem = new TestAxeItem(CrystalMaterial.INSTANCE,
			new Item.Settings().fireproof().rarity(Rarity.RARE));

	public static final Item RECALL_UPGRADE = new CrystalUpgradeItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
			new Item.Settings().rarity(Rarity.RARE));

	public static void Register_Anchor_Block(String modelId) {
		var id = Identifier.of("cor", modelId);
		var block = Registry.register(Registries.BLOCK, id,
				new CrystalAnchor(Block.Settings.create().ticksRandomly().hardness(4).requiresTool(),
						"block/" + modelId));

		Registry.register(Registries.ITEM, id,
				new CrystalAnchorItem(block, new Item.Settings().rarity(Rarity.UNCOMMON), "block/" + modelId));
	}

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.markAsRequired();
		PolymerResourcePackUtils.addModAssets("cor");

		// ITEMS
		Registry.register(Registries.ITEM, new Identifier("cor", "recall_crystal"), ITEM);
		Registry.register(Registries.ITEM, new Identifier("cor", "recall_upgrade"), RECALL_UPGRADE);
		Registry.register(Registries.ITEM, new Identifier("cor", "test_axe"), axeItem);

		// BLOCKS
		Register_Anchor_Block("crystal_anchor");

	}

}
