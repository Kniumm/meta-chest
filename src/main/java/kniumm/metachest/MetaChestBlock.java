package kniumm.metachest;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class MetaChestBlock extends BaseEntityBlock {
    private static final SoundEvent OPEN_SOUND = SoundEvents.ENDER_CHEST_OPEN;
    private static final SoundEvent CLOSE_SOUND = SoundEvents.ENDER_CHEST_CLOSE;

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    public MetaChestBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hit) {
        if (level instanceof ServerLevel serverLevel) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MetaChestBlockEntity metaChest) {
                player.openMenu(metaChest);
                PiglinAi.angerNearbyPiglins(serverLevel, player, true);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(MetaChestBlock::new);
    }

    @Override
    public BlockState getStateForPlacement(final @NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(OPEN, false);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new MetaChestBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN);
    }

    public SoundEvent getOpenChestSound() {
        return OPEN_SOUND;
    }
    public SoundEvent getCloseChestSound() {
        return CLOSE_SOUND;
    }
}