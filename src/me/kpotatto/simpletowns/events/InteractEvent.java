package me.kpotatto.simpletowns.events;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.towns.Claim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

    private SimpleTowns pl;

    public InteractEvent(SimpleTowns simpleTowns) {
        this.pl = simpleTowns;
    }

    @EventHandler
    public void onInterract(PlayerInteractEvent e){
        if(e.getClickedBlock() != null){
            if(e.getClickedBlock().getType().name().contains("CHEST") || e.getClickedBlock().getType().name().contains("DOOR") || e.getClickedBlock().getType().name().contains("GATE")){
                if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(e.getClickedBlock().getWorld().getName()) &&
                        cl.getChunkX() == e.getClickedBlock().getChunk().getX() && cl.getChunkZ() == e.getClickedBlock().getChunk().getZ(); })){
                    Claim claim = pl.claims.stream().filter(cl -> { return cl.getWorld_name().equals(e.getClickedBlock().getWorld().getName()) &&
                            cl.getChunkX() == e.getClickedBlock().getChunk().getX() && cl.getChunkZ() == e.getClickedBlock().getChunk().getZ(); }).findFirst().get();
                    Town t = pl.towns.get(claim.getTown_name());
                    if(t.getMembers().stream().noneMatch(uuid -> uuid.toString().equals(e.getPlayer().getUniqueId().toString()))){
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(pl.config.getString("messages.cannotinteract"));
                    }
                }
            }
        }
    }
}
