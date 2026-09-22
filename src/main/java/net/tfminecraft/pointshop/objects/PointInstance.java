package net.tfminecraft.pointshop.objects;

import org.bukkit.configuration.ConfigurationSection;

import net.tfminecraft.tlibs.objects.api.subapi.StringFormatter;

public class PointInstance {
	private String id;
	private String name;
	private int amount;
	
	public PointInstance(String key, ConfigurationSection config) {
		this.id = key;
		this.name = StringFormatter.formatHex(config.getString("name", "Point"));
		this.amount = 0;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void increase(int a) {
		this.amount = this.amount+a;
	}
	public void decrease(int a) {
		this.amount = this.amount-a;
		if(this.amount < 0) {
			this.amount = 0;
		}
	}
	public PointInstance(PointInstance another) {
		this.id = another.getId();
		this.name = another.getName();
		this.amount = another.getAmount();
	}
}
