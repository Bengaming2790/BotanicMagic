package ca.techgarage.client.screen;

import ca.techgarage.items.Textbook;
import ca.techgarage.spells.FlowerExtractionData;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Matrix3x2f;

import java.util.List;

public class TextbookScreen extends Screen {

    private static final int BOOK_W = 325;
    private static final int FOOTER_H = 32;
    private static final int BOOK_H = 180 + FOOTER_H;

    private static final int PAGE_W = 116;
    private static final int MARGIN = 14;

    private static final int COL_BG_TOP = 0xC0101010;
    private static final int COL_BG_BOTTOM = 0xD0101010;

    private static final int COL_PARCHMENT = 0xFFD8C88A;
    private static final int COL_SPINE = 0xFF8A6820;
    private static final int COL_DIVIDER = 0x808A6820;
    private static final int COL_INK = 0xFF1A0E04;
    private static final int COL_INK_MUTED = 0xFF5A3A18;

    private static final int COL_ELEMENT = 0xFF8030C0;
    private static final int COL_SHAPE = 0xFF306090;
    private static final int COL_HUSK = 0xFF306030;
    private static final int COL_FAIL = 0xFF903020;

    private static final int COL_BAR_BG = 0x408A6820;
    private static final int COL_PAGE_NUM = 0xFF8A6820;

    private final List<Textbook.FlowerEntry> entries;
    private int page = 0;

    private Button prevButton;
    private Button nextButton;

    public TextbookScreen(List<Textbook.FlowerEntry> entries) {
        super(Component.literal("Flora Arcana"));
        this.entries = entries;
    }

    @Override
    protected void init() {
        int x = (width - BOOK_W) / 2;
        int y = (height - BOOK_H) / 2;

        int btnY = y + BOOK_H - FOOTER_H + 8;

        prevButton = Button.builder(Component.literal("<"), b -> changePage(-1))
                .bounds(x + 10, btnY, 20, 16)
                .build();

        nextButton = Button.builder(Component.literal(">"), b -> changePage(1))
                .bounds(x + BOOK_W - 30, btnY, 20, 16)
                .build();

        addRenderableWidget(prevButton);
        addRenderableWidget(nextButton);

        updateButtons();
    }

    private void changePage(int delta) {
        if (entries.isEmpty()) return;

        page = Math.max(0, Math.min(page + delta, entries.size() - 1));
        updateButtons();
    }

    private void updateButtons() {
        boolean hasPages = !entries.isEmpty();
        prevButton.active = hasPages && page > 0;
        nextButton.active = hasPages && page < entries.size() - 1;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float delta) {

        gfx.fillGradient(0, 0, width, height, COL_BG_TOP, COL_BG_BOTTOM);

        int bx = (width - BOOK_W) / 2;
        int by = (height - BOOK_H) / 2;

        drawBook(gfx, bx, by);

        if (entries.isEmpty()) {
            gfx.centeredText(
                    font,
                    Component.literal("No flowers registered."),
                    width / 2,
                    height / 2,
                    COL_INK
            );
        } else {
            renderPage(gfx, entries.get(page), bx, by);

            gfx.centeredText(
                    font,
                    Component.literal((page + 1) + " / " + entries.size()),
                    bx + BOOK_W / 2,
                    by + BOOK_H - FOOTER_H + 10,
                    COL_PAGE_NUM
            );
        }

        super.extractRenderState(gfx, mouseX, mouseY, delta);
    }

    private void drawBook(GuiGraphicsExtractor gfx, int bx, int by) {
        gfx.fill(bx, by, bx + BOOK_W, by + BOOK_H, COL_PARCHMENT);
        gfx.outline(bx, by, BOOK_W, BOOK_H, COL_SPINE);

        gfx.fill(
                bx + BOOK_W / 2 - 1,
                by + 6,
                bx + BOOK_W / 2 + 1,
                by + BOOK_H - 6,
                COL_SPINE
        );

        gfx.fill(
                bx,
                by + BOOK_H - FOOTER_H,
                bx + BOOK_W,
                by + BOOK_H,
                0x66000000
        );
    }

