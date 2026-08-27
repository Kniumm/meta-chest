package kniumm.metachest;

import kniumm.metachest.metachest.MetaChest;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ModBlockItemIds {
	public static final BlockItemId META_CHEST = create("meta_chest");
	public static final BlockItemId ATTUNED_ENDER_CHEST = create("attuned_ender_chest");

	private static @NonNull BlockItemId create(String name) {
		Identifier id = Identifier.fromNamespaceAndPath(MetaChest.MOD_ID, name);
		return BlockItemId.create(id, id);
	}
}
