package ca.techgarage.client;

import ca.techgarage.OpenTextbookRequest;
import ca.techgarage.client.screen.TextbookScreen;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientTextbookOpener {

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var entries = OpenTextbookRequest.consume();

            if (entries != null) {
                Minecraft.getInstance().setScreen(new TextbookScreen(entries));
            }
        });
    }
}