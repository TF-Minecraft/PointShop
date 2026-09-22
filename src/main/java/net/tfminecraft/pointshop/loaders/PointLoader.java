package net.tfminecraft.pointshop.loaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.tfminecraft.tlibs.interfaces.LoaderInterface;
import net.tfminecraft.pointshop.objects.PointInstance;

public class PointLoader implements LoaderInterface{
	static List<PointInstance> oList = new ArrayList<PointInstance>();
	public static void clear() {
		oList.clear();
	}
	public static List<PointInstance> get() {
		return oList;
	}
	public static PointInstance getByString(String id) {
		for(PointInstance o : oList) {
			if(o.getId().equalsIgnoreCase(id)) return new PointInstance(o);
		}
		return null;
	}
	public static PointInstance getByName(String id) {
		for(PointInstance o : oList) {
			if(o.getName().equalsIgnoreCase(id)) return new PointInstance(o);
		}
		return null;
	}
	public void load(File configFile) {
		clear();
		FileConfiguration config = new YamlConfiguration();
        try {
        	config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Set<String> set = config.getKeys(false);

		List<String> list = new ArrayList<String>(set);
		
		for(String key : list) {
			PointInstance o = new PointInstance(key, config.getConfigurationSection(key));
			oList.add(o);
		}
	}
}
