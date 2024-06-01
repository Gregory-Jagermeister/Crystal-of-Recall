package net.fabricmc.example.items;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

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

    @Override
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

}
