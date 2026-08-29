package kniumm.metachest.client;

import kniumm.libsalad.client.ChestRenderers;
import kniumm.metachest.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;

public class MetaChestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModChestRenderProviders.initialize();

		ChestRenderers.register(ModBlockEntities.ATTUNED_ENDER_CHEST_ENTITY, ModChestRenderProviders.ATTUNED_ENDER_CHEST);
	}
}