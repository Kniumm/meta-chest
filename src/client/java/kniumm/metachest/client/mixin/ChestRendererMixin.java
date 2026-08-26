package kniumm.metachest.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kniumm.metachest.AttunedEnderChestBlockEntity;
import kniumm.metachest.client.ChestRenderStateExtension;
import kniumm.metachest.client.MetaChestRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestRenderer.class)
public class ChestRendererMixin {
    @Inject(
            method = "extractRenderState*",
            at = @At("TAIL")
    )
    private void metachest$setSprite(
            BlockEntity blockEntity,
            ChestRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.CrumblingOverlay breakProgress,
            CallbackInfo ci
    ) {
        if (blockEntity instanceof AttunedEnderChestBlockEntity) {
            ((ChestRenderStateExtension) state).metachest$setCustomSprite(
                    MetaChestRenderer.ATTUNED_ENDER_CHEST_SPRITE
            );
        }
    }

    @ModifyExpressionValue(
            method = "submit*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Sheets;chooseSprite(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/sprite/SpriteId;"
            )
    )
    private SpriteId metachest$replaceSprite(
            SpriteId original,
            ChestRenderState state
    ) {
        SpriteId custom =
                ((ChestRenderStateExtension) state).metachest$getCustomSprite();

        return custom != null ? custom : original;
    }
}