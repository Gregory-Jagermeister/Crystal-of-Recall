package net.fabricmc.example.items;

import org.jetbrains.annotations.Nullable;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;

public class TestAxeItem extends AxeItem implements PolymerItem {

    public TestAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        // TODO Auto-generated method stub
        return Items.AMETHYST_SHARD;
    }

}
