package net.tfminecraft.pointshop.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.tfminecraft.pointshop.PointShop;
import net.tfminecraft.pointshop.loaders.PointLoader;
import net.tfminecraft.pointshop.objects.PlayerData;
import net.tfminecraft.pointshop.objects.PointInstance;
import net.tfminecraft.pointshop.utils.Permissions;

import java.util.*;
import java.util.stream.Collectors;

public class CommandManager implements Listener, CommandExecutor, TabCompleter {
    public String cmd1 = "pointshop";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(cmd1) && args.length == 0) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                InventoryManager i = new InventoryManager();
                i.shopView(player, null, false);
                return true;
            }
        } else if(cmd.getName().equalsIgnoreCase(cmd1) && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if(Permissions.isAdmin(sender)) {
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    JavaPlugin.getPlugin(PointShop.class).reloadMessage(p);
                } else {
                    JavaPlugin.getPlugin(PointShop.class).reload();
                }
                return true;
            }
            if(sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage("§a[PointShop] §cYou do not have access to this command");
            }
        } else if(cmd.getName().equalsIgnoreCase(cmd1) && args.length == 4 && args[0].equalsIgnoreCase("givepoints")) {
            if(Permissions.isAdmin(sender)) {
                Player t = Bukkit.getPlayerExact(args[1]);
                if(t == null) {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        p.sendMessage("§a[PointShop] §cNo player by the name "+args[1]);
                    }
                    return true;
                }
                PointInstance type = PointLoader.getByString(args[2]);
                if(type == null) {
                    if(sender instanceof Player) {
                        Player p = (Player) sender;
                        p.sendMessage("§a[PointShop] §cNo point type by the id "+args[2]);
                    }
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("§a[PointShop] §cAmount must be a number.");
                    return true;
                }
                PlayerData pd = PlayerManager.getByPlayer(t);
                pd.increasePoints(type, amount);
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    p.sendMessage("§a[PointShop] §e"+t.getName()+" now has "+pd.getPoints(type)+" "+type.getName()+"(s)");
                    t.sendMessage("§a[PointShop] §eYou now have "+pd.getPoints(type)+" "+type.getName()+"(s)");
                }
                return true;
            }
            if(sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage("§a[PointShop] §cYou do not have access to this command");
            }
        }
        return false;
    }

    // ---------------- TAB COMPLETION ----------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!cmd.getName().equalsIgnoreCase(cmd1)) return Collections.emptyList();

        boolean isAdmin = Permissions.isAdmin(sender);

        if (args.length == 1) {
            // /pointshop <subcommand>
            List<String> base = new ArrayList<>();
            if (isAdmin) {
                base.add("reload");
                base.add("givepoints");
            }
            return prefix(base, args[0]);
        }

        // /pointshop givepoints <player> <type> <amount>
        if (args.length == 2 && equalsIgnoreCase(args[0], "givepoints") && isAdmin) {
            // players online
            List<String> names = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            return prefix(names, args[1]);
        }

        if (args.length == 3 && equalsIgnoreCase(args[0], "givepoints") && isAdmin) {
            // point types (IDs)
            // Provide your own accessor for registered types if needed.
            // Replace 'listPointTypeIds()' with whatever your loader exposes.
            List<String> types = listPointTypeIds();
            return prefix(types, args[2]);
        }

        if (args.length == 4 && equalsIgnoreCase(args[0], "givepoints") && isAdmin) {
            // some common amounts
            List<String> common = Arrays.asList("1", "5", "10", "25", "50", "100", "250", "500", "1000");
            return prefix(common, args[3]);
        }

        return Collections.emptyList();
    }

    // Helper to expose your PointLoader’s known type IDs.
    // Implement this to pull from your actual data source.
    private List<String> listPointTypeIds() {
        // If PointLoader already has a method, use that here.
        // Examples you might have or add:
        // return PointLoader.getAll().stream().map(PointInstance::getId).collect(Collectors.toList());
        // or
        // return new ArrayList<>(PointLoader.getRegisteredIds());
        //
        // Fallback (so it compiles). Replace with real data.
		List<String> completions = new ArrayList<>();
		for(PointInstance point : PointLoader.get()) {
			completions.add(point.getId());
		}
        return completions;
    }

    private static List<String> prefix(Collection<String> options, String arg) {
        String a = arg == null ? "" : arg.toLowerCase(Locale.ROOT);
        List<String> out = options.stream()
                .filter(s -> s != null && s.toLowerCase(Locale.ROOT).startsWith(a))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        return out.isEmpty() ? Collections.emptyList() : out;
    }

    private static boolean equalsIgnoreCase(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }
}
