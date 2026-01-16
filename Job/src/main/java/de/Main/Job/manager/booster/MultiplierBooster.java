package de.Main.Job.manager.booster;

import java.util.UUID;

public class MultiplierBooster extends Booster {

    private double multiplier = 0;

    public MultiplierBooster(UUID uuid, String name) {
        super(uuid, name);
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
