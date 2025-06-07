package com.xironite.buildedit.utils;

import com.xironite.buildedit.models.BlockPlaceInfo;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BlockCalculator {

    // region Fields
    private long size;
    private final double weightRatio;
    private final Random random;
    private final Map<BlockPlaceInfo, Double> exactPredictedBlocks;
    private final Map<BlockPlaceInfo, Long> allocatedBlocks;
    private final Map<BlockPlaceInfo, Long> blocks;
    // endregion

    // region Constructor
    public BlockCalculator(long paramSize, List<BlockPlaceInfo> paramBlocks) {
        this.size = paramSize;
        double totalWeight = paramBlocks
                .stream()
                .mapToDouble(BlockPlaceInfo::getPercentage)
                .sum();
        this.weightRatio = this.size / totalWeight;
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
        long randomValue = random.nextLong(1, size + 1);
        long currentWeight = 0;

        for (var entry : allocatedBlocks.entrySet()) {
            currentWeight += entry.getValue();
            if (randomValue <= currentWeight && entry.getValue() > 0) {
                // Decrease the weight by 1
                long newWeight = entry.getValue() - 1;
                allocatedBlocks.put(entry.getKey(), newWeight);

                // Update total weight
                size--;
                return entry.getKey();
            }
        }
        // Fallback in case something goes wrong
        // This should never happen if the logic is correct
        return allocatedBlocks.keySet().iterator().next();
    }

    /**
     * Check if the player has any blocks remaining to place.
     *
     * @return true if there are blocks remaining, false otherwise.
     */
    public boolean hasBlocksRemaining() {
        return allocatedBlocks.values().stream().noneMatch(value -> value > 0);
    }

    /**
     * Checks if the inventory contains all the required blocks.
     *
     * @param inventory the inventory to check.e
     * @return true if the player has all required blocks, false otherwise.
     */
    public boolean hasBlocks(Inventory inventory) {
        for (var entry : blocks.entrySet()) {
            Material material = entry.getKey().getBlock();
            long amount = entry.getValue();
            if (amount > 0 && (!inventory.contains(material, (int) amount) && material != Material.AIR)) {
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
        for (var entry : blocks.entrySet()) {
            Material material = entry.getKey().getBlock();
            long amount = entry.getValue();

            if (amount > 0) {
                // Count how many of this material we have in the inventory
                long countInInventory = 0;
                for (ItemStack item : inventory.getContents()) {
                    if (item != null && (item.getType() == material && item.getType() != Material.AIR)) {
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

    /**
     * Consumes the blocks from the inventory.
     *
     * @param inventory The inventory to consume blocks from
     */
    public void consumeBlocks(Inventory inventory) {
        for (var entry : blocks.entrySet()) {
            if (entry.getValue() <= 0) continue;
            ItemStack item = new ItemStack(entry.getKey().getBlock(), (int) entry.getValue().longValue());
            inventory.removeItem(item);
        }
    }

    private void addDiscrepancyBlocks() {
        long discrepancy = (size - allocatedBlocks.values().stream().mapToLong(Long::longValue).sum());
        for (int i = 0; i < discrepancy; i++) {
            BlockPlaceInfo[] remainder = exactPredictedBlocks.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .map(Map.Entry::getKey)
                    .toArray(BlockPlaceInfo[]::new);
            allocatedBlocks.compute(remainder[i], (BlockPlaceInfo key, Long value) -> value + 1);
        }
    }
    // endregion


}