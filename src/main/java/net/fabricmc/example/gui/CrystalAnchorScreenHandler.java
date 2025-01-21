package net.fabricmc.example.gui;

import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CrystalAnchorScreenHandler extends SimpleGui {

    /**
     * Constructs a new simple container gui for the supplied player.
     *
     * @param type                  the screen handler that the client should display
     * @param player                the player to server this gui to
     * @param manipulatePlayerSlots if <code>true</code> the players inventory
     *                              will be treated as slots of this gui
     */
    public CrystalAnchorScreenHandler(ServerPlayerEntity player, List<BlockPos> anchors) {
        super(ScreenHandlerType.STONECUTTER, player, false);
        this.setTitle(Text.literal("Anchor Selection"));
    }


}
