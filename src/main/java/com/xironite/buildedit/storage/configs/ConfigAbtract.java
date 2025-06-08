package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.enums.ConfigSection;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigAbtract {

    // region Fields
    protected final JavaPlugin plugin;
    protected final String fileName;
    protected File file;
    @Getter
    protected FileConfiguration config;
    // endregion

    // region Constructor
    public ConfigAbtract(JavaPlugin paramPlugin, String paramFileName) {
        this.plugin = paramPlugin;
        this.fileName = paramFileName + ".yml";
        createCustomConfig();
    }
    // endregion

    // region Methods
    public void createCustomConfig() {
        file = new File(plugin.getDataFolder(), fileName);

        // Create parent directories if needed
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        // Create the file if it doesn't exist
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        // Load the configuration
        config = YamlConfiguration.loadConfiguration(file);
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Error loading configuration file: " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void reload() {
        try {
            config = YamlConfiguration.loadConfiguration(file);
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Error reloading configuration file: " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save configuration file: " + fileName);
            plugin.getLogger().warning(e.getMessage());
        }
    }

    /**
     * Gets a value from the configuration for the specified ConfigSection.
     * This can return various types (String, Integer, Boolean, List, etc.)
     * depending on what is stored in the configuration.
     *
     * @param section The ConfigSection to get the value for
     * @return The value from the configuration, or null if not found
     */
    public Object getValue(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.get(section.value);
        }
        return null;
    }

    /**
     * Gets a String value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The String value, or null if not found or not a String
     */
    public String get(ConfigSection section) {
        if (config.contains(section.value)) {
            if (config.isList(section.value))
                return String.join("\n", config.getStringList(section.value));
            else return config.getString(section.value);
        }
        return null;
    }

    /**
     * Gets a String value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The String value, or null if not found or not a String
     */
    public String get(String section) {
        if (config.contains(section)) {
            return config.getString(section);
        }
        return null;
    }

    public Color getColor(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getColor(section.value);
        }
        return null;
    }

    /**
     * Gets an Integer value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Integer value, or null if not found or not an Integer
     */
    public Integer getInt(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getInt(section.value);
        }
        return null;
    }

    /**
     * Gets an Integer value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Integer value, or null if not found or not an Integer
     */
    public Integer getInt(String section) {
        if (config.contains(section)) {
            return config.getInt(section);
        }
        return null;
    }

    /**
     * Gets a Double value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Double value, or null if not found
     */
    public Double getDouble(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getDouble(section.value);
        }
        return null;
    }

    /**
     * Gets a Float value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Float value, or null if not found
     */
    public Float getFloat(ConfigSection section) {
        if (config.contains(section.value)) {
            return (float) config.getDouble(section.value);
        }
        return null;
    }

    /**
     * Gets a Boolean value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Boolean value, or null if not found or not a Boolean
     */
    public Boolean getBoolean(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getBoolean(section.value);
        }
        return null;
    }

    /**
     * Gets a Boolean value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The Boolean value, or null if not found or not a Boolean
     */
    public Boolean getBoolean(String section) {
        if (config.contains(section)) {
            return config.getBoolean(section);
        }
        return null;
    }

    /**
     * Gets a List value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The List value, or null if not found or not a List
     */
    public List<?> getList(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getList(section.value);
        }
        return null;
    }

    /**
     * Gets a List value from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The List value, or null if not found or not a List
     */
    public List<?> getList(String section) {
        if (config.contains(section)) {
            return config.getList(section);
        }
        return null;
    }

    /**
     * Gets a List of Strings from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The List of Strings, or null if not found or not a List of Strings
     */
    public List<String> getStringList(ConfigSection section) {
        if (config.contains(section.value)) {
            return config.getStringList(section.value);
        }
        return List.of();
    }

    /**
     * Gets a List of Strings from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The List of Strings, or null if not found or not a List of Strings
     */
    public List<String> getStringList(String section) {
        if (config.contains(section)) {
            return config.getStringList(section);
        }
        return List.of();
    }

    /**
     * Gets a List of Strings from the configuration for the specified ConfigSection.
     *
     * @param section The ConfigSection to get the value for
     * @return The List of Strings, or null if not found or not a List of Strings
     */
    public List<String> getKeys(ConfigSection section) {
        if (config.contains(section.value)) {
            ConfigurationSection configSection = config.getConfigurationSection(section.value);
            if (configSection != null) {
                return new ArrayList<>(configSection.getKeys(false));
            }
        }
        return new ArrayList<>();
    }

    /**
     * Gets a List of Strings from the configuration for the specified ConfigSection.
     *
     * @param key Check if the key exists
     * @return The List of Strings, or null if not found or not a List of Strings
     */
    public boolean contains(String key) {
        return config.contains(key);
    }
    // endregion
}