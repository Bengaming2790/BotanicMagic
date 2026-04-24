package ca.techgarage.blocks;

import ca.techgarage.ModItems;
import ca.techgarage.screen.ResearchTableMenu;
import ca.techgarage.spells.FlowerExtractionData;
import ca.techgarage.spells.IExtractableFlower;
import ca.techgarage.items.MagicEssenceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ResearchTableBlockEntity extends BlockEntity implements Container, MenuProvider {

    private static final int EXTRACTION_TIME = 0;
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int extractionProgress = 0;

    public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RESEARCH_TABLE, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ResearchTableBlockEntity entity) {
        if (level.isClientSide()) return;

        ItemStack input = entity.getItem(0);

        if (input.isEmpty() || !(Block.byItem(input.getItem()) instanceof IExtractableFlower)) {
            entity.extractionProgress = 0;
            return;
        }

        ItemStack output = entity.getItem(1);

        if (!output.isEmpty()) {
            entity.extractionProgress = 0;
            return;
        }

        entity.extractionProgress++;

        if (entity.extractionProgress >= EXTRACTION_TIME) {
            entity.extractionProgress = 0;
            entity.extract(level.getRandom());
        }

        entity.setChanged();
    }

    private void extract(RandomSource random) {
        ItemStack input = getItem(0);
        if (input.isEmpty()) return;

        Block block = Block.byItem(input.getItem());
        if (!(block instanceof IExtractableFlower flower)) return;

        FlowerExtractionData.ExtractionOutcome outcome = flower.getExtractionData().roll(random);

        ItemStack result = switch (outcome.result()) {
            case ELEMENT -> MagicEssenceItem.createEssence(outcome.element(), null);
            case SHAPE -> MagicEssenceItem.createEssence(null, outcome.shape());
            case HUSK -> ModItems.FLOWER_HUSK.getDefaultInstance();
            case FAILED -> ItemStack.EMPTY;
        };

        input.shrink(1);
        setItem(0, input);


        if (!result.isEmpty()) {
            ItemStack currentOutput = getItem(1);
            if (currentOutput.isEmpty()) {
                setItem(1, result);
            } else if (ItemStack.isSameItemSameComponents(currentOutput, result)
                    && currentOutput.getCount() < currentOutput.getMaxStackSize()) {
                currentOutput.grow(1);
                setItem(1, currentOutput);
            }
        }

        setChanged();
    }

    public ContainerData getData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> extractionProgress;
                    case 1 -> EXTRACTION_TIME;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) extractionProgress = value;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Research Table");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new ResearchTableMenu(syncId, playerInventory, this, this.getData());
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
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

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                output.store("item_" + i, ItemStack.CODEC, items.get(i));
            }
        }
        output.putInt("extractionProgress", extractionProgress);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, input.read("item_" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY));
        }
        extractionProgress = input.getIntOr("extractionProgress", 0);
    }
}