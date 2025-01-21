package net.fabricmc.example.blocks.blockEntities;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.blocks.CrystalAnchor;
import net.fabricmc.example.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    private static <T extends BlockEntity> BlockEntityType<T> register(String name,
                                                                       BlockEntityType.BlockEntityFactory<? extends T> entityFactory,
                                                                       Block... blocks) {
        Identifier id = Identifier.of(ExampleMod.MOD_ID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static final BlockEntityType<CrystalAnchorEntity> CRYSTAL_ANCHOR_ENTITY = register("crystal_anchor", CrystalAnchorEntity::new, ModBlocks.CRYSTAL_ANCHOR);
    public static void initialize(){

    }
}
