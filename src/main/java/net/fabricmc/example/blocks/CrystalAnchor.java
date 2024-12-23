package net.fabricmc.example.blocks;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CrystalAnchor extends Block implements PolymerTexturedBlock {

    private final BlockState polymerBlockState;

    public CrystalAnchor(Settings settings, String modelID) {
        super(settings);

        this.polymerBlockState = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK,
                PolymerBlockModel.of(new Identifier("cor", modelID)));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return this.polymerBlockState;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        System.out.println("Random display tick was called ");
        // if (!world.isClient) {
        // System.out.println("Hello Particles!");
        // ServerWorld sWorld = (ServerWorld) world;
        // showParticles(sWorld, pos, random);
        // }

        for (int i = 0; i < 4; ++i) {
            double d = (double) pos.getX() + random.nextDouble();
            double e = (double) pos.getY() + random.nextDouble();
            double f = (double) pos.getZ() + random.nextDouble();
            double g = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double h = ((double) random.nextFloat() - 0.5D) * 0.5D;
            double j = ((double) random.nextFloat() - 0.5D) * 0.5D;
            int k = random.nextInt(2) * 2 - 1;
            if (!world.getBlockState(pos.west()).isOf(this) && !world.getBlockState(pos.east()).isOf(this)) {
                d = (double) pos.getX() + 0.5D + 0.25D * (double) k;
                g = (double) (random.nextFloat() * 2.0F * (float) k);
            } else {
                f = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
                j = (double) (random.nextFloat() * 2.0F * (float) k);
            }

            world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    private void showParticles(ServerWorld world, BlockPos pos, Random random) {
        for (int i = 0; i < 360; i += 20) {
            double angle = Math.toRadians(i + (world.getTime() % 360)); // Make particles spin
            double xOffset = 0.5D + 0.5D * MathHelper.cos((float) angle); // Spiral or circular pattern
            double zOffset = 0.5D + 0.5D * MathHelper.sin((float) angle);

            Vec3d particlePos = new Vec3d(pos.getX() + xOffset, pos.getY() + 1.5D, pos.getZ() + zOffset); // '1.5D' for
                                                                                                          // above the
                                                                                                          // block

            ParticleS2CPacket packet = new ParticleS2CPacket(ParticleTypes.HAPPY_VILLAGER, true, particlePos.x,
                    particlePos.y, particlePos.z, 0, 0, 0, 1, 0);
            for (var player : world.getPlayers(p -> p.squaredDistanceTo(particlePos) < 256)) {
                player.networkHandler.sendPacket(packet);
            }
        }
    }

}
