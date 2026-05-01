package ca.techgarage.client.screen;

import ca.techgarage.items.Textbook;
import net.minecraft.client.Minecraft;
import java.util.List;

public class ClientTextbookOpener {

    public static void open(List<Textbook.FlowerEntry> entries) {
        Minecraft.getInstance().setScreen(new TextbookScreen(entries));
    }
    public static void init() {}
}