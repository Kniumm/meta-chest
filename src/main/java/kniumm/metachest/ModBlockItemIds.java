package kniumm.metachest;

import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class ModBlockItemIds {
	public static final BlockItemId META_CHEST = create("meta_chest");

	private static @NonNull BlockItemId create(String name) {
		Identifier id = Identifier.fromNamespaceAndPath(MetaChest.MOD_ID, name);
		return BlockItemId.create(id, id);
	}
}
