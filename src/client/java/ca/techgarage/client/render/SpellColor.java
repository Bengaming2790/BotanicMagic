package ca.techgarage.client.render;

public class SpellColor {
    public final float r;
    public final float g;
    public final float b;

    public SpellColor(int r, int g, int b) {
        this.r = r / 255f;
        this.g = g / 255f;
        this.b = b / 255f;
    }

    public static SpellColor fromInt(int color) {
        int r = (color >> 16) & 255;
        int g = (color >> 8) & 255;
        int b = color & 255;
        return new SpellColor(r, g, b);
    }
}