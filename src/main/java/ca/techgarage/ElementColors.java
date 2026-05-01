package ca.techgarage;

import ca.techgarage.spells.SpellElement;

public class ElementColors {

    public static int getRGB(SpellElement element) {
        return switch (element) {
            case FIRE  -> 0xFF4D1A;
            case ICE   -> 0x4DD2FF;
            case EARTH -> 0x33FF66;
            case SPARK -> 0xFFE066;
            case DARK  -> 0x7A00CC;
            case LIGHT -> 0xFFFFFF;
            case WIND -> 0xFFFFFFF;
        };
    }
}