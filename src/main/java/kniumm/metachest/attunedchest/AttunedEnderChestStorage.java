package kniumm.metachest.attunedchest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kniumm.metachest.MetaChest;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttunedEnderChestStorage extends SavedData {
    public record SerializedItem(
            int slot,
            ItemStack item
    ) {
        public static final Codec<AttunedEnderChestStorage.SerializedItem> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.INT.fieldOf("slot")
                                        .forGetter(AttunedEnderChestStorage.SerializedItem::slot),
                                ItemStack.CODEC.fieldOf("item")
                                        .forGetter(AttunedEnderChestStorage.SerializedItem::item)
                        ).apply(instance, AttunedEnderChestStorage.SerializedItem::new)
                );
    }

    public static final int SIZE = 9 * 3;

    private final HashMap<String, NonNullList<ItemStack>> inventories;

    public AttunedEnderChestStorage() {
        this.inventories = HashMap.newHashMap(20);

        this.inventories.replaceAll((k, v) -> NonNullList.withSize(SIZE, ItemStack.EMPTY));
    }

    public AttunedEnderChestStorage(@NonNull HashMap<String, NonNullList<SerializedItem>> serializedInventories) {
        this();

        for (String key : serializedInventories.keySet()) {
            NonNullList<SerializedItem> serializedItems = serializedInventories.get(key);
            NonNullList<ItemStack> inventory = NonNullList.withSize(SIZE, ItemStack.EMPTY);

            for (SerializedItem serializedItem : serializedItems) {
                inventory.set(serializedItem.slot, serializedItem.item);
            }

            this.inventories.put(key, inventory);
        }
    }

    private static final int MAX_KEY_LENGTH = 64;

    public static final Codec<String> KEY_CODEC = Codec.STRING
            .validate(s -> s.length() <= MAX_KEY_LENGTH
                    ? DataResult.success(s)
                    : DataResult.error(() ->
                    "Inventory name is too long (" + s.length() +
                            ", max " + MAX_KEY_LENGTH + ")"));

    public static final Codec<AttunedEnderChestStorage> CODEC =
            Codec.unboundedMap(
                    KEY_CODEC,
                    Codec.list(SerializedItem.CODEC)
            ).xmap(
                    map -> {
                        HashMap<String, NonNullList<SerializedItem>> result =
                                HashMap.newHashMap(map.size());

                        map.forEach((key, list) -> {
                            NonNullList<SerializedItem> items = NonNullList.createWithCapacity(list.size());

                            items.addAll(list);
                            result.put(key, items);
                        });

                        return new AttunedEnderChestStorage(result);
                    },
                    inventory -> {
                        Map<String, List<SerializedItem>> result =
                                HashMap.newHashMap(inventory.inventories.size());

                        for (String key : inventory.inventories.keySet()) {
                            result.put(key, inventory.getSerializedInventory(key));
                        }

                        return result;
                    }
            );

    private @NonNull List<SerializedItem> getSerializedInventory(String key) {
        List<ItemStack> inventory = this.inventories.get(key);
        List<SerializedItem> result = new ArrayList<>();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack item = inventory.get(i);

            if (!item.isEmpty()) {
                result.add(new SerializedItem(i, item));
            }
        }

        return result;
    }

    public NonNullList<ItemStack> getInventory(String key) {
        return inventories.computeIfAbsent(key, _ -> {
            setDirty();
            return NonNullList.withSize(SIZE, ItemStack.EMPTY);
        });
    }

    public void setInventory(String key, NonNullList<ItemStack> items) {
        inventories.put(key, items);
        setDirty();
    }

    private static final SavedDataType<AttunedEnderChestStorage> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            MetaChest.MOD_ID,
                            "attuned_ender_chest"
                    ),
                    AttunedEnderChestStorage::new,
                    CODEC,
                    null
            );

    public static @NonNull AttunedEnderChestStorage get(@NonNull MinecraftServer server) {
        ServerLevel overworld = server.overworld();

        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }
}
