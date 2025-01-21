package net.fabricmc.example.blocks;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.items.CrystalAnchorItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {

    public static final Block CRYSTAL_ANCHOR = register(new CrystalAnchor(AbstractBlock.Settings.create().ticksRandomly()),"crystal_anchor",true, CrystalAnchorItem.class);

    public static <T extends BlockItem> Block register(Block block, String name, boolean shouldRegisterItem,  Class<T> itemClass){
        // Register the block and its item.
        Identifier id = Identifier.of(ExampleMod.MOD_ID, name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            try {
                // Use reflection to create an instance of the specified BlockItem class
                T blockItem = itemClass.getDeclaredConstructor(Block.class, Item.Settings.class, String.class)
                        .newInstance(block, new Item.Settings(), "block/" + name);
                Registry.register(Registries.ITEM, id, blockItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void initialize(){

    }
}
