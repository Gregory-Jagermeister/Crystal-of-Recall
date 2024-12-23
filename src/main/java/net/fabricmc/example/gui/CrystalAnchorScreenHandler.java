package net.fabricmc.example.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class CrystalAnchorScreenHandler extends ScreenHandler {

    protected CrystalAnchorScreenHandler(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canUse'");
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'quickMove'");
    }

}
