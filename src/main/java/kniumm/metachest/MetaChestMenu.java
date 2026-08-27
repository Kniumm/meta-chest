package kniumm.metachest;

import com.google.common.collect.HashBiMap;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jspecify.annotations.NonNull;

public class MetaChestMenu extends AbstractContainerMenu {
    private static final int SLOTS_ROWS = 6;
    private static final int SLOTS_COLUMNS = 9;
    private static final int SLOTS_COUNT = SLOTS_ROWS * SLOTS_COLUMNS;

    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END = SLOTS_COUNT;
    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END = INVENTORY_START + Inventory.INVENTORY_SIZE;

    private final Container container;

    public MetaChestMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOTS_COUNT));
    }

    public MetaChestMenu(final int containerId, final @NonNull Inventory inventory, final Container container) {
        super(MenuType.GENERIC_9x6, containerId);
        checkContainerSize(container, SLOTS_ROWS * 9);
        this.container = container;
        container.startOpen(inventory.player);
        int chestGridTop = 18;
        this.addChestGrid(container, 8, 18);
        int inventoryTop = 18 + SLOTS_ROWS * 18 + 13;
        this.addStandardInventorySlots(inventory, 8, inventoryTop);
    }

    private void addChestGrid(final Container container, final int left, final int top) {
        for(int y = 0; y < SLOTS_ROWS; ++y) {
            for(int x = 0; x < 9; ++x) {
                this.addSlot(new Slot(container, x + y * 9, left + x * 18, top + y * 18) {
                    @Override
                    public boolean mayPlace(@NonNull ItemStack stack) {
                        assert container instanceof MetaChestBlockEntity;

                        Item item = stack.getItem();
                        HashBiMap<Item, Integer> itemMap = ((MetaChestBlockEntity) this.container).getInventory().getItemMap();

                        boolean is_item_already_elsewhere = itemMap.containsKey(item) && (itemMap.get(item) != this.getContainerSlot());
                        boolean is_block_item = item instanceof BlockItem;

                        return stack.isStackable() && is_block_item && (!is_item_already_elsewhere);
                    }
                });
            }
        }

    }

    @Override
    public boolean stillValid(final @NonNull Player player) {
        return this.container.stillValid(player);
    }

    private boolean moveItemStackToMapped(Slot inputSlot, @NonNull ItemStack stack) {
        assert container instanceof MetaChestBlockEntity;

        MetaChestInventory inventory = ((MetaChestBlockEntity) this.container).getInventory();
        HashBiMap<Item, Integer> itemMap = inventory.getItemMap();
        Item item = stack.getItem();

        if (!itemMap.containsKey(item)) {
            return false;
        }

        int slotIndex = itemMap.get(item);
        Slot slot = this.slots.get(slotIndex);

        MetaItem destinationStack = inventory.getItems().get(slotIndex);
        int count = Math.min(stack.getCount(), destinationStack.getMaxStackSize() - destinationStack.getCount());

        destinationStack.grow(count);
        stack.shrink(count);

        return true;
    }

    @Override
    public @NonNull ItemStack quickMoveStack(final @NonNull Player player, final int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();

        if (slotIndex < SLOTS_COUNT) {
            int before = stack.getCount();

            if (!this.moveItemStackTo(stack, SLOTS_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }

            int after = stack.getCount();

            assert container instanceof MetaChestBlockEntity;

            MetaChestInventory inventory = ((MetaChestBlockEntity) this.container).getInventory();

            inventory.getItems().get(slotIndex).shrink(before - after);
        } else if (!this.moveItemStackToMapped(slot, stack)) {
            if (!this.moveItemStackTo(stack, 0, SLOTS_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return stack.copy();
    }

    @Override
    public void removed(final @NonNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return SLOTS_ROWS;
    }
}
