package ca.techgarage;

import ca.techgarage.items.SpellFlowerRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipes {

    public static final RecipeSerializer<SpellFlowerRecipe> SPELL_FLOWER_SERIALIZER =
            Registry.register(
                    BuiltInRegistries.RECIPE_SERIALIZER,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, SpellFlowerRecipe.ID),
                    new RecipeSerializer<>(SpellFlowerRecipe.CODEC, SpellFlowerRecipe.STREAM_CODEC)
            );

    public static void register() {
        BotanicMagic.LOGGER.info("[Botanic Magic] Recipes initialized");
    }
}