package ca.techgarage.blocks;

import ca.techgarage.ModDataComponents;
import ca.techgarage.ModItems;
import ca.techgarage.items.SpellFlowerItem;
import ca.techgarage.screen.RefinementTableMenu;
import ca.techgarage.spells.MagicEssenceData;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RefinementTableBlockEntity extends BlockEntity implements Container, MenuProvider {

    private int progress = 0;
    private static final int MAX_TIME = 80;
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public RefinementTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REFINEMENT_TABLE, pos, state);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, RefinementTableBlockEntity be) {
        if (level.isClientSide()) return;

        if (!be.canRefine()) {
            be.progress = 0;
            return;
        }

        be.progress++;

        if (be.progress >= MAX_TIME) {
            be.progress = 0;
            be.refine();
        }

        be.setChanged();
    }

    private void refine() {
        if (!canRefine()) return;
        ItemStack husk = getItem(RefinementTableMenu.HUSK_SLOT);
        ItemStack elementEssence = getItem(RefinementTableMenu.ELEMENT_SLOT);
        ItemStack shapeEssence = getItem(RefinementTableMenu.SHAPE_SLOT);
        System.out.println("HUSK: " + husk);
        System.out.println("ELEMENT: " + elementEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA));
        System.out.println("SHAPE: " + shapeEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA));
        if (!husk.is(ModItems.FLOWER_HUSK)) return;

        MagicEssenceData elementData = elementEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA);
        MagicEssenceData shapeData = shapeEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA);

        if (elementData == null || shapeData == null) return;
        if (elementData.element().isEmpty() || shapeData.shape().isEmpty()) return;

        SpellElement element = elementData.element().get();
        MagicShape shape = shapeData.shape().get();

        ItemStack result = SpellFlowerItem.create(element, shape);

        if (!result.isEmpty()) {
            setItem(RefinementTableMenu.OUTPUT_SLOT, result.copy());

            husk.shrink(1);
            elementEssence.shrink(1);
            shapeEssence.shrink(1);
        }
    }

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_TIME;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        @Override
        public int getCount() {
            return 2;
        }
    };
    public ContainerData getData() {
        return dataAccess;
    }
    private boolean canRefine() {
        ItemStack husk = getItem(RefinementTableMenu.HUSK_SLOT);
        ItemStack elementEssence = getItem(RefinementTableMenu.ELEMENT_SLOT);
        ItemStack shapeEssence = getItem(RefinementTableMenu.SHAPE_SLOT);

        if (husk.isEmpty() || elementEssence.isEmpty() || shapeEssence.isEmpty()) {
            return false;
        }

        if (!husk.is(ModItems.FLOWER_HUSK)) return false;

        MagicEssenceData elementData = elementEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA);
        MagicEssenceData shapeData = shapeEssence.get(ModDataComponents.MAGIC_ESSENCE_DATA);

        if (elementData == null || shapeData == null) return false;
        if (elementData.element().isEmpty() || shapeData.shape().isEmpty()) return false;

        return true;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Refinement Table");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new RefinementTableMenu(syncId, inv, this, this.getData());
    }


    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = items.get(slot).split(amount);
        setChanged();
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override public void clearContent() { items.clear(); }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }
}