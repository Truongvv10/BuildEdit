package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.models.BlockPlaceInfo;
import com.xironite.buildedit.utils.BlockMapper;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BlacklistConfig extends ConfigAbtract {

    @Getter
    private final Set<String> cache;

    public BlacklistConfig(JavaPlugin paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.cache = getStringList("blacklist").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
    }

    public void reload() {
        super.reload();
        this.cache.clear();
        this.cache.addAll(getStringList("blacklist").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet()));
    }

    public boolean isBlacklisted(String item) {
        return cache.contains(item.toUpperCase());
    }

    public boolean isBlacklisted(List<BlockPlaceInfo> blocks) {
        return blocks.stream()
                .map(BlockPlaceInfo::getBlock)  // Use getBlock() method reference
                .anyMatch(material -> isBlacklisted(material.name()));
    }

    public List<String> getBlacklisted(String args) {
        String[] splitArgs = args.split(",");
        Pattern pattern = Pattern.compile(BlockMapper.COMMANDS_PATTERN);
        return Arrays.stream(splitArgs)
                .filter(x -> {
                    Matcher matcher = pattern.matcher(x);
                    if (matcher.find()) {
                        String block = matcher.group(3) != null ? matcher.group(3) : "";
                        Material material = Material.matchMaterial(block);
                        if (material != null) {
                            return this.isBlacklisted(material.name());
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
