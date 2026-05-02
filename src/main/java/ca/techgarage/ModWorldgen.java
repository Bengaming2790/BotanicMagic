package ca.techgarage;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModWorldgen {

    private static final ResourceKey<PlacedFeature> PYROTUNIA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "pyrotunia"));

    private static final ResourceKey<PlacedFeature> GLACIOLUS_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "glaciolus"));

    private static final ResourceKey<PlacedFeature> TERROW_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "terrow"));

    private static final ResourceKey<PlacedFeature> SPARKSPUR_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "sparkspur"));

    private static final ResourceKey<PlacedFeature> ABYSSUM_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "abyssum"));

    private static final ResourceKey<PlacedFeature> BEGONEYA_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "begoneya"));

    private static final ResourceKey<PlacedFeature> HOLYHOCK_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath("botanicmagic", "holyhock"));
    public static void register() {
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_SAVANNA).or(BiomeSelectors.tag(BiomeTags.IS_BADLANDS)),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                PYROTUNIA_PLACED
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.HAS_SWAMP_HUT),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                ABYSSUM_PLACED
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                HOLYHOCK_PLACED
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                BEGONEYA_PLACED
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_MOUNTAIN).or(BiomeSelectors.tag(BiomeTags.IS_TAIGA)),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                GLACIOLUS_PLACED
        );

        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                TERROW_PLACED
        );
        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_HILL).or(BiomeSelectors.tag(BiomeTags.HAS_VILLAGE_PLAINS)),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                SPARKSPUR_PLACED
        );
    }
}