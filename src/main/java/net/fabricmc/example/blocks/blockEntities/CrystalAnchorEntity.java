package net.fabricmc.example.blocks.blockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

public class CrystalAnchorEntity extends BlockEntity implements BlockEntityTicker {

    public CrystalAnchorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRYSTAL_ANCHOR_ENTITY, pos, state);
    }


    public static void tick(World world, BlockPos pos, BlockState state, CrystalAnchorEntity blockEntity) {
        int numberOfParticles = 1; // Total number of particles
        ServerWorld serverWorld = (ServerWorld)world;
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;

        for (int i = 0; i < numberOfParticles; i++) {
            // Generate random spherical coordinates
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);

            // Convert spherical coordinates to Cartesian coordinates
            double sinPhi = Math.sin(phi);
            double startX = centerX + sinPhi * Math.cos(theta);
            double startY = centerY + sinPhi * Math.sin(theta);
            double startZ = centerZ + Math.cos(phi);

            // Adjust velocity toward the center, with offset to reduce falling effect
            double baseUpwardVelocity = 0.0; // Adjust upward force
            double velocityX = (centerX - startX) * 0.01;
            double velocityY = (centerY - startY) * 0.01 + baseUpwardVelocity; // Add upward velocity
            double velocityZ = (centerZ - startZ) * 0.01;

            // Spawn the particle with the adjusted upward velocity
            serverWorld.spawnParticles(ParticleTypes.GLOW, startX, startY, startZ, 0, velocityX, velocityY, velocityZ, 0.5);
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {

    }
}
