package net.fabricmc.example;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {

    public HashMap<UUID, PlayerData> players = new HashMap<>();

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup regLookup){
        StateSaverAndLoader state = new StateSaverAndLoader();
        // Load logic for other attributes...

        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();
            NbtCompound playerNbt = playersNbt.getCompound(key);

            // Load dirtBlocksBroken and other data...

            // Load anchors
            NbtList anchorsNbt = playerNbt.getList("anchors", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : anchorsNbt) {
                NbtCompound anchorNbt = (NbtCompound) element;
                BlockPos pos = new BlockPos(
                        anchorNbt.getInt("x"),
                        anchorNbt.getInt("y"),
                        anchorNbt.getInt("z")
                );
                String name = anchorNbt.getString("name");
                playerData.anchorPoints.add(pos);
                playerData.anchorNames.add(name);
            }

            // Add playerData to the persistent state's players map
            state.players.put(UUID.fromString(key), playerData);
        });

        return state;
    }

    public static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new,
            StateSaverAndLoader::createFromNbt,
            null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server){
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, ExampleMod.MOD_ID);
        state.markDirty();

        return state;
    }


    public static PlayerData getPlayerState(LivingEntity player){
        StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));

        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();

            NbtList anchorsNbt = new NbtList();
            for (int i = 0; i < playerData.anchorPoints.size(); i++) {
                BlockPos pos = playerData.anchorPoints.get(i);
                NbtCompound anchorNbt = new NbtCompound();
                anchorNbt.putInt("x", pos.getX());
                anchorNbt.putInt("y", pos.getY());
                anchorNbt.putInt("z", pos.getZ());
                anchorNbt.putString("name", playerData.anchorNames.get(i));
                anchorsNbt.add(anchorNbt);
            }
            playerNbt.put("anchors", anchorsNbt);
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }
}
