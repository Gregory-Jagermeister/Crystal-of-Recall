package net.fabricmc.example;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.*;

public class StateSaverAndLoader extends PersistentState {

    private HashMap<UUID, List<Anchor>> playerAnchors = new HashMap<>();

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup regLookup){
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound playersNbt = tag.getCompound("playerAnchors");
        playersNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            NbtCompound anchorsNbt = playersNbt.getCompound(key);
            List<Anchor> anchors = new ArrayList<>();

            anchorsNbt.getKeys().forEach(anchorKey -> {
                NbtCompound anchorNbt = anchorsNbt.getCompound(anchorKey);
                BlockPos pos = new BlockPos(anchorNbt.getInt("x"), anchorNbt.getInt("y"), anchorNbt.getInt("z"));
                String blockName = anchorNbt.getString("blockName");
                RegistryKey<World> dimension = RegistryKey.of(RegistryKey.ofRegistry(Identifier.of("world")), Identifier.of(anchorNbt.getString("dimension")));

                anchors.add(new Anchor(pos, blockName, dimension));
            });
            state.playerAnchors.put(uuid, anchors);
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

    public void addPlayerAnchor(UUID playerId, Anchor anchor) {
        this.playerAnchors.computeIfAbsent(playerId, k -> new ArrayList<>()).add(anchor);
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        // Save player anchors
        NbtCompound playersNbt = new NbtCompound();
        playerAnchors.forEach((uuid, anchors) -> {
            NbtCompound anchorsNbt = new NbtCompound();

            for (int i = 0; i < anchors.size(); i++) {
                Anchor anchor = anchors.get(i);
                NbtCompound anchorNbt = new NbtCompound();
                anchorNbt.putInt("x", anchor.getPosition().getX());
                anchorNbt.putInt("y", anchor.getPosition().getY());
                anchorNbt.putInt("z", anchor.getPosition().getZ());
                anchorNbt.putString("blockName", anchor.getBlockName());
                anchorNbt.putString("dimension", anchor.getDimension().getValue().toString());

                anchorsNbt.put("anchor" + i, anchorNbt);
            }
            playersNbt.put(uuid.toString(), anchorsNbt);
        });
        nbt.put("playerAnchors", playersNbt);
        return nbt;
    }

    public void removePlayerAnchor(UUID uuid, BlockPos targetPos) {
        List<Anchor> anchors = playerAnchors.get(uuid);
        if (anchors != null) {
            anchors.removeIf(anchor -> anchor.getPosition().equals(targetPos));
        }
        markDirty();
    }

    public List<Anchor> getPlayerAnchors(UUID playerId) {
        return this.playerAnchors.getOrDefault(playerId, new ArrayList<>());
    }
}
