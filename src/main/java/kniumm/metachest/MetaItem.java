package kniumm.metachest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class MetaItem {
    public static final int MAX_STACK_SIZE = 65536;
    public static final MetaItem EMPTY = new MetaItem(0, ItemStack.EMPTY);

    private int count;
    private int hanging;
    private ItemStack stack;

    public static final Codec<MetaItem> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("count")
                                    .forGetter(MetaItem::getCount),
                            ItemStack.CODEC.fieldOf("stack")
                                    .forGetter(MetaItem::getStack)
                    ).apply(instance, MetaItem::new)
            );

    public MetaItem(int count, @NonNull ItemStack stack) {
        this.count = count;
        this.hanging = 0;
        this.stack = stack;

        this.calculateHanging();
    }

    public MetaItem copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new MetaItem(this.count, this.stack.copy());
        }
    }

    public MetaItem copyWithCount(final int count) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            MetaItem copy = this.copy();
            copy.setCount(count);
            return copy;
        }
    }

    public int getMaxStackSize() { return MAX_STACK_SIZE; }

    public int getCount() {
        return count;
    }

    public int getHanging() {
        return this.hanging;
    }

    public void calculateHanging() {
        this.hanging = Math.min(this.count, 64);
    }

    public void setCount(int count) {
        this.count = count;
        this.calculateHanging();
    }

    public ItemStack getStack() {
        return stack;
    }

    public void grow(final int amount) {
        this.setCount(this.getCount() + amount);
    }
    public void shrink(final int amount) {
        this.grow(-amount);
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public ItemStack split(int amount) {
        int realAmount = Math.min(amount, this.count);

        ItemStack result = this.stack.copyWithCount(realAmount);
        this.shrink(realAmount);

        return result;
    }

    public String toString() {
        return "stack=" + this.getStack() + " count=" + this.getCount();
    }
}