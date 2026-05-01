package ca.techgarage;


import ca.techgarage.items.MagicEssenceItem;
import ca.techgarage.items.seeds.*;
import ca.techgarage.items.SpellFlowerItem;
import ca.techgarage.items.Textbook;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {

    public static Item TEXTBOOK;
    public static Item PYROTUNIA_SEEDS;
    public static Item SPARKSPUR_SEEDS;
    public static Item GLACIOLUS_SEEDS;
    public static Item TERROW_SEEDS;
    public static Item HOLYHOCK_SEEDS;
    public static Item ABYSSUM_SEEDS;
    public static Item BEGONEYA_SEEDS;

    public static Item MAGIC_ESSENCE;
    public static Item FLOWER_HUSK;
    public static Item SPELL_FLOWER;
    public static void initialize() {
        //seeds
        PYROTUNIA_SEEDS = register("pyrotunia_seeds", PyrotuniaSeedsItem::new, new Item.Properties().stacksTo(64));
        SPARKSPUR_SEEDS = register("sparkspur_seeds", SparkspurSeedsItem::new, new Item.Properties().stacksTo(64));
        GLACIOLUS_SEEDS = register("glaciolus_seeds", GlaciolusSeedsItem::new, new Item.Properties().stacksTo(64));
        TERROW_SEEDS = register("terrow_seeds", TerrowSeedsItem::new, new Item.Properties().stacksTo(64));
        HOLYHOCK_SEEDS = register("holyhock_seeds", HolyhockSeedsItem::new, new Item.Properties().stacksTo(64));
        ABYSSUM_SEEDS = register("abyssum_seeds", AbyssumSeedsItem::new, new Item.Properties().stacksTo(64));
        BEGONEYA_SEEDS = register("begoneya_seeds", BegoneyaSeedsItem::new, new Item.Properties().stacksTo(64));
        //misc
        TEXTBOOK = register("textbook", Textbook::new, new Item.Properties().stacksTo(1));
        MAGIC_ESSENCE = register("magic_essence", MagicEssenceItem::new, new Item.Properties().stacksTo(64));
        FLOWER_HUSK = register("flower_husk", Item::new, new Item.Properties().stacksTo(64));
        SPELL_FLOWER = register("spell_flower", SpellFlowerItem::new, new Item.Properties().stacksTo(1));
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
        Identifier id = Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, name);

        settings = settings.setId(ResourceKey.create(Registries.ITEM, id));

        T item = factory.apply(settings);

        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }
}