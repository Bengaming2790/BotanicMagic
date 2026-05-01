package ca.techgarage.items;

import ca.techgarage.OpenTextbookRequest;
import ca.techgarage.spells.FlowerExtractionData;
import ca.techgarage.spells.IExtractableFlower;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Textbook extends Item {

    public Textbook(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            OpenTextbookRequest.open(collectFlowers());
            player.playSound(SoundEvents.BOOK_PAGE_TURN, 1, 1);
        }
        return InteractionResult.SUCCESS;
    }

    private List<FlowerEntry> collectFlowers() {
        List<FlowerEntry> entries = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof IExtractableFlower flower) {
                String name = BuiltInRegistries.BLOCK.getKey(block).getPath();

                ItemStack stack = new ItemStack(block.asItem());

                entries.add(new FlowerEntry(
                        name,
                        flower.getExtractionData(),
                        stack
                ));
            }
        }

        return entries;
    }

    public record FlowerEntry(String registryName, FlowerExtractionData data, ItemStack stack) {

        public String displayName() {
            String[] words = registryName.replace('_', ' ').split(" ");
            StringBuilder sb = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    sb.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1))
                            .append(" ");
                }
            }

            return sb.toString().trim();
        }
    }
}