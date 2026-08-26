package kniumm.metachest.client.mixin;

import kniumm.metachest.client.ChestRenderStateExtension;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChestRenderState.class)
public class ChestRenderStateMixin implements ChestRenderStateExtension {
	@Unique
	private SpriteId metachest$customSprite;

	@Override
    public SpriteId metachest$getCustomSprite() {
		return metachest$customSprite;
	}

	@Override
    public void metachest$setCustomSprite(SpriteId sprite) {
		this.metachest$customSprite = sprite;
	}
}