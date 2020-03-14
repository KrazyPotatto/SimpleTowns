package me.kpotatto.simpletowns.events;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.towns.Claim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.Optional;

public class BlocksEvent implements Listener {

    SimpleTowns pl;

    public BlocksEvent(SimpleTowns pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
        cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); })){
            Claim claim = pl.claims.stream().filter(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
                    cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); }).findFirst().get();
            Town t = pl.towns.get(claim.getTown_name());
            if(t.getMembers().stream().noneMatch(uuid -> uuid.toString().equals(e.getPlayer().getUniqueId().toString()))){
                e.setCancelled(true);
                e.getPlayer().sendMessage("You are not authorized to build here");
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
                cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); })){
            Claim claim = pl.claims.stream().filter(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
                    cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); }).findFirst().get();
            Town t = pl.towns.get(claim.getTown_name());
            if(t.getMembers().stream().noneMatch(uuid -> uuid.toString().equals(e.getPlayer().getUniqueId().toString()))){
                e.setCancelled(true);
                e.getPlayer().sendMessage("You are not authorized to build here");
            }
        }
    }

    @EventHandler
    public void onBlockExplose(EntityExplodeEvent e){
        for(Block b: e.blockList()){
            if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(b.getWorld().getName()) &&
                    cl.getChunkX() == b.getChunk().getX() && cl.getChunkZ() == b.getChunk().getZ(); })){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e){
        if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
                cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); })){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFireSpred(BlockSpreadEvent e){
        if(e.getSource().getType() == Material.FIRE){
            if(pl.claims.stream().anyMatch(cl -> { return cl.getWorld_name().equals(e.getBlock().getWorld().getName()) &&
                    cl.getChunkX() == e.getBlock().getChunk().getX() && cl.getChunkZ() == e.getBlock().getChunk().getZ(); })){
                e.setCancelled(true);
            }
        }
    }

}
