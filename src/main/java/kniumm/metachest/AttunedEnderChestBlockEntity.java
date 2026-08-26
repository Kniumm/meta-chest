package kniumm.metachest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class AttunedEnderChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {
    private static final int SIZE = 9 * 3;

    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState blockState) {
            level.playSound(null, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(final @NonNull Level level, final @NonNull BlockPos pos, final @NonNull BlockState blockState) {
            level.playSound(null, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(final @NonNull Level level, final BlockPos pos, final @NonNull BlockState blockState, final int previous, final int current) {
            level.blockEvent(AttunedEnderChestBlockEntity.this.worldPosition, ModBlocks.ATTUNED_ENDER_CHEST, 1, current);
        }

        @Override
        public boolean isOwnContainer(final @NonNull Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)player.containerMenu).getContainer();
                return container == AttunedEnderChestBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    private LockCode lockKey;
    private @Nullable Component name;

    private String key;

    public AttunedEnderChestBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        super(ModBlockEntities.ATTUNED_ENDER_CHEST_ENTITY, worldPosition, blockState);

        this.lockKey = LockCode.NO_LOCK;

        this.key = "placeholder";
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    protected void loadAdditional(final @NonNull ValueInput input) {
        super.loadAdditional(input);
        this.lockKey = LockCode.fromTag(input);
        this.name = parseCustomNameSafe(input, "CustomName");
    }

    @Override
    protected void saveAdditional(final @NonNull ValueOutput output) {
        super.saveAdditional(output);
        this.lockKey.addToTag(output);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
    }

    public static void lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final @NonNull AttunedEnderChestBlockEntity entity) {
        entity.chestLidController.tickLid();
    }

    @Override
    public boolean triggerEvent(final int b0, final int b1) {
        if (b0 == 1) {
            this.chestLidController.shouldBeOpen(b1 > 0);
            return true;
        } else {
            return super.triggerEvent(b0, b1);
        }
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
    public boolean canOpen(final @NonNull Player player) {
        return this.lockKey.canUnlock(player);
    }

    @Override
    public boolean isLocked() {
        return !this.lockKey.equals(LockCode.NO_LOCK);
    }

    private @NonNull AttunedEnderChestStorage getStorage() {
        assert level != null;

        MinecraftServer server = level.getServer();

        if (server == null) {
            throw new IllegalStateException(
                    "AttunedEnderChestBlockEntity is not attached to a server"
            );
        }

        return AttunedEnderChestStorage.get(server);
    }

    public NonNullList<ItemStack> getInventory() {
        return getStorage().getInventory(this.key);
    }

    @Override
    public void preRemoveSideEffects(final @NonNull BlockPos pos, final @NonNull BlockState state) {}

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    protected @NonNull NonNullList<ItemStack> getItems() {
        return this.getInventory();
    }

    @Override
    protected void setItems(@NonNull NonNullList<ItemStack> items) {
        this.getStorage().setInventory(this.key, items);
        setChanged();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : this.getItems()) {
            if (!item.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NonNull ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    public void recheckOpen() {
        if (!this.remove) {
            assert this.getLevel() != null;

            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    @Override
    public float getOpenNess(final float a) {
        return this.chestLidController.getOpenness(a);
    }

    @Override
    public @NonNull Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public @NonNull Component getDisplayName() {
        return this.getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return this.name;
    }

    @Override
    protected @NonNull Component getDefaultName() {
        return Component.translatable("container.enderchest");
    }

    @Override
    protected @NonNull AbstractContainerMenu createMenu(int containerId, @NonNull Inventory inventory) {
        return new AttunedEnderChestMenu(containerId, inventory, this);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        this.name = components.get(DataComponents.CUSTOM_NAME);
        this.lockKey = components.getOrDefault(
                DataComponents.LOCK,
                LockCode.NO_LOCK
        );
    }

    @Override
    public void setChanged() {
        super.setChanged();

        // It's fine.
        if (level == null || level.getServer() == null) {
            return;
        }

        this.getStorage().setDirty();
    }
}
