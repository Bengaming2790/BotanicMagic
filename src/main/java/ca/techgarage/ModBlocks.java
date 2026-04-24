package ca.techgarage;


import ca.techgarage.blocks.RefinementTableBlock;
import ca.techgarage.blocks.RefinementTableBlockEntity;
import ca.techgarage.blocks.flowers.Glaciolus;
import ca.techgarage.blocks.flowers.Pyrotunia;
import ca.techgarage.blocks.ResearchTableBlock;
import ca.techgarage.blocks.flowers.Sparkspur;
import ca.techgarage.blocks.flowers.Terrow;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {

    public static final Block PYROTUNIA = register(
            "pyrotunia",
            Pyrotunia::new,
            BlockBehaviour.Properties.of().noCollision().sound(SoundType.GRASS),
            true
    );
    public static final Block SPARKSPUR = register(
            "sparkspur",
            Sparkspur::new,
            BlockBehaviour.Properties.of().noCollision().sound(SoundType.GRASS),
            true
    );
    public static final Block GLACIOLUS = register(
            "glaciolus",
            Glaciolus::new,
            BlockBehaviour.Properties.of().noCollision().sound(SoundType.GRASS),
            true
    );
    public static final Block TERROW = register(
            "terrow",
            Terrow::new,
            BlockBehaviour.Properties.of().noCollision().sound(SoundType.GRASS),
            true
    );
    public static final Block RESEARCH_TABLE = register(
            "research_table",
            ResearchTableBlock::new,
            BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD),
            true
    );
    public static final Block REFINEMENT_TABLE = register(
            "refinement_table",
            RefinementTableBlock::new,
            BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.IRON),
            true
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.setId(blockKey));

        if (shouldRegisterItem) {

            ResourceKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, name));
    }

    public static void initialize() {
        BotanicMagic.LOGGER.info("[Botanic Magic] Blocks initialized");
    }
}