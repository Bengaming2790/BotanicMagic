package ca.techgarage;

import ca.techgarage.blocks.ModBlockEntities;
import ca.techgarage.bscm.Bscm;
import ca.techgarage.entity.ModEntities;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BotanicMagic implements ModInitializer {
	public static final String MOD_ID = "botanicmagic";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		LOGGER.info("[Botanic Magic] Initializing BM");
		Bscm.load(BotanicConfig.class, "botanicmagic");
		ModDataComponents.register();
		ModBlocks.initialize();
		ModItems.initialize();
		ModBlockEntities.register();
		ModMenus.register();
		ModRecipes.register();
		ModEntities.register();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_CREATIVE_TAB_KEY, CUSTOM_CREATIVE_TAB);
	}

	public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(
			BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(MOD_ID, "botanic_magic")
	);
	public static final CreativeModeTab CUSTOM_CREATIVE_TAB = FabricCreativeModeTab.builder()
			.icon(() -> new ItemStack(ModItems.TEXTBOOK))
			.title(Component.translatable("botanicmagic.itemgroup"))
			.displayItems((params, output) -> {
				output.accept(ModItems.TEXTBOOK);
				output.accept(ModBlocks.RESEARCH_TABLE);
				output.accept(ModBlocks.PYROTUNIA);
				output.accept(ModItems.PYROTUNIA_SEEDS);
				output.accept(ModBlocks.SPARKSPUR);
				output.accept(ModItems.SPARKSPUR_SEEDS);
				output.accept(ModBlocks.GLACIOLUS);

			})
			.build();

}