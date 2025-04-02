package com.xironite.buildedit.utils;

import com.xironite.buildedit.Main;
import com.xironite.buildedit.models.BlockPlaceInfo;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WeightParser {

    // region Fields
    Map<BlockPlaceInfo, Integer> chances;
    Integer totalWeight;
    private Random random;
    // endregion

    // region Constructor
    public WeightParser(long paramSize, List<BlockPlaceInfo> paramBlocks) {
        chances = new HashMap<BlockPlaceInfo, Integer>();
        totalWeight = 0;
        random = new Random();
        double totalPercentage = paramBlocks
                .stream()
                .mapToDouble(BlockPlaceInfo::getPercentage)
                .sum();
        for (BlockPlaceInfo block : paramBlocks) {
            double percentage = block.getPercentage() / totalPercentage;
            int weight = (int) Math.round(paramSize * percentage);
            chances.put(block, weight);
            totalWeight += weight;
            Main.getPlugin().getLogger().info("Chances for " + block.getBlock() + ";" + weight);
        }
        Main.getPlugin().getLogger().info("Total weight is " + totalWeight);
    }
    // endregion

    // region Methods
    public Material selectBlock() {
        int randomValue = random.nextInt(1, totalWeight + 1);
        int currentWeight = 0;

        for (var entry : chances.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue <= currentWeight && entry.getValue() > 0) {
                // Decrease the weight by 1
                int newWeight = entry.getValue() - 1;
                chances.put(entry.getKey(), newWeight);

                // Update total weight
                totalWeight--;
                return entry.getKey().getBlock();
            }
        }

        // Fallback in case something goes wrong
        return chances.keySet().iterator().next().getBlock();
    }
    // endregion


}
