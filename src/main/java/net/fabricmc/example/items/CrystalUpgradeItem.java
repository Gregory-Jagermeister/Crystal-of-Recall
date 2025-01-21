package net.fabricmc.example.items;

import org.jetbrains.annotations.Nullable;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CrystalUpgradeItem extends Item implements PolymerItem {

    private final PolymerModelData model;

    public CrystalUpgradeItem(Item polymerItem, Settings settings) {
        super(settings);
        this.model = PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of("cor", "item/recall_upgrade"));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return model.value();
    }

}
