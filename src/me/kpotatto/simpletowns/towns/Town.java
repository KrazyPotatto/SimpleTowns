package me.kpotatto.simpletowns.towns;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Town {

    private String name, worldName;
    private UUID owner;
    private List<UUID> members, invited, admins;
    private List<Claim> claims;
    private double spawnX,spawnY,spawnZ;

    public Town(String name, UUID owner, Location spawn) {
        this.name = name;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.claims = new ArrayList<>();
        this.invited = new ArrayList<>();
        this.admins = new ArrayList<>();
        admins.add(owner);
        this.spawnX = spawn.getX();
        this.spawnY = spawn.getY();
        this.spawnZ = spawn.getZ();
        this.worldName = spawn.getWorld().getName();
        this.members.add(owner);
    }

    public Town(String name, UUID owner, List<UUID> members, List<UUID> invited, List<UUID> admins,List<Claim> claims, Location spawn) {
        this.name = name;
        this.owner = owner;
        this.members = members;
        this.invited = invited;
        this.admins = admins;
        this.claims = claims;
        this.spawnX = spawn.getX();
        this.spawnY = spawn.getY();
        this.spawnZ = spawn.getZ();
        this.worldName = spawn.getWorld().getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<UUID> getInvited() {
        return invited;
    }

    public void setInvited(List<UUID> invited) {
        this.invited = invited;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UUID> admins) {
        this.admins = admins;
    }

    public Location getSpawn() {
        return new Location(Bukkit.getWorld(worldName),spawnX,spawnY,spawnZ);
    }

    public void setSpawn(Location loc){ spawnX = loc.getX(); spawnY = loc.getY(); spawnZ = loc.getZ(); worldName = loc.getWorld().getName(); }
}
