package com.xironite.buildedit.utils;

import com.xironite.buildedit.models.BlockPlaceInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class WeightParser {

    // region Fields
    private final long size;
    private double totalWeight;
    private final double weightRatio;
    private final Random random;
    private final Map<BlockPlaceInfo, Double> exactPredictedBlocks;
    private final Map<BlockPlaceInfo, Long> allocatedBlocks;
    private final Map<BlockPlaceInfo, Long> blocks;
    // endregion

    // region Constructor
    public WeightParser(long paramSize, List<BlockPlaceInfo> paramBlocks) {
        this.size = paramSize;
        this.totalWeight = paramBlocks
                .stream()
                .mapToDouble(BlockPlaceInfo::getPercentage)
                .sum();
        this.weightRatio = this.size / this.totalWeight;
        this.random = new Random();
        this.exactPredictedBlocks = paramBlocks
                .stream()
                .collect(Collectors.toMap(
                        block -> block,
                        x -> x.getPercentage() * this.weightRatio));
        this.allocatedBlocks = paramBlocks
                .stream()
                .collect(Collectors.toMap(
                        block -> block,
                        x -> (long) Math.floor(x.getPercentage() * this.weightRatio)));
        this.blocks = Collections.unmodifiableMap(this.allocatedBlocks);
        this.addDiscrepancyBlocks();
    }
    // endregion

    // region Methods
    public BlockPlaceInfo selectBlock() {
        long randomValue = this.random.nextLong(1, this.size + 1);
        long currentWeight = 0;

        for (var entry : this.allocatedBlocks.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue <= currentWeight && entry.getValue() > 0) {
                // Decrease the weight by 1
                long newWeight = entry.getValue() - 1;
                this.allocatedBlocks.put(entry.getKey(), newWeight);

                // Update total weight
                this.totalWeight--;
                return entry.getKey();
            }
        }
        // Fallback in case something goes wrong
        return this.allocatedBlocks.keySet().iterator().next();
    }

    /**
     * Checks if the inventory contains all the required blocks.
     *
     * @param inventory the inventory to check.
     * @return true if the player has all required blocks, false otherwise.
     */
    public boolean hasBlocks(Inventory inventory) {
        for (var entry : this.blocks.entrySet()) {
            Material material = entry.getKey().getBlock();
            long amount = entry.getValue();
            if (amount > 0 && !inventory.contains(material, (int) amount)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a map of materials and their missing amounts from the inventory.
     *
     * @param inventory The inventory to check for materials
     * @return A map of materials and the amounts that are missing from the inventory
     */
    public Map<Material, Long> getMissingBlocks(Inventory inventory) {
        Map<Material, Long> missingBlocks = new HashMap<>();
        for (var entry : this.blocks.entrySet()) {
            Material material = entry.getKey().getBlock();
            long amount = entry.getValue();

            if (amount > 0) {
                // Count how many of this material we have in the inventory
                long countInInventory = 0;
                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.getType() == material) {
                        countInInventory += item.getAmount();
                    }
                }

                // If we don't have enough, add to missing blocks
                if (countInInventory < amount) {
                    long missingAmount = amount - countInInventory;
                    missingBlocks.put(material, missingAmount);
                }
            }
        }
        return missingBlocks;
    }

    private void addDiscrepancyBlocks() {
        long discrepancy = (this.size - this.allocatedBlocks.values().stream().mapToLong(Long::longValue).sum());
        for (int i = 0; i < discrepancy; i++) {
            BlockPlaceInfo[] remainder = this.exactPredictedBlocks.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .toArray(BlockPlaceInfo[]::new);
            this.allocatedBlocks.compute(remainder[i], (BlockPlaceInfo key, Long value) -> value + 1);
        }
    }
    // endregion


}