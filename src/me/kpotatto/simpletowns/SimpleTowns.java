package me.kpotatto.simpletowns;

import me.kpotatto.simpletowns.commands.TownCommand;
import me.kpotatto.simpletowns.events.BlocksEvent;
import me.kpotatto.simpletowns.gson.SerializerManager;
import me.kpotatto.simpletowns.towns.Claim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTowns extends JavaPlugin {

    public SerializerManager serializerManager;
    private static SimpleTowns instance;
    public FileConfiguration config;
    public File townsPath = new File(getDataFolder(), "\\towns\\");

    public Map<String, Town> towns = new HashMap<>();
    public List<Claim> claims = new ArrayList<>();

    @Override
    public void onEnable() {

        super.onEnable();

        serializerManager = new SerializerManager();

        getConfig().options().copyHeader(true);
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("town").setExecutor(new TownCommand(this));

        getServer().getPluginManager().registerEvents(new BlocksEvent(this), this);
        config = getConfig();
        instance = this;
        System.out.println(townsPath.listFiles());
        readTownFiles(townsPath);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static SimpleTowns getInstance() {
        return instance;
    }

    public SerializerManager getSerializerManager() {
        return serializerManager;
    }

    private void readTownFiles(final File folder) throws NullPointerException{
        if(getDataFolder().exists()){
            if(townsPath.exists()){
                for(final File child : folder.listFiles()){
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(child));
                            StringBuilder text = new StringBuilder();
                            String line;
                            while((line = reader.readLine()) != null){
                                text.append(line);
                            }
                            reader.close();

                            Town town = serializerManager.deserializeTown(text.toString());
                            this.towns.put(town.getName(), town);
                            if(!town.getClaims().isEmpty())
                                this.claims.addAll(town.getClaims());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }else{
                townsPath.mkdirs();
            }
        }else{
            getDataFolder().mkdirs();
            townsPath.mkdirs();
        }
    }
}
