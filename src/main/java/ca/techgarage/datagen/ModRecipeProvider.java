package ca.techgarage.datagen;

import ca.techgarage.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                TagKey<Item> flowersTag = TagKey.create(
                        Registries.ITEM,
                        Identifier.fromNamespaceAndPath("botanicmagic", "flowers")
                );

                shapeless(RecipeCategory.MISC, ModItems.TEXTBOOK)
                        .requires(Items.BOOK)
                        .requires(flowersTag)
                        .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK))
                        .save(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "BotanicMagicRecipeProvider";
    }
}