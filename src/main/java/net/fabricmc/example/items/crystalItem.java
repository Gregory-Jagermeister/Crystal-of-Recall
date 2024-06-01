package net.fabricmc.example.items;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.example.Exceptions.DragonDefeatedException;
import net.fabricmc.example.Exceptions.RespawnAnchorSetException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class crystalItem extends ToolItem implements PolymerItem {

    private final PolymerModelData model;

    public crystalItem(Item polymerItem, ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
        this.model = PolymerResourcePackUtils.requestModel(polymerItem, new Identifier("cor", "item/recall_crystal"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    public boolean isDragonDefeated(ServerWorld world) throws DragonDefeatedException {
        // Check if the Ender Dragon has been defeated
        if (world.getEnderDragonFight() != null && !(world.getEnderDragonFight().hasPreviouslyKilled())) {
            throw new DragonDefeatedException("The Energy wafting off the Ender Dragon interuptted your Warp.");
        }
        return false;
    }

    public BlockPos getOverworldSpawn(ServerPlayerEntity player) throws RespawnAnchorSetException {

        BlockPos spawnPos = player.getSpawnPointPosition();
        RegistryKey<World> spawnDimensionId = player.getSpawnPointDimension();

        if (spawnPos != null && !World.OVERWORLD.getValue().equals(spawnDimensionId.getValue())) {
            throw new RespawnAnchorSetException(
                    "The Crystal oozes with the power of the nether its too dangerous to use.");
        }

        if (spawnPos != null && World.OVERWORLD.getValue().equals(spawnDimensionId.getValue())) {
            return spawnPos;
        }

        return null;

    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        ServerWorld sWorld = (ServerWorld) world;
        if (!world.isClient) {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            BlockPos overworldSpawn = null;

            try {
                ServerWorld endWorld = player.getServer().getWorld(World.END);

                if (endWorld != null && player.getWorld() == endWorld) {
                    isDragonDefeated(endWorld);
                }

                overworldSpawn = getOverworldSpawn(player);

                if (overworldSpawn != null && player.isOnGround()) {
                    ServerWorld overworld = player.getServer().getWorld(World.OVERWORLD);

                    itemStack.setDamage(10);
                    sWorld.spawnParticles(ParticleTypes.GLOW, (double) user.getX(),
                            (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);

                    if (overworld != null && player.getWorld() != overworld) {
                        player.teleport(overworld, overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ(),
                                player.getYaw(), player.getPitch());
                    } else {
                        player.requestTeleport(overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ());
                    }
                    player.getItemCooldownManager().set(this, 200);
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    sWorld.spawnParticles(ParticleTypes.GLOW, (double) user.getX(),
                            (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
                            SoundCategory.AMBIENT);
                }
            } catch (DragonDefeatedException | RespawnAnchorSetException e) {
                user.sendMessage(Text.of(e.getMessage()), true);
                sWorld.spawnParticles(ParticleTypes.SMOKE, (double) user.getX(),
                        (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                // Handle teleportation failure (e.g., play a sound, add particle effects, etc.)
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.AMBIENT);
            } catch (Exception e) {
                user.sendMessage(Text.of("Warp Failed"), true);
                sWorld.spawnParticles(ParticleTypes.SMOKE, (double) user.getX(),
                        (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.AMBIENT);
            }
        } else {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            user.sendMessage(Text.of("Warp Failed"), true);
            sWorld.spawnParticles(ParticleTypes.SMOKE, (double) user.getX(),
                    (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK,
                    SoundCategory.AMBIENT);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void modifyClientTooltip(List<Text> tooltip, ItemStack stack, ServerPlayerEntity player) {
        tooltip.add(0, Text.translatable("There's no place like home!").formatted(Formatting.GOLD)
                .formatted(Formatting.ITALIC));
        tooltip.add(
                Text.translatable("")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("Right click to teleport back")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("to your bed or respawn anchor")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("")
                        .formatted(Formatting.DARK_GRAY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }

        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            float cooldown = playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0f);
            stack.setDamage((int) (cooldown * 10));
        }
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        return this.model.item();
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return model.value();
    }
}