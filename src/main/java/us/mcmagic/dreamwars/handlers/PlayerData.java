package us.mcmagic.dreamwars.handlers;

import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private int cloud;
    private int attacks = 0;

    public PlayerData(UUID uuid, int cloud) {
        this.uuid = uuid;
        this.cloud = cloud;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getCloud() {
        return cloud;
    }

    public int getAttacks() {
        return attacks;
    }

    public void addAttack() {
        attacks += 1;
    }
}
