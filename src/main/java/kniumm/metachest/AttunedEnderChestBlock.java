package kniumm.metachest;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AttunedEnderChestBlock extends AbstractChestBlock<AttunedEnderChestBlockEntity> implements SimpleWaterloggedBlock {
    public static final MapCodec<AttunedEnderChestBlock> CODEC = simpleCodec(AttunedEnderChestBlock::new);
    public static final EnumProperty<Direction> FACING;
    public static final BooleanProperty WATERLOGGED;
    private static final VoxelShape SHAPE;

    public @NonNull MapCodec<AttunedEnderChestBlock> codec() {
        return CODEC;
    }

    public AttunedEnderChestBlock(final BlockBehaviour.Properties properties) {
        super(properties, () -> ModBlockEntities.ATTUNED_ENDER_CHEST_ENTITY);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    public DoubleBlockCombiner.@NonNull NeighborCombineResult<? extends ChestBlockEntity> combine(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final boolean ignoreBeingBlocked) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    protected @NonNull VoxelShape getShape(final @NonNull BlockState state, final @NonNull BlockGetter level, final @NonNull BlockPos pos, final @NonNull CollisionContext context) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(final @NonNull BlockPlaceContext context) {
        FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
        return (this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
    }

    @Override
    protected @NonNull InteractionResult useItemOn(
            @NonNull ItemStack stack,
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull Player player,
            @NonNull InteractionHand hand,
            @NonNull BlockHitResult hit
    ) {
        Component custom_name = stack.getCustomName();

        if (stack.is(ModItems.ENDER_KEY) && custom_name != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (level.getBlockEntity(pos) instanceof AttunedEnderChestBlockEntity chest) {
                chest.setKey(custom_name.getString());
            }
        }

        return this.useWithoutItem(state, level, pos, player, hit);
    }

    protected @NonNull InteractionResult useWithoutItem(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull Player player, final @NonNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof AttunedEnderChestBlockEntity enderChest) {
            BlockPos above = pos.above();
            if (!level.getBlockState(above).isRedstoneConductor(level, above)) {
                if (level instanceof ServerLevel serverLevel) {
                    player.openMenu(enderChest);
                    player.awardStat(Stats.OPEN_ENDERCHEST);
                    PiglinAi.angerNearbyPiglins(serverLevel, player, true);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    public BlockEntity newBlockEntity(final @NonNull BlockPos worldPosition, final @NonNull BlockState blockState) {
        return new AttunedEnderChestBlockEntity(worldPosition, blockState);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final @NonNull Level level, final @NonNull BlockState blockState, final @NonNull BlockEntityType<T> type) {
        return level.isClientSide() ? createTickerHelper(type, ModBlockEntities.ATTUNED_ENDER_CHEST_ENTITY, AttunedEnderChestBlockEntity::lidAnimateTick) : null;
    }

    public void animateTick(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull RandomSource random) {
        for(int i = 0; i < 3; ++i) {
            int flipX = random.nextInt(2) * 2 - 1;
            int flipZ = random.nextInt(2) * 2 - 1;
            double x = (double)pos.getX() + (double)0.5F + (double)0.25F * (double)flipX;
            double y = ((float)pos.getY() + random.nextFloat());
            double z = (double)pos.getZ() + (double)0.5F + (double)0.25F * (double)flipZ;
            double xa = (random.nextFloat() * (float)flipX);
            double ya = ((double)random.nextFloat() - (double)0.5F) * (double)0.125F;
            double za = (random.nextFloat() * (float)flipZ);
            level.addParticle(ParticleTypes.PORTAL, x, y, z, xa, ya, za);
        }

    }

    protected @NonNull BlockState rotate(final @NonNull BlockState state, final @NonNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected @NonNull BlockState mirror(final @NonNull BlockState state, final @NonNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(final StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    protected @NonNull FluidState getFluidState(final @NonNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    protected @NonNull BlockState updateShape(final @NonNull BlockState state, final @NonNull LevelReader level, final @NonNull ScheduledTickAccess ticks, final @NonNull BlockPos pos, final @NonNull Direction directionToNeighbour, final @NonNull BlockPos neighbourPos, final @NonNull BlockState neighbourState, final @NonNull RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    protected boolean isPathfindable(final @NonNull BlockState state, final @NonNull PathComputationType type) {
        return false;
    }

    protected void tick(final @NonNull BlockState state, final @NonNull ServerLevel level, final @NonNull BlockPos pos, final @NonNull RandomSource random) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AttunedEnderChestBlockEntity attunedEnderChestBlockEntity) {
            attunedEnderChestBlockEntity.recheckOpen();
        }

    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE = Block.column(14.0F, 0.0F, 14.0F);
    }
}