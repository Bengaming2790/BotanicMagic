package ca.techgarage.client.screen;

import ca.techgarage.screen.RefinementTableMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class RefinementTableScreen extends AbstractContainerScreen<RefinementTableMenu> {

    private int imageWidth;
    private int imageHeight;

    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/smithing.png");

    public RefinementTableScreen(RefinementTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // Same as smithing screen
        this.titleLabelX = 44;
        this.titleLabelY = 15;

        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {
        int x = leftPos;
        int y = topPos;

        // Background
        gfx.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x, y,
                0, 0,
                imageWidth, imageHeight,
                256, 256
        );

        // Progress arrow/result area
        int progress = menu.getProgress();
        int max = menu.getMaxProgress();

        if (max > 0) {
            int width = Mth.ceil((progress / (float) max) * 28);

            gfx.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 90,
                    y + 35,
                    176, 0,
                    width,
                    17,
                    256, 256
            );
        }
    }
}