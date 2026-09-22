package net.tfminecraft.pointshop.managers;

import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.tfminecraft.pointshop.database.Database;
import net.tfminecraft.pointshop.objects.PlayerData;

public class PlayerManager implements Listener{
	public static List<PlayerData> players = new ArrayList<>();

	public static boolean remove(Player p) {
		for(PlayerData pd : players) {
			if(pd.getPlayer().equals(p)) {
				players.remove(pd);
				return true;
			}
		}
		return false;
	}
	
	public static PlayerData getByPlayer(Player p) {
		for(PlayerData pd : players) {
			if(pd.getPlayer().equals(p)) return pd;
		}
		Database db = new Database();
		if(!db.load(p)) {
			PlayerData pd = new PlayerData(p);
			players.add(pd);
			return pd;
		} else {
			for(PlayerData pd : players) {
				if(pd.getPlayer().equals(p)) return pd;
			}
		}
		return null;
	}
	public static boolean exists(Player p) {
		for(PlayerData pd : players) {
			if(pd.getPlayer().equals(p)) return true;
		}
		return false;
	}

	public static void init(Player p) {
		if(exists(p)) return;
		Database db = new Database();
		if(!db.load(p)) {
			players.add(new PlayerData(p));
		}
	}
	
	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		init(p);
	}
	@EventHandler
	public void quitEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(!exists(p)) return;
		Database db = new Database();
		db.save(p);
		remove(p);
	}
}
