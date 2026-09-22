package net.tfminecraft.pointshop.managers;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.tfminecraft.pointshop.PointShop;
import net.tfminecraft.pointshop.loaders.TradeLoader;
import net.tfminecraft.pointshop.objects.PlayerData;
import net.tfminecraft.pointshop.objects.Trade;

public class PointManager implements Listener{
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	@EventHandler
	public void invenClick(InventoryClickEvent e) {
		if(e.getClickedInventory() == null) return;
		if(e.getCurrentItem() == null) return;
		Player p = (Player) e.getWhoClicked();
		if(e.getView().getTitle().equalsIgnoreCase("§7Point Shop")) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if(item == null) return;
			NamespacedKey id = new NamespacedKey(PointShop.plugin, "id");
			Trade t = TradeLoader.getByString(item.getItemMeta().getPersistentDataContainer().get(id, PersistentDataType.STRING));
			if(t == null) return;
			if(trade(p, t)) {
				InventoryManager inv = new InventoryManager();
				inv.shopView(p, e.getView().getTopInventory(), true);
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 1f);
			} else {
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				p.sendMessage("§cCannot afford this");
			}
		}
	}
	
	public boolean trade(Player p, Trade t) {
		PlayerData pd = PlayerManager.getByPlayer(p);
		if(!pd.hasPoints(t.getType(), t.getCost())) return false;
		pd.decreasePoints(t.getType(), t.getCost());
		Random rng = ThreadLocalRandom.current();
		String commandToRun = t.getWeightedResult(rng);
		PointShop.plugin.getServer().dispatchCommand(PointShop.plugin.getServer().getConsoleSender(), commandToRun.replace("#player#", p.getName()));
		return true;
	}
}
