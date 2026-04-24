package ca.techgarage.client.screen;

import ca.techgarage.screen.RefinementTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class RefinementTableScreen extends AbstractContainerScreen<RefinementTableMenu> {

    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/furnace.png");

    public RefinementTableScreen(RefinementTableMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                leftPos, topPos,
                0.0f, 0.0f,
                this.imageWidth, this.imageHeight,
                256, 256
        );

        super.extractContents(graphics, mouseX, mouseY, delta);
    }
}