package kniumm.metachest;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

public class ModLootTables {
    public static void initialize() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin()
                    && key.identifier().equals(Identifier.fromNamespaceAndPath(
                    "minecraft",
                    "chests/end_city_treasure"
            ))) {
                tableBuilder.withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(ModItems.ENDER_KEY)
                                        .when(LootItemRandomChanceCondition.randomChance(0.10f))
                                )
                );
            }
        });
    }
}