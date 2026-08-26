package kniumm.metachest.client;

import kniumm.metachest.MetaChest;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;

public class MetaChestRenderer {
    public static final SpriteId ATTUNED_ENDER_CHEST_SPRITE =
            new SpriteId(
                    Sheets.CHEST_SHEET,
                    Identifier.fromNamespaceAndPath(
                            MetaChest.MOD_ID,
                            "entity/chest/attuned_ender_chest"
                    )
            );
}
