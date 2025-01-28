package net.fabricmc.example.items;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.fabricmc.example.Anchor;
import net.fabricmc.example.Exceptions.DragonDefeatedException;
import net.fabricmc.example.Exceptions.NoBedSpawnSetException;
import net.fabricmc.example.Exceptions.RespawnAnchorSetException;
import net.fabricmc.example.StateSaverAndLoader;
import net.fabricmc.example.blocks.CrystalAnchor;
import net.fabricmc.example.blocks.blockEntities.CrystalAnchorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
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

import java.util.HashMap;
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
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            // Check if the block is an instance of CrystalAnchor
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (block instanceof CrystalAnchor) {

                // Check if there are at least two empty spaces above the block
                if (!isSpaceAboveClear(world, blockPos)) {
                    player.sendMessage(Text.literal("Warp cannot be added: Not enough space above!").formatted(Formatting.RED), true);
                    return ActionResult.FAIL;
                }

                // Get block name (if it has a custom name in a component, otherwise generic)
                String blockName = "";
                CrystalAnchorEntity blockEntity = (CrystalAnchorEntity) world.getBlockEntity(blockPos);
                if (blockEntity.getComponents().contains(DataComponentTypes.CUSTOM_NAME)){
                    blockName = blockEntity.getComponents().get(DataComponentTypes.CUSTOM_NAME).getString();
                }else {
                    blockName = block.asItem().getName().getString();
                }

                RegistryKey<World> dimension = world.getRegistryKey();
                Anchor anchor = new Anchor(blockPos, blockName, dimension);

                // Get the persistent state and store the block position and name
                StateSaverAndLoader state = StateSaverAndLoader.getServerState(serverPlayer.getServer());
                state.addPlayerAnchor(player.getUuid(), anchor);

                player.sendMessage(Text.literal("Warp added: " + blockName + " at " + blockPos), false);
                return ActionResult.SUCCESS; // Success only if interaction was with CrystalAnchor
            }
        }

        return ActionResult.PASS; // Pass if the block isn't a CrystalAnchor
    }

    // Check if the two blocks above the given position are clear (i.e., air)
    private boolean isSpaceAboveClear(World world, BlockPos pos) {
        // Check the two blocks above the current block
        BlockState blockAbove1 = world.getBlockState(pos.up());
        BlockState blockAbove2 = world.getBlockState(pos.up(2));

        // Verify both blocks are non-solid (typically air)
        return !blockAbove1.isSolidBlock(world, pos.up()) && !blockAbove2.isSolidBlock(world, pos.up(2));
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

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Retrieve the list of warps for the player
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(serverPlayer.getServer());
            List<Anchor> playerWarps = state.getPlayerAnchors(serverPlayer.getUuid());

            // Open the GUI to display warp points
            openAnchorSelectorGui(serverPlayer, playerWarps);
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
                Text.translatable("Right click to Open")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("the Teleport Menu.")
                        .formatted(Formatting.DARK_GRAY));
        tooltip.add(
                Text.translatable("")
                        .formatted(Formatting.DARK_GRAY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    public void openAnchorSelectorGui(ServerPlayerEntity player, List<Anchor> anchors) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public boolean onClick(int index, ClickType type, SlotActionType action, GuiElementInterface element) {
                // Handle what happens when a slot is clicked, like teleporting, etc.
                return super.onClick(index, type, action, element);
            }

            @Override
            public boolean canPlayerClose() {
                return true; // Players can close the GUI
            }
        };

        gui.setTitle(Text.literal("Select Anchor"));

        // Populate the GUI with anchor entries
        int index = 0;
        for (Anchor anchor : anchors) {
            if (index >= 26) break;  // Limit to 26 anchors in this example for GUI slots

            String warpName = anchor.getBlockName();
            BlockPos pos = anchor.getPosition();
            RegistryKey<World> dimension = anchor.getDimension();

            // Representation of anchor location and dimension
            String displayName = String.format("%s at %s in %s", warpName, pos.toShortString(), dimension.getValue().toString());

            gui.setSlot(index, new GuiElementBuilder(Items.ENDER_PEARL)
                    .setName(Text.literal(displayName))
                    .setCallback((slotIndex, clickType, slotActionType) -> teleportToAnchor(player, anchor))
            );
            index++;
        }
            // Optional Slot for Respawn Point
        // Add respawn point option
        gui.setSlot(26, new GuiElementBuilder(Items.ENDER_EYE)
                .setName(Text.literal("Respawn Point"))
                .setCallback((slotIndex, clickType, slotActionType) -> teleportToRespawn(player))
        );


        gui.open();
    }

    private void teleportToAnchor(ServerPlayerEntity player, Anchor anchor) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        RegistryKey<World> anchorDimension = anchor.getDimension();
        ServerWorld targetWorld = server.getWorld(anchorDimension);
        BlockPos anchorPos = anchor.getPosition();

        if (targetWorld != null) {
            try {
                if (!isSpaceAboveClear(targetWorld, anchorPos)) {
                    player.sendMessage(Text.literal("Cannot Teleport: Not enough space above!").formatted(Formatting.RED), true);
                    return;
                }

                // Check for Ender Dragon defeat conditions if in the END
                if (anchorDimension == World.END) {
                    isDragonDefeated(targetWorld);
                }

                // Teleport player between dimensions if necessary
                if (player.getWorld() != targetWorld) {
                    player.teleport(targetWorld, anchorPos.getX() + 0.5, anchorPos.getY() + 1, anchorPos.getZ() + 0.5, player.getYaw(), player.getPitch());
                } else {
                    player.requestTeleport(anchorPos.getX() + 0.5, anchorPos.getY() + 1, anchorPos.getZ() + 0.5);
                }
                triggerSuccessEffects(targetWorld, player);

            } catch (DragonDefeatedException e) {
                handleTeleportFailure(e.getMessage(), player.getServerWorld(), player);
            }

        } else {
            player.sendMessage(Text.literal("Warp failed: Target dimension not found.").formatted(Formatting.RED), false);
        }

        player.closeHandledScreen();
    }

    private void teleportToRespawn(ServerPlayerEntity player) {
        ServerWorld sWorld = player.getServerWorld();
        BlockPos respawnPoint = player.getSpawnPointPosition();

        try {
            ServerWorld endWorld = player.getServer().getWorld(World.END);
            // Check for Ender Dragon defeat conditions
            if (endWorld != null && player.getWorld() == endWorld) {
                isDragonDefeated(endWorld);
            }

            // Get overworld spawn or fall back
            respawnPoint = getOverworldSpawn(player);
            if (respawnPoint != null && player.isOnGround()) {
                // Teleport logic
                if (respawnPoint != null) {
                    player.requestTeleport(respawnPoint.getX(), respawnPoint.getY(), respawnPoint.getZ());
                    triggerSuccessEffects(sWorld, player);
                }
            }
        } catch (DragonDefeatedException | RespawnAnchorSetException | NoBedSpawnSetException e) {
            handleTeleportFailure(e.getMessage(), sWorld, player);
        } catch (Exception e) {
            handleTeleportFailure("Warp Failed", sWorld, player);
        }

        player.closeHandledScreen();
    }

    private void handleTeleportFailure(String message, ServerWorld world, PlayerEntity player) {
        player.sendMessage(Text.literal(message).formatted(Formatting.RED), true);
        world.spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 0.25, player.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.AMBIENT);
    }

    private void triggerSuccessEffects(ServerWorld world, PlayerEntity player) {
        world.spawnParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 0.25, player.getZ(), 100, 0.5, 0.5, 0.5, 0.1);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.AMBIENT);
        player.getItemCooldownManager().set(this, 200);   // Adding item cooldown
        player.incrementStat(Stats.USED.getOrCreateStat(this));
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
