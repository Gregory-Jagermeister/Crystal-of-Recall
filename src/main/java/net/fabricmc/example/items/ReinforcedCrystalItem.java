package net.fabricmc.example.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.example.Exceptions.DragonDefeatedException;
import net.fabricmc.example.Exceptions.NoBedSpawnSetException;
import net.fabricmc.example.Exceptions.RespawnAnchorSetException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReinforcedCrystalItem extends ToolItem implements PolymerItem {

    private final PolymerModelData model;

    public ReinforcedCrystalItem(Item polymerItem, ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
        this.model = PolymerResourcePackUtils.requestModel(polymerItem, Identifier.of("cor", "item/recall_crystal"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        return ActionResult.PASS;
    }

    public boolean isDragonDefeated(ServerWorld world) throws DragonDefeatedException {
        // Check if the Ender Dragon has been defeated
        if (world.getEnderDragonFight() != null && !(world.getEnderDragonFight().hasPreviouslyKilled())) {
            throw new DragonDefeatedException("The Energy wafting off the Ender Dragon interrupted your Warp.");
        }
        return false;
    }

    public BlockPos getOverworldSpawn(ServerPlayerEntity player)
            throws RespawnAnchorSetException, NoBedSpawnSetException {

        BlockPos spawnPos = player.getSpawnPointPosition();
        RegistryKey<World> spawnDimensionId = player.getSpawnPointDimension();

        if (spawnPos != null && !World.OVERWORLD.getValue().equals(spawnDimensionId.getValue())) {
            throw new RespawnAnchorSetException(
                    "The Crystal oozes with the power of the nether its too dangerous to use.");
        }

        if (spawnPos != null && World.OVERWORLD.getValue().equals(spawnDimensionId.getValue())) {
            return spawnPos;
        }

        throw new NoBedSpawnSetException("These is no place to call home for you! (set your spawn at a bed.)");

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

                    BlockPos test = new BlockPos(0, 200, 0);
                    List<BlockPos> anchorPoints = new ArrayList<BlockPos>(){{add(test);}};
                    ServerWorld overworld = player.getServer().getWorld(World.OVERWORLD);

                    itemStack.setDamage(10);
                    sWorld.spawnParticles(ParticleTypes.GLOW, (double) user.getX(),
                            (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);

//                    if (overworld != null && player.getWorld() != overworld) {
//                        player.teleport(overworld, overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ(),
//                                player.getYaw(), player.getPitch());
//                    } else {
//                        player.requestTeleport(overworldSpawn.getX(), overworldSpawn.getY(), overworldSpawn.getZ());
//                    }
                    openAnchorSelectorGui((ServerPlayerEntity)user, anchorPoints);
                    player.getItemCooldownManager().set(this, 200);
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    sWorld.spawnParticles(ParticleTypes.GLOW, (double) user.getX(),
                            (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK,
                            SoundCategory.AMBIENT);
                }
            } catch (DragonDefeatedException | RespawnAnchorSetException | NoBedSpawnSetException e) {
                user.sendMessage(Text.literal(e.getMessage()).formatted(Formatting.RED), true);
                sWorld.spawnParticles(ParticleTypes.SMOKE, (double) user.getX(),
                        (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                // Handle teleportation failure (e.g., play a sound, add particle effects, etc.)
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.AMBIENT);
            } catch (Exception e) {
                user.sendMessage(Text.literal("Warp Failed").formatted(Formatting.RED), true);
                sWorld.spawnParticles(ParticleTypes.SMOKE, (double) user.getX(),
                        (double) user.getY() + 0.25, (double) user.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK,
                        SoundCategory.AMBIENT);
            }
        } else {
            ServerPlayerEntity player = (ServerPlayerEntity) user;
            user.sendMessage(Text.literal("Warp Failed").formatted(Formatting.RED), true);
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
                Text.translatable("to your bed.")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("")
                        .formatted(Formatting.DARK_GRAY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    public void openAnchorSelectorGui(ServerPlayerEntity player, List<BlockPos> anchorPoints) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
                // Handle what happens when a slot is clicked, like teleporting, etc.
                player.sendMessage(Text.literal("You clicked slot: " + index), false);

                return super.onClick(index, type, action, element);
            }

            @Override
            public boolean canPlayerClose() {
                return true; // Players can close the GUI
            }
        };

        gui.setTitle(Text.literal("Select Anchor"));

        // Populate the GUI with anchor entries
        for (int i = 0; i < anchorPoints.size(); i++) {
            BlockPos anchor = anchorPoints.get(i);
            gui.setSlot(i, new GuiElementBuilder(Items.ENDER_PEARL)
                    .setName(Text.literal("Anchor " + (i + 1)))
                    .setCallback((index, clickType, actionType) -> teleportToAnchor(player, anchor))
            );
        }

        // Optional Slot for Respawn Point
        gui.setSlot(anchorPoints.size(), new GuiElementBuilder(Items.ENDER_EYE)
                .setName(Text.literal("Respawn Point"))
                .setCallback((index, clickType, actionType) -> teleportToRespawn(player))
        );

        gui.open();
    }

    private void teleportToAnchor(ServerPlayerEntity player, BlockPos anchor) {
        ServerWorld overworld = Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD);
        player.requestTeleport(anchor.getX(), anchor.getY(), anchor.getZ());
        player.closeHandledScreen();
        System.out.println("Would Teleport");
    }

    private void teleportToRespawn(ServerPlayerEntity player) {
        //ServerWorld overworld = player.getServer().getWorld(World.OVERWORLD);
        BlockPos respawnPoint = player.getSpawnPointPosition();
        if (respawnPoint != null) {
            player.requestTeleport(respawnPoint.getX(), respawnPoint.getY(), respawnPoint.getZ());
        }
        player.closeHandledScreen();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }

        if (entity instanceof PlayerEntity playerEntity) {
            float cooldown = playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0f);
            stack.setDamage((int) (cooldown * 10));
        }
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
