package ca.techgarage.client;

import ca.techgarage.ModMenus;
import ca.techgarage.bscm.Bscm;
import ca.techgarage.client.screen.RefinementTableScreen;
import ca.techgarage.client.screen.ResearchTableScreen;
import ca.techgarage.client.spells.SpellProjectileRenderer;
import ca.techgarage.entity.ModEntities;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class BotanicMagicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.RESEARCH_TABLE, ResearchTableScreen::new);
		MenuScreens.register(ModMenus.REFINEMENT_TABLE, RefinementTableScreen::new);
		EntityRenderers.register(ModEntities.SPELL_PROJECTILE, SpellProjectileRenderer::new);


	}
}