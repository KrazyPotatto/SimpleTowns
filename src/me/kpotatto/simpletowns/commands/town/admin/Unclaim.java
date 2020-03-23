package me.kpotatto.simpletowns.commands.town.admin;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.towns.Claim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Unclaim {

    public static void execute(Player p){
        SimpleTowns pl = SimpleTowns.getInstance();
        Chunk c = p.getLocation().getChunk();
        if(pl.claims.stream().anyMatch(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());})){
            Optional<Claim> oclaim = pl.claims.stream().filter(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());}).findFirst();
            if(oclaim.isPresent()){
                Claim claim = oclaim.get();
                Town t = pl.towns.get(claim.getTown_name());
                List<Claim> claims = t.getClaims();
                claims.remove(claim);
                t.setClaims(claims);
                pl.towns.remove(t.getName());
                pl.towns.put(t.getName(), t);
                pl.claims.remove(claim);
                File save = new File(pl.townsPath, t.getName()+".json");
                if(save.exists()){
                    save.delete();
                }
                String json = pl.serializerManager.serialize(t);
                pl.serializerManager.saveToFile(save, json);
                p.sendMessage(pl.config.getString("messages.chunk.unclaim"));
            }
        }
    }

}
