package ca.techgarage.items;

import ca.techgarage.ModDataComponents;
import ca.techgarage.ModItems;
import ca.techgarage.ModRecipes;
import ca.techgarage.spells.MagicEssenceData;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class SpellFlowerRecipe implements CraftingRecipe {

    public static final String ID = "spell_flower";

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasElementEssence = false;
        boolean hasShapeEssence = false;
        boolean hasHusk = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.FLOWER_HUSK)) {
                hasHusk = true;
            } else if (stack.is(ModItems.MAGIC_ESSENCE)) {
                MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);
                if (data == null) continue;

                if (data.element().isPresent() && data.shape().isEmpty()) {
                    hasElementEssence = true;
                } else if (data.shape().isPresent() && data.element().isEmpty()) {
                    hasShapeEssence = true;
                }
            }
        }

        return hasElementEssence && hasShapeEssence && hasHusk;
    }


    @Override
    public boolean showNotification() {
        return false;
    }
    @Override
    public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }
    @Override
    public String group() {
        return "";
    }
    public static final MapCodec<SpellFlowerRecipe> CODEC =
            MapCodec.unit(SpellFlowerRecipe::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellFlowerRecipe> STREAM_CODEC =
            StreamCodec.unit(new SpellFlowerRecipe());
    @Override
    public ItemStack assemble(CraftingInput input) {
        MagicEssenceData elementData = null;
        MagicEssenceData shapeData = null;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.is(ModItems.MAGIC_ESSENCE)) continue;

            MagicEssenceData data = stack.get(ModDataComponents.MAGIC_ESSENCE_DATA);
            if (data == null) continue;

            if (data.element().isPresent() && data.shape().isEmpty()) {
                elementData = data;
            } else if (data.shape().isPresent() && data.element().isEmpty()) {
                shapeData = data;
            }
        }

        if (elementData == null || shapeData == null) return ItemStack.EMPTY;

        return SpellFlowerItem.create(
                elementData.element().get(),
                shapeData.shape().get()
        );
    }



    @Override
    public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
        return ModRecipes.SPELL_FLOWER_SERIALIZER;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(List.of(
                Ingredient.of(ModItems.FLOWER_HUSK),
                Ingredient.of(ModItems.MAGIC_ESSENCE)
        ));
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

}