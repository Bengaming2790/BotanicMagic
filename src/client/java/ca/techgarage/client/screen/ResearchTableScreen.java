package ca.techgarage.client.screen;

import ca.techgarage.screen.ResearchTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ResearchTableScreen extends AbstractContainerScreen<ResearchTableMenu> {

    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

    public ResearchTableScreen(ResearchTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos, topPos,
                0, 0,
                imageWidth, imageHeight,
                256, 256
        );

        int progress = menu.getProgress();
        int max = menu.getMaxProgress();

        if (max > 0) {
            int arrowWidth = Mth.ceil((progress / (float) max) * 24);

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    leftPos + 79,
                    topPos + 34,
                    176, 0,
                    arrowWidth, 16,
                    256, 256
            );
        }

    }
}