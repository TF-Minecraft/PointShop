package net.tfminecraft.pointshop.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class PlayerData {
	private Player player;
	private List<PointInstance> points = new ArrayList<>();
	
	public PlayerData(Player p) {
		this.player = p;
	}

	public Player getPlayer() {
		return player;
	}

	public List<PointInstance> getPoints() {
		return points;
	}
	
	public void addPoints(PointInstance i) {
		this.points.add(i);
	}
	
	public void increasePoints(PointInstance type, int amount) {
		for(PointInstance i : points) {
			if(i.getId().equalsIgnoreCase(type.getId())) {
				i.increase(amount);
				return;
			}
		}
		PointInstance newPoints = new PointInstance(type);
		newPoints.setAmount(amount);
		addPoints(newPoints);
	}
	public void decreasePoints(PointInstance type, int amount) {
		for(PointInstance i : points) {
			if(i.getId().equalsIgnoreCase(type.getId())) {
				i.decrease(amount);
				return;
			}
		}
	}
	public int getPoints(PointInstance type) {
		for(PointInstance i : points) {
			if(i.getId().equalsIgnoreCase(type.getId())) {
				return i.getAmount();
			}
		}
		return 0;
	}
	public boolean hasPoints(PointInstance type, int amount) {
		for(PointInstance i : points) {
			if(i.getId().equalsIgnoreCase(type.getId())) {
				if(i.getAmount() >= amount) return true;
			}
		}
		return false;
	}
}
