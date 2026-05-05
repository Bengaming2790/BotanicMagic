package ca.techgarage.items;

import ca.techgarage.ModDataComponents;
import ca.techgarage.ModItems;
import ca.techgarage.spells.MagicEssenceData;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.Spell;
import ca.techgarage.spells.SpellElement;
import ca.techgarage.spells.effect.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Consumer;

public class SpellFlowerItem extends Item {


    public SpellFlowerItem(Properties properties) {
        super(properties.stacksTo(1));
    }


    public static ItemStack create(SpellElement element, MagicShape shape) {
        ItemStack stack = new ItemStack(ModItems.SPELL_FLOWER);
        stack.set(ModDataComponents.MAGIC_ESSENCE_DATA,
                new MagicEssenceData(Optional.of(element), Optional.of(shape)));
        int cost = (int)(Math.random() * 10) + 1;
        stack.set(ModDataComponents.SPELL_XP_COST, cost);
        return stack;
    }


    public static Optional<SpellElement> getElement(ItemStack stack) {
        MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);
        return data != null ? data.element() : Optional.empty();
    }

    public static Optional<MagicShape> getShape(ItemStack stack) {
        MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);
        return data != null ? data.shape() : Optional.empty();
    }


    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(player, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1, 2);

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);

        if (data == null || data.element().isEmpty() || data.shape().isEmpty()) {
            return InteractionResult.FAIL;
        }

        int xpCost = stack.getOrDefault(ModDataComponents.SPELL_XP_COST, 1);
        if (player.experienceLevel < xpCost && !player.getAbilities().instabuild) {
            player.playSound(SoundEvents.FIRE_EXTINGUISH);
            return InteractionResult.FAIL;
        }
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-xpCost);
        }

        castSpell(level, player, data.element().get(), data.shape().get());
        player.getCooldowns().addCooldown(stack, 20);
        return InteractionResult.SUCCESS;
    }

    private void castSpell(Level level, Player player, SpellElement element, MagicShape shape) {
        Spell spell = switch (element) {
            case FIRE -> new FireEffect(11.5f, shape);
            case ICE -> new IceEffect(11.0f, shape);
            case EARTH -> new EarthEffect(10.5f, shape);
            case SPARK -> new SparkEffect(12.0f, shape);
            case DARK -> new DarkEffect(12.5f, shape);
            case LIGHT -> new LightEffect(12.5f, shape);
            case WIND -> new WindEffect(10f, shape);
        };


        switch (shape) {
            case PROJECTILE, COLUMN, CONE, AOE -> spell.apply(level, player, null);
            case BUFF_SELF -> spell.apply(level, player, player);
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                TooltipDisplay display, Consumer<Component> tooltip,
                                TooltipFlag flag) {
        MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);

        if (data == null) {
            tooltip.accept(Component.literal("Unidentified")
                    .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }

        data.element().ifPresent(element ->
                tooltip.accept(Component.literal("Element: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(element.name())
                                .withStyle(elementColor(element)).withStyle(ChatFormatting.BOLD)))
        );

        data.shape().ifPresent(shape ->
                tooltip.accept(Component.literal("Shape: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(shape.name())
                                .withStyle(ChatFormatting.AQUA)))
        );

        int xpCost = stack.getOrDefault(ModDataComponents.SPELL_XP_COST, 1);
        tooltip.accept(Component.literal("Cost: " + xpCost + " level")
                .withStyle(ChatFormatting.DARK_GREEN));
    }

    private ChatFormatting elementColor(SpellElement element) {
        return switch (element) {
            case FIRE -> ChatFormatting.RED;
            case ICE -> ChatFormatting.AQUA;
            case EARTH -> ChatFormatting.GREEN;
            case SPARK -> ChatFormatting.YELLOW;
            case DARK -> ChatFormatting.DARK_GRAY;
            case LIGHT -> ChatFormatting.WHITE;
            case WIND -> ChatFormatting.GRAY;
        };
    }
    public record SpellXpData(int cost) {}
}
