package kniumm.metachest.client.datagen;

import kniumm.metachest.MetaChest;
import kniumm.metachest.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MetaChestAdvancementProvider extends FabricAdvancementProvider {
    protected MetaChestAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.@NonNull Provider wrapperLookup, @NonNull Consumer<AdvancementHolder> consumer) {
        AdvancementHolder findEndCity = Advancement.Builder.advancement().build(
                Identifier.withDefaultNamespace("end/find_end_city")
        );

        AdvancementHolder getEnderKey = Advancement.Builder.advancement()
                .parent(findEndCity)
                .display(
                        ModItems.ENDER_KEY,
                        Component.literal("The Key at the End of the Game"),
                        Component.literal("Find an Ender Key"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("got_ender_key", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENDER_KEY))
                .save(consumer, Identifier.fromNamespaceAndPath(MetaChest.MOD_ID, "get_ender_key"));
    }
}