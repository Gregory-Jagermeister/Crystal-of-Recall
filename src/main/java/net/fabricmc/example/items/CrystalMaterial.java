package net.fabricmc.example.items;

import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CrystalMaterial implements ToolMaterial {

    public static final CrystalMaterial INSTANCE = new CrystalMaterial();

    @Override
    public int getDurability() {
        return 11;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 5.0f;
    }

    @Override
    public float getAttackDamage() {
        return 1f;
    }

    public int getMiningLevel() {
        return 1;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.AMETHYST_SHARD);
    }

    @Override
    public TagKey<Block> getInverseTag() {
        // Assuming there is a custom block tag for the tool material
        return TagKey.of(RegistryKey.ofRegistry(new Identifier("minecraft", "block")),
                new Identifier("cor", "empty_tag"));
    }

}