    private void renderPage(GuiGraphicsExtractor gfx, Textbook.FlowerEntry entry, int bx, int by) {
        FlowerExtractionData d = entry.data();

        float fail = Math.max(0f, 1f - d.elementChance - d.shapeChance - d.huskChance);

        int lx = bx + MARGIN;
        int ly = by + 12;
        FormattedCharSequence text = Component.literal(entry.displayName()).getVisualOrderText();

        gfx.text(
                font,
                text,
                lx + PAGE_W / 2 - font.width(text) / 2,
                ly,
                COL_INK,
                false
        );

        ly += font.lineHeight + 4;

        gfx.fill(lx + 4, ly, lx + PAGE_W - 4, ly + 1, COL_DIVIDER);
        ly += 8;

        int iconX = lx + (PAGE_W - 16) / 2;
        TrackingItemStackRenderState itemState = new TrackingItemStackRenderState();

        minecraft.getItemModelResolver().updateForTopItem(
                itemState,
                entry.stack(),
                ItemDisplayContext.GUI,
                minecraft.level,
                minecraft.player,
                0
        );

        gfx.guiRenderState.addItem(
                new GuiItemRenderState(
                        new Matrix3x2f(gfx.pose()),
                        itemState,
                        iconX,
                        ly,
                        gfx.scissorStack.peek()
                )
        );
        ly += 22;

        ly = drawStat(gfx, lx, ly, "Element:", pct(d.elementChance), COL_ELEMENT);
        ly = drawStat(gfx, lx, ly, "Shape:", pct(d.shapeChance), COL_SHAPE);
        ly = drawStat(gfx, lx, ly, "Husk:", pct(d.huskChance), COL_HUSK);
        drawStat(gfx, lx, ly, "Fail:", pct(fail), COL_FAIL);

        int rx = bx + BOOK_W / 2 + 8;
        int ry = by + 12;

        gfx.text(font, Component.literal("Elements"), rx, ry, COL_INK, false);
        ry += font.lineHeight + 4;

        double maxEW = Math.max(1f,
                d.elements.stream().mapToDouble(e -> e.weight()).max().orElse(1f)
        );

        for (var e : d.elements) {
            ry = drawWeightBar(gfx, rx, ry,
                    e.value().toString(),
                    e.weight(),
                    (float) maxEW,
                    COL_ELEMENT);
        }

        ry += 6;

        gfx.text(font, Component.literal("Shapes"), rx, ry, COL_INK, false);
        ry += font.lineHeight + 4;

        double maxSW = Math.max(1f,
                d.shapes.stream().mapToDouble(s -> s.weight()).max().orElse(1f)
        );

        for (var s : d.shapes) {
            ry = drawWeightBar(gfx, rx, ry,
                    formatShape(s.value().toString()),
                    s.weight(),
                    (float) maxSW,
                    COL_SHAPE);
        }
    }

    private int drawStat(GuiGraphicsExtractor gfx, int x, int y, String label, String value, int color) {
        gfx.text(font, Component.literal(label), x, y, COL_INK_MUTED, false);
        gfx.text(font, Component.literal(value),
                x + PAGE_W - font.width(value),
                y,
                color,
                false);
        return y + font.lineHeight + 2;
    }

    private int drawWeightBar(GuiGraphicsExtractor gfx, int x, int y,
                              String label, float weight, float maxWeight, int color) {

        int barMax = PAGE_W - 16;
        float safeMax = Math.max(maxWeight, 1f);

        int barW = Math.max(2, (int) ((weight / safeMax) * barMax));

        gfx.text(font, Component.literal(label), x, y, COL_INK_MUTED, false);
        y += font.lineHeight + 1;

        gfx.fill(x, y, x + barMax, y + 5, COL_BAR_BG);
        gfx.fill(x, y, x + barW, y + 5, color);

        String weightText = String.format("%.3f", weight);

        gfx.text(font,
                Component.literal(weightText),
                x + barMax + 4,
                y - 1,
                COL_PAGE_NUM,
                false);

        return y + 9;
    }

    private static String pct(float v) {
        return String.format("%.2f", v * 100f) + "%";
    }

    private static String formatShape(String raw) {
        raw = raw.replace('_', ' ').toLowerCase();
        String[] split = raw.split(" ");
        StringBuilder out = new StringBuilder();

        for (String s : split) {
            if (s.isEmpty()) continue;
            out.append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1))
                    .append(" ");
        }

        return out.toString().trim();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}