package net.tfminecraft.pointshop.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.tfminecraft.pointshop.loaders.PointLoader;
import net.tfminecraft.pointshop.managers.PlayerManager;
import net.tfminecraft.pointshop.objects.PlayerData;
import net.tfminecraft.pointshop.objects.PointInstance;

public class Database {
	private JSONObject json; // org.json.simple
    JSONParser parser = new JSONParser();

	public boolean load(Player p) {
		File file = new File("plugins/PointShop/Data",p.getUniqueId()+".json");
		if(!file.exists()) {
			return false;
		} else {
			try {
				json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				JSONArray pointsArray = (JSONArray) json.get("points");
				List<String> points = new ArrayList<String>();
				Integer i = 0;
				while(i < pointsArray.size()) {
					points.add(pointsArray.get(i).toString());
					i++;
				}
				PlayerData pd = new PlayerData(p);
				for(String s : points) {
					PointInstance point = PointLoader.getByString(s.split("\\.")[0]);
					point.setAmount(Integer.parseInt(s.split("\\.")[1]));
					pd.addPoints(point);
				}
				PlayerManager.players.add(pd);
        	} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	public void save(Player p) {
		try {
			PlayerData pd = PlayerManager.getByPlayer(p);
      if(pd == null) return;
			File file = new File("plugins/PointShop/Data",p.getUniqueId()+".json");
			if(file.exists() == true) {
				file.delete();
			}
			file.createNewFile();
        	PrintWriter pw = new PrintWriter(file, "UTF-8");
        	pw.print("{");
        	pw.print("}");
        	pw.flush();
        	pw.close();
            HashMap<String, Object> defaults = new HashMap<String, Object>();
        	json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        	defaults.put("name", p.getName());
        	List<PointInstance> points = pd.getPoints();
        	JSONArray pointsArray = new JSONArray();
        	for(PointInstance point : points) {
        		pointsArray.add(point.getId()+"."+point.getAmount());
        	}
        	defaults.put("points", pointsArray);
        	save(file, defaults);
        } catch (Throwable ex) {
			ex.printStackTrace();
        }
    }
	@SuppressWarnings("unchecked")
    public boolean save(File file, HashMap<String, Object> defaults) {
      try {
    	  JSONObject toSave = new JSONObject();
      
        for (String s : defaults.keySet()) {
          Object o = defaults.get(s);
          if (o instanceof String) {
            toSave.put(s, getString(s, defaults));
          } else if (o instanceof Double) {
            toSave.put(s, getDouble(s, defaults));
          } else if (o instanceof Integer) {
            toSave.put(s, getInteger(s, defaults));
          } else if (o instanceof JSONObject) {
            toSave.put(s, getObject(s, defaults));
          } else if (o instanceof JSONArray) {
            toSave.put(s, getArray(s, defaults));
          }
        }
      
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        treeMap.putAll(toSave);
      
       Gson g = new GsonBuilder().setPrettyPrinting().create();
       String prettyJsonString = g.toJson(treeMap);
      
        FileWriter fw = new FileWriter(file);
        fw.write(prettyJsonString);
        fw.flush();
        fw.close();
      
        return true;
      } catch (Exception ex) {
        ex.printStackTrace();
        return false;
      }
    }
    
    public String getRawData(String key, HashMap<String, Object> defaults) {
        return json.containsKey(key) ? json.get(key).toString()
           : (defaults.containsKey(key) ? defaults.get(key).toString() : key);
      }
    
      // Keep the existing legacy text representation, formatting, and exact-string comparisons.
      @SuppressWarnings("deprecation")
      public String getString(String key, HashMap<String, Object> defaults) {
        return ChatColor.translateAlternateColorCodes('&', getRawData(key, defaults));
      }

      public boolean getBoolean(String key, HashMap<String, Object> defaults) {
        return Boolean.valueOf(getRawData(key, defaults));
      }

      public double getDouble(String key, HashMap<String, Object> defaults) {
        try {
          return Double.parseDouble(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }

      public double getInteger(String key, HashMap<String, Object> defaults) {
        try {
          return Integer.parseInt(getRawData(key, defaults));
        } catch (Exception ex) { }
        return -1;
      }
     
      public JSONObject getObject(String key, HashMap<String, Object> defaults) {
         return json.containsKey(key) ? (JSONObject) json.get(key)
           : (defaults.containsKey(key) ? (JSONObject) defaults.get(key) : new JSONObject());
      }
     
      public JSONArray getArray(String key, HashMap<String, Object> defaults) {
    	     return json.containsKey(key) ? (JSONArray) json.get(key)
    	       : (defaults.containsKey(key) ? (JSONArray) defaults.get(key) : new JSONArray());
      }
}
