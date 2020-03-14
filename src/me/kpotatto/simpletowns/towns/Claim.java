package me.kpotatto.simpletowns.towns;

import org.bukkit.Chunk;

public class Claim {

    private int chunkX,chunkZ;
    private String world_name,town_name;

    public Claim(Chunk c, String town_name) {
        this.chunkX = c.getX();
        this.chunkZ = c.getZ();
        this.world_name = c.getWorld().getName();
        this.town_name = town_name;
    }

    public Claim(int chunkX, int chunkZ, String world_name) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.world_name = world_name;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public String getWorld_name() {
        return world_name;
    }

    public void setWorld_name(String world_name) {
        this.world_name = world_name;
    }

    public String getTown_name() {
        return town_name;
    }

    public void setTown_name(String town_name) {
        this.town_name = town_name;
    }
}
