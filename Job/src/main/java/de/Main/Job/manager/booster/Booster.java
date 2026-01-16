package de.Main.Job.manager.booster;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class Booster {

    private UUID uuid;



    private String name;

    private Consumer<BoosterEffect> activationEffect;
    private Consumer<BoosterEffect> deactivationEffect;

    public Booster(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }



    public void setActivationEffect(Consumer<BoosterEffect> playerEffect) {
        this.activationEffect = playerEffect;
    }

    public void setDeactivationEffect(Consumer<BoosterEffect> deactivationEffect) {
        this.deactivationEffect = deactivationEffect;
    }

    public void activateBooster(BoosterEffect effect) {
        if (this.activationEffect != null) {
            this.activationEffect.accept(effect);
        }
    }

    public void deactivateBooster(BoosterEffect effect) {
        if (this.deactivationEffect != null) {
            this.deactivationEffect.accept(effect);
        }
    }

    public static final String createDbKey(Booster booster, Player player) {
        if(player == null){
            throw new IllegalArgumentException("Player cannot be null");
        }
        return booster.getName() + "#GLOBAL";
    }
}