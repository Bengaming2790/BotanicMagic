package ca.techgarage.client.render;

import ca.techgarage.spells.SpellElement;

public class ElementRenderData {
    private static final ThreadLocal<SpellElement> CURRENT = new ThreadLocal<>();

    public static void set(SpellElement element) {
        CURRENT.set(element);
    }

    public static SpellElement get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}