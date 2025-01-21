package net.fabricmc.example.items;

import net.fabricmc.example.ExampleMod;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {



    public static Item register(Item item, String id){
        Identifier itemID = Identifier.of(ExampleMod.MOD_ID, id);
        // Return the registered item!
        return Registry.register(Registries.ITEM, itemID, item);
    }

    public static ToolItem CRYSTAL_ITEM = new crystalItem(Items.ENDER_PEARL, CrystalMaterial.INSTANCE,
            new Item.Settings().rarity(Rarity.EPIC));

    public static ToolItem CRYSTAL_REINFORCED_ITEM = new ReinforcedCrystalItem(Items.ENDER_PEARL, CrystalMaterial.INSTANCE,
            new Item.Settings().rarity(Rarity.EPIC));

    public static final Item RECALL_UPGRADE = new CrystalUpgradeItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
            new Item.Settings().rarity(Rarity.RARE));

    public static final Item RECALL_CRYSTAL = register(CRYSTAL_ITEM, "recall_crystal");
    public static final Item UPGRADED_RECALL_CRYSTAL = register(CRYSTAL_REINFORCED_ITEM, "upgraded_recall_crystal");
    public static final Item RECALL_UPGRADE_ITEM = register(RECALL_UPGRADE, "recall_upgrade");

    public static void initialize(){

    }
}
