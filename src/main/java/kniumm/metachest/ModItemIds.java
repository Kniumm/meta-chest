package kniumm.metachest;

import kniumm.metachest.metachest.MetaChest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

public class ModItemIds {
    public static final ResourceKey<Item> ENDER_KEY = create("ender_key");

    private static @NonNull ResourceKey<Item> create(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(MetaChest.MOD_ID, name);
        return ResourceKey.create(Registries.ITEM, id);
    }
}
