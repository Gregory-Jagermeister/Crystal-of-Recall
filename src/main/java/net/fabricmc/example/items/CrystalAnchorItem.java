package net.fabricmc.example.items;

import org.jetbrains.annotations.Nullable;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CrystalAnchorItem extends BlockItem implements PolymerItem {

    private final PolymerModelData polymerModel;

    public CrystalAnchorItem(Block block, Settings settings, String modelID) {
        super(block, settings);
        this.polymerModel = PolymerResourcePackUtils.requestModel(Items.LODESTONE, Identifier.of("cor", modelID));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.polymerModel.item();
    }

}
