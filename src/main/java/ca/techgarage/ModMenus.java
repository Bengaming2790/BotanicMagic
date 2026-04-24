package ca.techgarage;

import ca.techgarage.screen.RefinementTableMenu;
import ca.techgarage.screen.ResearchTableMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final MenuType<ResearchTableMenu> RESEARCH_TABLE =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "research_table"),
                    new MenuType<>(ResearchTableMenu::new, FeatureFlags.DEFAULT_FLAGS)
            );

    public static final MenuType<RefinementTableMenu> REFINEMENT_TABLE =
            Registry.register(
                    BuiltInRegistries.MENU,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "refinement_table"),
                    new MenuType<>((syncId, inventory) -> new RefinementTableMenu(syncId, inventory), FeatureFlags.DEFAULT_FLAGS)
            );

    public static void register() {}
}