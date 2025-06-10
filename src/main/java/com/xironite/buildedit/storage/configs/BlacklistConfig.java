package com.xironite.buildedit.storage.configs;

import com.xironite.buildedit.Main;
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
    private final Set<String> blacklist;
    private final Set<Pattern> blacklistPatterns;

    public BlacklistConfig(JavaPlugin paramPlugin, String paramFileName) {
        super(paramPlugin, paramFileName);
        this.blacklist = getStringList("blacklist").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        this.blacklistPatterns = mapToPatterns();
    }

    public void reload() {
        super.reload();
        this.blacklist.clear();
        this.blacklist.addAll(getStringList("blacklist").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet()));
        this.blacklistPatterns.clear();
        this.blacklistPatterns.addAll(mapToPatterns());
    }

    public boolean isBlacklisted(String item) {
        String upperItem = item.toUpperCase();
        return blacklistPatterns.stream()
                .anyMatch(pattern -> pattern.matcher(upperItem).matches());
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

    private Set<Pattern> mapToPatterns() {
        return blacklist.stream()
                .map(patternString -> {
                    try {
                        return Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
                    } catch (Exception e) {
                        Main.getPlugin().getLogger().warning("Error while parsing blacklist pattern: " + patternString);
                        return Pattern.compile(Pattern.quote(patternString), Pattern.CASE_INSENSITIVE);
                    }
                })
                .collect(Collectors.toSet());
    }
}
