package ca.techgarage;

import ca.techgarage.items.Textbook;

import java.util.List;

public class OpenTextbookRequest {
    private static List<Textbook.FlowerEntry> pending;

    public static void open(List<Textbook.FlowerEntry> entries) {
        pending = entries;
    }

    public static List<Textbook.FlowerEntry> consume() {
        List<Textbook.FlowerEntry> out = pending;
        pending = null;
        return out;
    }
}