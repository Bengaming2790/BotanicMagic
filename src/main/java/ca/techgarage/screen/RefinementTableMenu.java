package ca.techgarage.screen;

import ca.techgarage.ModItems;
import ca.techgarage.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class RefinementTableMenu extends AbstractContainerMenu {

    private static final int CONTAINER_SIZE = 4;

    public static final int HUSK_SLOT = 0;
    public static final int ELEMENT_SLOT = 1;
    public static final int SHAPE_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;

    private final Container container;
    private final ContainerData data;

    public RefinementTableMenu(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData());
    }

    public RefinementTableMenu(int syncId, Inventory inventory, Container container, ContainerData data) {
        super(ModMenus.REFINEMENT_TABLE, syncId);

        checkContainerSize(container, CONTAINER_SIZE);

        this.container = container;
        this.data = data;

        container.startOpen(inventory.player);

        this.addSlot(new Slot(container, HUSK_SLOT, 8, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.FLOWER_HUSK);
            }
        });

        this.addSlot(new Slot(container, ELEMENT_SLOT, 26, 48));

        this.addSlot(new Slot(container, SHAPE_SLOT, 44, 48));

        this.addSlot(new Slot(container, OUTPUT_SLOT, 98, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                // Consume inputs ONLY when result is taken
                container.getItem(HUSK_SLOT).shrink(1);
                container.getItem(ELEMENT_SLOT).shrink(1);
                container.getItem(SHAPE_SLOT).shrink(1);

                container.setChanged();
            }
        });

        this.addDataSlots(data);
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex == OUTPUT_SLOT) {
            if (!moveItemStackTo(stack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);

            return original;
        }

        if (slotIndex < CONTAINER_SIZE) {
            if (!moveItemStackTo(stack, CONTAINER_SIZE, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        }
        else {
            if (!moveItemStackTo(stack, HUSK_SLOT, OUTPUT_SLOT, false)) {
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

    public Container getContainer() {
        return container;
    }

    private static class SimpleContainerData implements ContainerData {
        @Override
        public int get(int index) {
            return 0;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}