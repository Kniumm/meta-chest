package kniumm.metachest;

import kniumm.metachest.attunedchest.AttunedEnderChestBlock;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public class ModBlocks {
	public static final Block META_CHEST = register(
			ModBlockItemIds.META_CHEST,
			MetaChestBlock::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST)
	);

	public static final Block ATTUNED_ENDER_CHEST = register(
			ModBlockItemIds.ATTUNED_ENDER_CHEST,
			AttunedEnderChestBlock::new,
			BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST)
	);

	private static @NonNull Block register(ResourceKey<Block> id, @NonNull Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.@NonNull Properties properties) {
		Block block = blockFactory.apply(properties.setId(id));

		return Registry.register(BuiltInRegistries.BLOCK, id, block);
	}

	private static @NonNull Block register(@NonNull BlockItemId id, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
		Block block = register(id.block(), blockFactory, properties);

		BlockItem blockItem = new BlockItem(block, new Item.Properties().useBlockDescriptionPrefix().setId(id.item()));
		Registry.register(BuiltInRegistries.ITEM, id.item(), blockItem);

		return block;
	}

	public static void initialize() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((creativeTab) -> {
			creativeTab.accept(ModBlocks.META_CHEST.asItem());
			creativeTab.accept(ModBlocks.ATTUNED_ENDER_CHEST.asItem());
		});
	}
}
