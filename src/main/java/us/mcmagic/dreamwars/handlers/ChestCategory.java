package us.mcmagic.dreamwars.handlers;

import java.util.ArrayList;
import java.util.List;

public enum ChestCategory {
    WEAPONS(1), HELMET(2), CHESTPLATE(3), LEGGINGS(4), BOOTS(5), MISC(6);

    private int id;
    private List<ChestItem> items = new ArrayList<>();

    ChestCategory(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public List<ChestItem> getItems() {
        return items;
    }

    public void addItem(ChestItem item) {
        items.add(item);
    }
}
