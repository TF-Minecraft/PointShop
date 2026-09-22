package net.tfminecraft.pointshop.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.tfminecraft.pointshop.PointShop;
import net.tfminecraft.pointshop.loaders.TradeLoader;
import net.tfminecraft.pointshop.objects.PlayerData;
import net.tfminecraft.pointshop.objects.Trade;

public class InventoryManager {
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public void shopView(Player player, Inventory i, boolean update) {
		if(!update) {
			i = PointShop.plugin.getServer().createInventory(null, 27, "§7Point Shop");
		}
		for(int y = 0; y<TradeLoader.get().size();y++) {
			Trade t = TradeLoader.get().get(y);
			i.setItem(y, createTradeItem(t, PlayerManager.getByPlayer(player)));
		}
		if(!update) {
			player.openInventory(i);
		}
	}
	// Keep the existing legacy text representation, formatting, and exact-string comparisons.
	@SuppressWarnings("deprecation")
	public ItemStack createTradeItem(Trade t, PlayerData pd) {
		ItemStack i = t.getMenuItem();
		ItemMeta meta = i.getItemMeta();
		List<String> lore = new ArrayList<>();
		meta.setLore(t.getMenuItem().getItemMeta().getLore());
		String cost = "§6"+t.getCost() +" "+ t.getType().getName();
		if(t.getCost() > 1) cost = cost+"s";
		lore.add("§eCost: "+cost);
		lore.add(" ");
		int amount = pd.getPoints(t.getType());
		String current = "§6"+amount+" "+t.getType().getName();
		if(amount > 1 || amount == 0) current = current+"s";
		lore.add("§eYou have: "+current);
		meta.setLore(lore);
		NamespacedKey id = new NamespacedKey(PointShop.plugin, "id");
		meta.getPersistentDataContainer().set(id, PersistentDataType.STRING, t.getId());
		i.setItemMeta(meta);
		return i;
	}
}
