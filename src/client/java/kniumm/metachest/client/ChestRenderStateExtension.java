package kniumm.metachest.client;

import net.minecraft.client.resources.model.sprite.SpriteId;

public interface ChestRenderStateExtension {
    void metachest$setCustomSprite(SpriteId sprite);

    SpriteId metachest$getCustomSprite();
}