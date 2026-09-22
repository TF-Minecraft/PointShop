package net.tfminecraft.pointshop;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.tfminecraft.pointshop.database.Database;
import net.tfminecraft.pointshop.loaders.PointLoader;
import net.tfminecraft.pointshop.loaders.TradeLoader;
import net.tfminecraft.pointshop.managers.CommandManager;
import net.tfminecraft.pointshop.managers.PlayerManager;
import net.tfminecraft.pointshop.managers.PointManager;


public class PointShop extends JavaPlugin{
	public static PointShop plugin;
	
	private final PointLoader pointLoader = new PointLoader();
	private final TradeLoader tradeLoader = new TradeLoader();
	
	private final CommandManager commandManager = new CommandManager();
	private final PointManager pointManager = new PointManager();
	private final PlayerManager playerManager = new PlayerManager();
	
	private final Database db = new Database();
	
	@Override
	public void onEnable() {
		plugin = this;
		createFolders();
		createConfigs();
		loadConfigs();
		registerListeners();
		getCommand(commandManager.cmd1).setExecutor(commandManager);
    	getCommand(commandManager.cmd1).setTabCompleter(commandManager);
		for(Player p : Bukkit.getOnlinePlayers()) {
			PlayerManager.init(p);
		}
	}
	
	@Override
	public void onDisable() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			db.save(p);
		}
	}
	
	public void registerListeners() {
		getServer().getPluginManager().registerEvents(commandManager, this);
		getServer().getPluginManager().registerEvents(pointManager, this);
		getServer().getPluginManager().registerEvents(playerManager, this);
	}
	public void loadConfigs() {
		pointLoader.load(new File(getDataFolder(), "points.yml"));
		tradeLoader.load(new File(getDataFolder(), "trades.yml"));
	}
	public void createFolders() {
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		File subFolder = new File(getDataFolder(), "Data");
		if(!subFolder.exists()) subFolder.mkdir();
	}
	
	public void createConfigs() {
		String[] files = {
				"points.yml",
				"trades.yml",
				};
		for(String s : files) {
			File newConfigFile = new File(getDataFolder(), s);
	        if (!newConfigFile.exists()) {
	        	newConfigFile.getParentFile().mkdirs();
	            saveResource(s, false);
	        }
		}
	}
	
	public void reload() {
		loadConfigs();
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void reloadMessage(Player p) {
		p.sendMessage(ChatColor.GREEN + "[PointShop]" + ChatColor.YELLOW + " Reloading plugin...");
		reload();
		p.sendMessage(ChatColor.GREEN + "[PointShop]" + ChatColor.YELLOW + " Reloading complete!");
	}
}
