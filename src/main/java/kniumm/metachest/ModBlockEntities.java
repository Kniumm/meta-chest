package kniumm.metachest;

import kniumm.metachest.attunedchest.AttunedEnderChestBlockEntity;
import kniumm.metachest.metachest.MetaChestBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import org.jspecify.annotations.NonNull;

public class ModBlockEntities {
    public static final BlockEntityType<MetaChestBlockEntity> META_CHEST_ENTITY =
            register("meta_chest", MetaChestBlockEntity::new, ModBlocks.META_CHEST);

    public static final BlockEntityType<AttunedEnderChestBlockEntity> ATTUNED_ENDER_CHEST_ENTITY =
            register("attuned_meta_chest", AttunedEnderChestBlockEntity::new, ModBlocks.ATTUNED_ENDER_CHEST);

    private static <T extends BlockEntity> @NonNull BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(MetaChest.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {
    }
}