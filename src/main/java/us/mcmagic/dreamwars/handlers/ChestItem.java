package us.mcmagic.dreamwars.handlers;

public class ChestItem {
    private int id;
    private double rarity;
    private ChestCategory category;

    public ChestItem(int id, double rarity, ChestCategory category) {
        this.id = id;
        this.rarity = rarity;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public double getRarity() {
        return rarity;
    }

    public ChestCategory getCategory() {
        return category;
    }
}
