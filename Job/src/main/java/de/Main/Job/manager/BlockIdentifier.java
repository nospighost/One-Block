package de.Main.Job.manager;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockIdentifier implements Listener {
    public static final String PLACED_METADATA_KEY = "placed_block";
    private final NamespacedKey placedKey;
    private final JavaPlugin plugin;

    public BlockIdentifier(JavaPlugin plugin) {
        this.placedKey = new NamespacedKey(plugin, "player_placed");
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.getBlock().setMetadata(PLACED_METADATA_KEY, new FixedMetadataValue(plugin, true));

    }
}
