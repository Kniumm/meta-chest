package kniumm.metachest;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class ModItems {
    public static final Item ENDER_KEY = register(ModItemIds.ENDER_KEY, Item::new, new Item.Properties().stacksTo(1).rarity(Rarity.RARE));

    public static Item register(ResourceKey<Item> itemKey, @NonNull Function<Item.Properties, Item> itemFactory, Item.@NonNull Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((creativeTab) -> {
            creativeTab.accept(ModItems.ENDER_KEY);
        });
    }
}