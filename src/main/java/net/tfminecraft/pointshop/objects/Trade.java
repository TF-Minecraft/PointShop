package net.tfminecraft.pointshop.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.tfminecraft.tlibs.TLibs;
import net.tfminecraft.tlibs.objects.api.ItemAPI;
import net.tfminecraft.pointshop.loaders.PointLoader;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trade {
    private static final Pattern COMPACT_PATTERN =
            Pattern.compile("^\\s*\"(.*)\"\\s+([0-9]*\\.?[0-9]+)\\s*$");

    private final String id;
    private final ItemStack menuItem;
    private final PointInstance type;
    private final int cost;
    private final List<WeightedResult> results;

    public Trade(String key, ConfigurationSection config) {
        this.id = key;

        ItemAPI api = TLibs.getItemAPI();
        this.menuItem = api.getCreator().getItemFromConfig(config.getConfigurationSection("item"));
        this.type = PointLoader.getByString(config.getString("type"));
        this.cost = config.getInt("cost");

        // Parse results (new format preferred)
        this.results = parseResults(config);
        // Back-compat: single "result" string
        if (this.results.isEmpty()) {
            String single = config.getString("result");
            if (single != null && !single.isEmpty()) {
                this.results.add(new WeightedResult(single, 1.0));
            }
        }
        // Final safety: ensure at least one entry to avoid NPEs downstream
        if (this.results.isEmpty()) {
            this.results.add(new WeightedResult("say No results configured for trade " + id, 1.0));
        }
    }

    private static List<WeightedResult> parseResults(ConfigurationSection config) {
        List<WeightedResult> list = new ArrayList<>();

        // 1) Map-list style: results: - command: "...", weight: 0.1
        List<Map<?, ?>> mapList = config.getMapList("results");
        if (mapList != null && !mapList.isEmpty()) {
            for (Map<?, ?> m : mapList) {
                Object cmdObj = m.get("command");
                Object wObj = m.get("weight");
                if (cmdObj instanceof String) {
                    String cmd = (String) cmdObj;
                    double weight = 1.0;
                    if (wObj instanceof Number) {
                        weight = ((Number) wObj).doubleValue();
                    } else if (wObj instanceof String) {
                        try { weight = Double.parseDouble((String) wObj); } catch (NumberFormatException ignored) {}
                    }
                    if (weight < 0) weight = 0; // no negative weights
                    list.add(new WeightedResult(cmd, weight));
                }
            }
            return list;
        }

        // 2) Compact list style: results: - "\"command here\" 0.1"
        if (config.isList("results")) {
            List<?> raw = config.getList("results");
            if (raw != null) {
                for (Object o : raw) {
                    if (o instanceof String) {
                        Matcher matcher = COMPACT_PATTERN.matcher((String) o);
                        if (matcher.matches()) {
                            String cmd = matcher.group(1);
                            double weight = 1.0;
                            try { weight = Double.parseDouble(matcher.group(2)); } catch (NumberFormatException ignored) {}
                            if (weight < 0) weight = 0;
                            list.add(new WeightedResult(cmd, weight));
                        } else {
                            // If it doesn't match, treat as plain command with default weight
                            list.add(new WeightedResult((String) o, 1.0));
                        }
                    }
                }
            }
        }

        return list;
    }

    /** Returns one command chosen by weight. Always returns something. */
    public String getWeightedResult(Random random) {
        if (results.size() == 1) return results.get(0).command;

        double total = 0.0;
        for (WeightedResult r : results) {
            total += Math.max(0.0, r.weight);
        }

        // If all weights are zero, treat as uniform
        if (total <= 0.0) {
            return results.get(random.nextInt(results.size())).command;
        }

        double roll = random.nextDouble() * total;
        double cumulative = 0.0;
        for (WeightedResult r : results) {
            cumulative += Math.max(0.0, r.weight);
            if (roll <= cumulative) {
                return r.command;
            }
        }

        // Fallback (floating point edge case)
        return results.get(results.size() - 1).command;
    }

    // --- Getters ---

    public String getId() { return id; }
    public ItemStack getMenuItem() { return menuItem; }
    public PointInstance getType() { return type; }
    public int getCost() { return cost; }
    public List<WeightedResult> getResults() { return Collections.unmodifiableList(results); }

    // --- Helper class ---
    public static class WeightedResult {
        public final String command;
        public final double weight;

        public WeightedResult(String command, double weight) {
            this.command = command;
            this.weight = weight;
        }
    }
}

