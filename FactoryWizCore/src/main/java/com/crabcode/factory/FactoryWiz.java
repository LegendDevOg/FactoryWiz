package com.crabcode.factory;


import com.crabcode.factory.data.DataManager;
import com.crabcode.factory.data.FileDataManager;
import com.crabcode.factory.data.MySQLDataManager;
import com.crabcode.factory.data.SQLiteDataManager;
import com.crabcode.factory.entities.EntityRenderer;
import com.crabcode.factory.entities.FakeEntityAPI;
import com.crabcode.factory.entities.IFakeEntity;
import com.crabcode.factory.entities.entity.ArmorStandRenderer;
import com.crabcode.factory.util.ActionBarUtil;
import com.crabcode.factory.util.Logger;
import com.crabcode.factory.util.Scheduler;
import com.crabcode.factory.util.TimeoutMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

import static com.crabcode.factory.reflect.Reflection.findClass;
import static com.crabcode.factory.reflect.Reflection.setFieldValue;

public class FactoryWiz extends JavaPlugin implements Listener {

    private static FactoryWiz instance;
    private static DataManager dataManager;
    private static File factoryDirectory;

    @Override
    public void onLoad() {
        instance = this;
        Logger.initialize(this.getLogger());
        factoryDirectory = this.getDataFolder();
        setFieldValue(Scheduler.class, null, "instance", this);
        setFieldValue(TimeoutMetadata.class, null, "instance", this);
    }
    @Override
    public void onEnable() {
        // Initialize the configuration file
        this.saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        FileConfiguration config = this.getConfig();
        String type = config.getString("database.type").toUpperCase(Locale.ENGLISH);
        switch (type) {
            case "MYSQL":
                dataManager = new MySQLDataManager(
                        config.getString("database.host"),
                        config.getInt("database.port"),
                        config.getString("database.schema"),
                        config.getString("database.user"),
                        config.getString("database.password")
                );
                break;
            case "SQLITE":
                dataManager = new SQLiteDataManager(new File(factoryDirectory, "factorywiz.db"));
                break;
            case "FILE":
                dataManager = new FileDataManager(new File(factoryDirectory, "factorywiz.cimg"));
                break;
            default:
                throw new IllegalStateException("Unknown database type: " + type);
        }

        if (config.getBoolean("database.initialize")) {
            dataManager.initialize();
        }

        FakeEntityAPI.init();
    }

    @EventHandler
    // This is called directly after the PlayerConnection
    // is set as the packetListener for the player
    public void onJoin(PlayerInteractEvent event) {

        IFakeEntity ife =  FakeEntityAPI.get().spawn(UUID.randomUUID(), event.getPlayer().getLocation());
        ArmorStandRenderer ars = ife.setRenderer(ArmorStandRenderer.class);

        ars.setHelmet(new ItemStack(Material.COAL));


    }

    /**
     * Get the directory in which the factory machines are stored.
     *
     * @return The factory directory.
     */
    public static File getFactoryDirectory() {
        return factoryDirectory;
    }

    /**
     * Get the {@link DataManager} for the data storage.
     *
     * @return The data manger.
     */
    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     * Get the singleton instance of this plugin.
     *
     * @return The plugin instance.
     */
    public static FactoryWiz getInstance() {
        return instance;
    }

}