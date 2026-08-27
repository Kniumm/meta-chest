package kniumm.metachest.metachest;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import kniumm.metachest.MetaItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MetaChestInventory {
    public record SerializedItem(
            int slot,
            MetaItem meta
    ) {
        public static final Codec<SerializedItem> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.INT.fieldOf("slot")
                                        .forGetter(SerializedItem::slot),
                                MetaItem.CODEC.fieldOf("meta")
                                        .forGetter(SerializedItem::meta)
                        ).apply(instance, SerializedItem::new)
                );
    }

    public static final int SIZE = 9 * 6;

    private final NonNullList<MetaItem> items;
    private final HashBiMap<Item, Integer> itemMap;

    public MetaChestInventory() {
        this.items = NonNullList.withSize(SIZE, MetaItem.EMPTY);
        this.itemMap = HashBiMap.create(SIZE);
    }

    private MetaChestInventory(
            int size,
            @NonNull List<SerializedItem> serializedItems
    ) {
        this();

        for (SerializedItem item : serializedItems) {
            item.meta.getStack().setCount(1);
            this.set(item.slot, item.meta);
        }
    }

    public static final Codec<MetaChestInventory> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("size")
                                    .forGetter(MetaChestInventory::size),

                            SerializedItem.CODEC.listOf()
                                    .fieldOf("items")
                                    .forGetter(MetaChestInventory::getSerializedItems)
                    ).apply(
                            instance,
                            MetaChestInventory::new
                    )
            );

    public NonNullList<MetaItem> getItems() {
        return items;
    }

    public HashBiMap<Item, Integer> getItemMap() {
        return itemMap;
    }

    private @NonNull List<SerializedItem> getSerializedItems() {
        List<SerializedItem> result = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            MetaItem meta = items.get(i);

            if (!meta.getStack().isEmpty()) {
                result.add(new SerializedItem(i, meta));
            }
        }

        return result;
    }

    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        MetaItem meta = items.get(slot);
        Item inputItem = meta.getStack().getItem();

        if (this.itemMap.containsValue(slot)) {
            Item currentItem = this.itemMap.inverse().get(slot);

            return currentItem == inputItem;
        }

        return true;
    }

    public void set(int slot, @NonNull MetaItem meta) {
        Item item = meta.getStack().getItem();

        if (item == Items.AIR) {
            MetaItem current = items.get(slot);

            if (current.getCount() == 0) {
                this.itemMap.remove(itemMap.inverse().get(slot));
            } else {
                return;
            }
        } else if (!this.itemMap.containsKey(item) && !this.itemMap.containsValue(slot)) {
            this.itemMap.put(item, slot);
        }

        items.set(slot, meta);
    }

    public int size() {
        return items.size();
    }
}