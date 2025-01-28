package net.fabricmc.example.blocks;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.example.StateSaverAndLoader;
import net.fabricmc.example.blocks.blockEntities.CrystalAnchorEntity;
import net.fabricmc.example.blocks.blockEntities.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class CrystalAnchor extends BlockWithEntity implements PolymerTexturedBlock {

    private final BlockState polymerBlockState;

    public CrystalAnchor(Settings settings) {
        super(settings);

        this.polymerBlockState = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
                PolymerBlockModel.of(Identifier.of("cor", "block/crystal_anchor")));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.polymerBlockState;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            // Retrieve the list of warps for the player
            StateSaverAndLoader anchorState = StateSaverAndLoader.getServerState(serverPlayer.getServer());
            anchorState.removePlayerAnchor(serverPlayer.getUuid(), pos);
        }


        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        System.out.println("Hello World");

    }

    public void showTeleportationCircle(ServerWorld world, BlockPos pos) {
        // Loop over angles to create a circular effect
        for (int angle = 0; angle < 360; angle += 10) {
            double radians = Math.toRadians(angle);
            double offsetX = Math.cos(radians);
            double offsetZ = Math.sin(radians);
            double x = pos.getX() + 0.5 + offsetX;
            double y = pos.getY() + 0.5;  // Adjust this if you want the circle higher or lower
            double z = pos.getZ() + 0.5 + offsetZ;

            // Use spawnParticles to send this to all clients observing this portion of the world
            world.spawnParticles(ParticleTypes.PORTAL, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(CrystalAnchor::new);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrystalAnchorEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.CRYSTAL_ANCHOR_ENTITY, CrystalAnchorEntity::tick);
    }
}
