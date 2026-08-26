package kniumm.metachest.client;

import kniumm.metachest.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.ChestRenderer;

public class MetaChestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(
				ModBlockEntities.ATTUNED_ENDER_CHEST_ENTITY,
				ChestRenderer::new
		);
	}
}