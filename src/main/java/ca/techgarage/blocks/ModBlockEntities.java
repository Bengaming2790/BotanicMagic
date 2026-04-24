package ca.techgarage.blocks;

import ca.techgarage.BotanicMagic;
import ca.techgarage.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<ResearchTableBlockEntity> RESEARCH_TABLE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "research_table"),
                    FabricBlockEntityTypeBuilder.<ResearchTableBlockEntity>create(
                            ResearchTableBlockEntity::new,
                            ModBlocks.RESEARCH_TABLE
                    ).build()
            );

    public static final BlockEntityType<RefinementTableBlockEntity> REFINEMENT_TABLE =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "refinement_table"),
                    FabricBlockEntityTypeBuilder.create(
                            RefinementTableBlockEntity::new,
                            ModBlocks.REFINEMENT_TABLE
                    ).build()
            );

    public static void register() {}
}