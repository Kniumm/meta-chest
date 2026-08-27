package kniumm.metachest.metachest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.NonNull;

public class MetaChestStorage extends SavedData {
    private final MetaChestInventory inventory;

    public MetaChestStorage() {
        this.inventory = new MetaChestInventory();
    }

    private MetaChestStorage(MetaChestInventory inventory) {
        this.inventory = inventory;
    }

    private static final Codec<MetaChestStorage> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MetaChestInventory.CODEC
                    .fieldOf("inventory").forGetter(
                            MetaChestStorage::getInventory
                    )
            ).apply(
                    instance,
                    MetaChestStorage::new
            )
    );

    public MetaChestInventory getInventory() {
        return inventory;
    }

    private static final SavedDataType<MetaChestStorage> TYPE =
        new SavedDataType<>(
                Identifier.fromNamespaceAndPath(
                        MetaChest.MOD_ID,
                        "meta_chest"
                ),
                MetaChestStorage::new,
                CODEC,
                null
    );

    public static @NonNull MetaChestStorage get(@NonNull MinecraftServer server) {
        ServerLevel overworld = server.overworld();

        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }
}