package net.fabricmc.example;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Anchor {
    private BlockPos position;
    private String blockName;
    private RegistryKey<World> dimension;

    public Anchor(BlockPos position, String blockName, RegistryKey<World> dimension) {
        this.position = position;
        this.blockName = blockName;
        this.dimension = dimension;
    }

    public BlockPos getPosition() {
        return position;
    }

    public String getBlockName() {
        return blockName;
    }

    public RegistryKey<World> getDimension() {
        return dimension;
    }

    // Optional: Add setters if you need to change these values after creation
}
