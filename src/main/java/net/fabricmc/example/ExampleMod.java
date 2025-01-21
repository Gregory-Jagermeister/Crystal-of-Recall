package net.fabricmc.example;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.elements.*;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import eu.pb4.sgui.api.gui.HotbarGui;
import eu.pb4.sgui.api.gui.SimpleGui;
import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.gui.layered.Layer;
import eu.pb4.sgui.api.gui.layered.LayerView;
import eu.pb4.sgui.api.gui.layered.LayeredGui;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.blocks.CrystalAnchor;
import net.fabricmc.example.blocks.ModBlocks;
import net.fabricmc.example.blocks.blockEntities.CrystalAnchorEntity;
import net.fabricmc.example.blocks.blockEntities.ModBlockEntities;
import net.fabricmc.example.items.*;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.MossBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.

	public static final String MOD_ID = "cor";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.markAsRequired();
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		// ITEMS
		ModItems.initialize();

		// BLOCKS
		ModBlocks.initialize();
		ModBlockEntities.initialize();

		//CALLBACKS
		UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
			if (!world.isClient && player.getStackInHand(hand).getItem() == ModItems.UPGRADED_RECALL_CRYSTAL) {
				// Example: only act on your specific block
				if (world.getBlockState(hitResult.getBlockPos()).getBlock() == ModBlocks.CRYSTAL_ANCHOR) {

					StateSaverAndLoader state = StateSaverAndLoader.getServerState(Objects.requireNonNull(world.getServer()));
					PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

					playerData.anchorPoints.add(hitResult.getBlockPos());
					playerData.anchorNames.add(world.getBlockState(hitResult.getBlockPos()).getBlock().getName().getString());

					state.markDirty(); // Ensure changes are saved
					System.out.println("Hello world");
					return ActionResult.SUCCESS;
				}
			}
			return ActionResult.PASS;
		}));

		//NETWORKING

	}
}
