package kniumm.metachest.client.datagen;

import java.util.concurrent.CompletableFuture;

import kniumm.metachest.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;

public class MetaChestRecipeProvider extends FabricRecipeProvider {
    public MetaChestRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registryLookup, @NonNull RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shaped(RecipeCategory.MISC, ModBlocks.META_CHEST)
                        .pattern("dod")
                        .pattern("ses")
                        .pattern("dsd")
                        .define('d', Items.DIAMOND)
                        .define('o', Items.ENDER_EYE)
                        .define('s', ItemTags.SHULKER_BOXES)
                        .define('e', Items.ENDER_CHEST)
                        .unlockedBy(getHasName(Items.ENDER_CHEST), has(Items.ENDER_CHEST))
                        .save(output);
            }
        };
    }

    @Override
    public @NonNull String getName() {
        return "MetaChestRecipeProvider";
    }
}