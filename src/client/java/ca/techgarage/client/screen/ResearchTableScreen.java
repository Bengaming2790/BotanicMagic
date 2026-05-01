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
    private int imageWidth;
    private int imageHeight;
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("botanicmagic", "textures/gui/research.png");

    public ResearchTableScreen(ResearchTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        // actual hopper gui size
        this.imageWidth = 176;
        this.imageHeight = 133;
    }

    @Override
    protected void init() {
        super.init();

        // centered like vanilla hopper
        this.titleLabelX = 8;
        this.titleLabelY = 6;

        // player inventory text line above inventory
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94; // 39
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {
        int x = this.leftPos;
        int y = this.topPos;

        // full hopper background
        gfx.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x, y,
                0, 0,
                this.imageWidth, this.imageHeight,
                256, 256
        );

        int progress = menu.getProgress();
        int max = menu.getMaxProgress();

        if (max > 0) {
            int width = Mth.ceil((progress / (float) max) * 24);

            gfx.blit(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 76,
                    y + 21,
                    176, 0,
                    width, 17,
                    256, 256
            );
        }
    }
}