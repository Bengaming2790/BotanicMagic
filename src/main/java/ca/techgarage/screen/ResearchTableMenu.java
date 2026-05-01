package ca.techgarage.screen;

import ca.techgarage.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResearchTableMenu extends AbstractContainerMenu {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int CONTAINER_SIZE = 2;

    private final Container container;
    private final ContainerData data;

    public ResearchTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(2));
    }

    public ResearchTableMenu(int syncId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.RESEARCH_TABLE, syncId);

        checkContainerSize(container, CONTAINER_SIZE);

        this.container = container;
        this.data = data;

        container.startOpen(inventory.player);

        this.addSlot(new Slot(container, INPUT_SLOT, 62, 20));

        this.addSlot(new Slot(container, OUTPUT_SLOT, 98, 20) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        this.addStandardInventorySlots(inventory, 8, 51);

        this.addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex < CONTAINER_SIZE) {
            if (!moveItemStackTo(stack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    public int getScaledProgress() {
        int progress = getProgress();
        int max = getMaxProgress();
        return max != 0 && progress != 0 ? progress * 24 / max : 0;
    }

    public Container getContainer() {
        return container;
    }
}