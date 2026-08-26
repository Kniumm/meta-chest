package kniumm.metachest;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class AttunedEnderChestMenu extends ChestMenu {
    private static final int ROWS = 3;

    public AttunedEnderChestMenu(int containerId, Inventory inventory, Container container) {
        super(MenuType.GENERIC_9x3, containerId, inventory, container, ROWS);
    }
}
