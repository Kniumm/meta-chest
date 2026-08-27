package kniumm.metachest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.WorldlyContainer;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class MetaChestBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private final ContainerOpenersCounter openersCounter;

    public MetaChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.META_CHEST_ENTITY, pos, state);

        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState blockState) {
                Block block = blockState.getBlock();

                if (block instanceof MetaChestBlock chestBlock) {
                    MetaChestBlockEntity.this.playSound(blockState, chestBlock.getOpenChestSound());
                    MetaChestBlockEntity.this.updateBlockState(state, true);
                }
            }

            protected void onClose(final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState blockState) {
                Block block = blockState.getBlock();

                if (block instanceof MetaChestBlock chestBlock) {
                    MetaChestBlockEntity.this.playSound(blockState, chestBlock.getCloseChestSound());
                    MetaChestBlockEntity.this.updateBlockState(state, false);
                }
            }

            protected void openerCountChanged(final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState blockState, final int previous, final int current) {
                MetaChestBlockEntity.this.signalOpenCount(level, pos, blockState, previous, current);
            }

            public boolean isOwnContainer(final @NonNull Player player) {
                if (!(player.containerMenu instanceof MetaChestMenu)) {
                    return false;
                }

                Container container = ((MetaChestMenu)player.containerMenu).getContainer();

                if (container == MetaChestBlockEntity.this) {
                    return true;
                }

                if (container instanceof CompoundContainer compoundContainer) {
                    return compoundContainer.contains(MetaChestBlockEntity.this);
                }

                return false;
            }
        };
    }

    private void updateBlockState(final @NonNull BlockState state, final boolean isOpen) {
        assert this.level != null;

        this.level.setBlock(this.getBlockPos(), state.setValue(MetaChestBlock.OPEN, isOpen), 3);
    }

    private void playSound(final @NonNull BlockState state, final SoundEvent event) {
        Vec3i direction = (state.getValue(MetaChestBlock.FACING)).getUnitVec3i();

        double x = (double)this.worldPosition.getX() + (double)0.5F + (double)direction.getX() / (double)2.0F;
        double y = (double)this.worldPosition.getY() + (double)0.5F + (double)direction.getY() / (double)2.0F;
        double z = (double)this.worldPosition.getZ() + (double)0.5F + (double)direction.getZ() / (double)2.0F;

        assert this.level != null;

        this.level.playSound(null, x, y, z, event, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void startOpen(final @NonNull ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            assert this.getLevel() != null;
            this.openersCounter.incrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), containerUser.getContainerInteractionRange());
        }

    }

    @Override
    public void stopOpen(final @NonNull ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            assert this.getLevel() != null;
            this.openersCounter.decrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public @NonNull List<ContainerUser> getEntitiesWithContainerOpen() {
        assert this.getLevel() != null;
        return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
    }

    protected void signalOpenCount(final @NonNull Level level, final BlockPos pos, final @NonNull BlockState blockState, final int previous, final int current) {
        Block block = blockState.getBlock();
        level.blockEvent(pos, block, 1, current);
    }

    private @NonNull MetaChestStorage getStorage() {
        assert level != null;

        MinecraftServer server = level.getServer();

        if (server == null) {
            throw new IllegalStateException(
                    "MetaChestBlockEntity is not attached to a server"
            );
        }

        return MetaChestStorage.get(server);
    }

    public MetaChestInventory getInventory() {
        return getStorage().getInventory();
    }

    @Override
    public void preRemoveSideEffects(final @NonNull BlockPos pos, final @NonNull BlockState state) {}

    @Override
    public int getContainerSize() {
        return getInventory().size();
    }

    @Override
    public boolean isEmpty() {
        for (MetaItem item : getInventory().getItems()) {
            if (!item.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NonNull ItemStack getItem(int slot) {
        MetaItem item = getInventory().getItems().get(slot);
        ItemStack stack = item.getStack().copy();

        stack.setCount(item.getHanging());

        return stack;
    }

    @Override
    public @NonNull ItemStack removeItem(int slot, int count) {
        MetaChestStorage storage = getStorage();

        NonNullList<MetaItem> items = getInventory().getItems();

        if ((slot < 0) || (slot >= items.size()) || (items.get(slot).isEmpty())) {
            return ItemStack.EMPTY;
        }

        ItemStack result = items.get(slot).split(count);

        if (!result.isEmpty()) {
            storage.setDirty();
        }

        return result;

    }

    @Override
    public @NonNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);

        getInventory().getItems().set(slot, MetaItem.EMPTY);

        return stack;
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return getInventory().canPlaceItem(slot, stack);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        MetaChestStorage storage = getStorage();

        getInventory().set(slot, new MetaItem(stack.getCount(), stack));
        storage.setDirty();
    }

    @Override
    public int @NonNull [] getSlotsForFace(@NonNull Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NonNull ItemStack stack, Direction side) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NonNull ItemStack stack, @NonNull Direction side) {
        return false;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    @NonNull
    public Component getDisplayName() {
        return Component.translatable("block.metachest.meta_chest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory, @NonNull Player player) {
        return new MetaChestMenu(containerId, inventory, this);
    }

    @Override
    public void clearContent() {
        getInventory().getItems().clear();
    }
}
