package kniumm.metachest.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jspecify.annotations.NonNull;

public class MetaChestDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(@NonNull FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(MetaChestRecipeProvider::new);
        pack.addProvider(MetaChestAdvancementProvider::new);
    }
}
