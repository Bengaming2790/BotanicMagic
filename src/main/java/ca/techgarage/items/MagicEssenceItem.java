package ca.techgarage.items;

import ca.techgarage.ModDataComponents;

import ca.techgarage.ModItems;
import ca.techgarage.spells.MagicEssenceData;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MagicEssenceItem extends Item {

    public MagicEssenceItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createEssence(SpellElement element, MagicShape shape) {
        ItemStack stack = new ItemStack(ModItems.MAGIC_ESSENCE);
        stack.set(ModDataComponents.MAGIC_ESSENCE_DATA,
                new MagicEssenceData(
                        Optional.ofNullable(element),
                        Optional.ofNullable(shape)
                )
        );
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                TooltipDisplay display, Consumer<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);

        if (data == null) {
            tooltip.accept(Component.literal("Unidentified")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        data.element().ifPresent(element ->
                tooltip.accept(Component.literal("Element: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(formatName(element.name()))
                                .withStyle(elementColor(element))))
        );

        data.shape().ifPresent(shape ->
                tooltip.accept(Component.literal("Shape: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(formatName(shape.name()))
                                .withStyle(ChatFormatting.AQUA)))
        );
    }

    private String formatName(String enumName) {
        String[] words = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!sb.isEmpty()) sb.append(" ");
            if (word.equalsIgnoreCase("aoe")) {sb.append("AOE"); return sb.toString();}
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private ChatFormatting elementColor(SpellElement element) {
        return switch (element) {
            case FIRE -> ChatFormatting.RED;
            case ICE -> ChatFormatting.AQUA;
            case EARTH -> ChatFormatting.GREEN;
            case SPARK -> ChatFormatting.YELLOW;
        };
    }
}