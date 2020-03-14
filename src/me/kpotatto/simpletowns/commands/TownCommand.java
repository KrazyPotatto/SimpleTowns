package me.kpotatto.simpletowns.commands;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.towns.Claim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class TownCommand implements CommandExecutor, TabCompleter {

    private final SimpleTowns pl;

    public TownCommand(SimpleTowns pl){
        this.pl = pl;
    }

    @Override
    @Deprecated
    public boolean onCommand(CommandSender s, Command cmd, String cmds, String[] args) {
        if(!cmd.getName().equalsIgnoreCase("town") && !cmd.getName().equalsIgnoreCase("t")){
            return false;
        }
        if(args.length == 0){
            sendHelpMessage(s);
            return false;
        }
        if(args[0].equalsIgnoreCase("list")){
            s.sendMessage(pl.config.getString("messages.listTop"));
            for (Map.Entry<String, Town> name : SimpleTowns.getInstance().towns.entrySet()){
                s.sendMessage(name.getKey());
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("printclaims")){
            if(s instanceof Player){
                Player plll = (Player)s;
                if(plll.isOp()) {sendHelpMessage(s); return false; }
            }
            Object[] cs = pl.claims.toArray();
            String txt = pl.serializerManager.serialize(cs);
            System.out.println(txt);
            return false;
        }
        if(!(s instanceof Player)){
            s.sendMessage("§cYou must be a player to execute this command!");
            return false;
        }
        Player p = (Player)s;
        if(args[0].equalsIgnoreCase("create")){
            if(args.length != 2){
                sendHelpMessage(s);
                return false;
            }
            for (Town town : pl.towns.values()){
                if(town.getOwner().toString().equals(p.getUniqueId().toString())){
                    s.sendMessage(pl.config.getString("messages.create.duplicate"));
                    return false;
                }
                if(town.getName().equalsIgnoreCase(args[1])){
                    s.sendMessage(pl.config.getString("messages.create.exists").replace("%name%", args[1]));
                    return false;
                }
            }
            String townName = args[1];
            Town town = new Town(townName,p.getUniqueId(), p.getLocation());
            pl.towns.put(townName, town);
            File save = new File(pl.townsPath, townName+".json");
            String json = pl.serializerManager.serialize(town);
            pl.serializerManager.saveToFile(save, json);
            s.sendMessage(pl.config.getString("messages.create.success"));
            return true;
        }
        if (args[0].equalsIgnoreCase("claim")) {
            Chunk c = p.getLocation().getChunk();
            if(args.length == 2){
                if(pl.claims.stream().noneMatch(claim -> claim.getChunkX() == c.getX() &&
                        claim.getChunkZ() == c.getZ() &&
                        claim.getWorld_name().equals(c.getWorld().getName()))){
                    if(pl.towns.containsKey(args[1])){
                        Town town = pl.towns.get(args[1]);
                        if(town.getAdmins().stream().anyMatch(uuid -> uuid.toString().equals(p.getUniqueId().toString()))){
                            List<Claim> claims = town.getClaims();
                            claims.add(new Claim(c,town.getName()));
                            town.setClaims(claims);
                            File save = new File(pl.townsPath, town.getName()+".json");
                            if(save.exists()){
                                save.delete();
                            }
                            Town ntown = new Town(town.getName(), town.getOwner(), town.getMembers(), town.getInvited(), town.getAdmins(), claims, town.getSpawn());
                            String json = pl.serializerManager.serialize(ntown);
                            pl.serializerManager.saveToFile(save, json);
                            pl.towns.remove(town.getName());
                            pl.towns.put(town.getName(), ntown);
                            pl.claims.add(new Claim(c, town.getName()));
                            s.sendMessage(pl.config.getString("messages.chunk.success"));
                        }else{
                            s.sendMessage(pl.config.getString("messages.notauthorize"));
                        }
                    }else{
                        s.sendMessage(pl.config.getString("messages.unknowtown").replaceAll("%name%",args[1]));
                    }
                }else{
                    Optional<Claim> claim = pl.claims.stream().filter(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());}).findFirst();
                    s.sendMessage(pl.config.getString("messages.chunk.already").replaceAll("%name%",claim.get().getTown_name()));
                }
            }else if(args.length == 1){
                //Without Town name
                for (Town town: pl.towns.values()) {
                    if(town.getOwner().toString().equals(p.getUniqueId().toString())){
                        if(pl.claims.stream().noneMatch(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());})){
                            List<Claim> claims = town.getClaims();
                            claims.add(new Claim(c,town.getName()));
                            town.setClaims(claims);
                            File save = new File(pl.townsPath, town.getName()+".json");
                            if(save.exists()){
                                save.delete();
                            }
                            Town ntown = new Town(town.getName(), town.getOwner(), town.getMembers(), town.getInvited(), town.getAdmins(), claims, town.getSpawn());
                            String json = pl.serializerManager.serialize(ntown);
                            pl.serializerManager.saveToFile(save, json);
                            pl.towns.remove(town.getName());
                            pl.towns.put(town.getName(), ntown);
                            pl.claims.add(new Claim(c, town.getName()));
                            s.sendMessage(pl.config.getString("messages.chunk.success"));
                            return false;
                        }else{
                            for(Claim claim: pl.claims){
                                if(c.getX() == claim.getChunkX() && c.getZ() == claim.getChunkZ()){
                                    s.sendMessage(pl.config.getString("messages.chunk.already").replaceAll("%name%",claim.getTown_name()));
                                    break;
                                }
                            }
                        }
                        return false;
                    }
                }
                s.sendMessage(pl.config.getString("messages.chunk.notowning"));
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("unclaim")) {
            Chunk c = p.getLocation().getChunk();
            if(pl.claims.stream().anyMatch(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());})){
                Optional<Claim> oclaim = pl.claims.stream().filter(cl -> {return cl.getChunkX() == c.getX() && cl.getChunkZ() == c.getZ() && cl.getWorld_name().equals(c.getWorld().getName());}).findFirst();
                if(oclaim.isPresent()){
                    Claim claim = oclaim.get();
                    Town t = pl.towns.get(claim.getTown_name());
                    if(t.getAdmins().stream().anyMatch(uuid -> {return uuid.toString().equals(p.getUniqueId().toString());})){
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
                        s.sendMessage(pl.config.getString("messages.chunk.unclaim"));
                    }else{
                        s.sendMessage(pl.config.getString("messages.notauthorize"));
                    }
                }
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("owner")){
            for (Town town: pl.towns.values()){
                if(town.getOwner().toString().equals(p.getUniqueId().toString())){
                    s.sendMessage(pl.config.getString("messages.owner.own").replaceAll("%name%",town.getName()));
                    return false;
                }
            }
            s.sendMessage(pl.config.getString("messages.owner.not"));
            return false;
        }
        if(args[0].equalsIgnoreCase("invite") && args.length == 3){
            Town t = pl.towns.get(args[1]);
            if(t != null){
                if(t.getAdmins().stream().anyMatch(uuid -> {return uuid.toString().equals(p.getUniqueId().toString());})){
                    Player target = Bukkit.getPlayer(args[2]);
                    if(target != null){
                        List<UUID> invited = t.getInvited();
                        if(!invited.contains(target.getUniqueId()))
                            invited.add(target.getUniqueId());
                        t.setInvited(invited);

                        File save = new File(pl.townsPath, t.getName()+".json");
                        if(save.exists()){
                            save.delete();
                        }
                        String json = pl.serializerManager.serialize(t);
                        pl.serializerManager.saveToFile(save, json);
                        pl.towns.remove(t.getName());
                        pl.towns.put(t.getName(), t);
                        s.sendMessage(pl.config.getString("messages.invite.success").replaceAll("%name%", target.getName()));
                        target.sendMessage(pl.config.getString("messages.invite.received")
                        .replaceAll("%name%", p.getName())
                        .replaceAll("%town%", t.getName()));
                    }else{
                        s.sendMessage(pl.config.getString("messages.invite.notonline"));
                    }
                }else{
                    s.sendMessage(pl.config.getString("messages.notauthorize"));
                }
            }else{
                s.sendMessage(pl.config.getString("messages.unknowtown").replaceAll("%name%", args[1]));
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("join") && args.length == 2){
            Town t = pl.towns.get(args[1]);
            if(t != null){
                if(t.getInvited().stream().anyMatch(u -> {return u.toString().equals(p.getUniqueId().toString());})){
                    List<UUID> invited = t.getInvited();
                    invited.remove(p.getUniqueId());
                    t.setInvited(invited);
                    List<UUID> members = t.getMembers();
                    members.add(p.getUniqueId());

                    File save = new File(pl.townsPath, t.getName()+".json");
                    if(save.exists()){
                        save.delete();
                    }
                    String json = pl.serializerManager.serialize(t);
                    pl.serializerManager.saveToFile(save, json);
                    pl.towns.remove(t.getName());
                    pl.towns.put(t.getName(), t);
                    s.sendMessage(pl.config.getString("messages.invite.joined").replaceAll("%town%", t.getName()));
                }
            }else{
                s.sendMessage(pl.config.getString("messages.unknowtown").replaceAll("%name%", args[1]));
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("tp") && (args.length == 1 || args.length == 2)){
            if(args.length == 1){
                for(Town t : pl.towns.values()){
                    if(t.getOwner().toString().equals(p.getUniqueId().toString())){
                        p.teleport(t.getSpawn());
                        s.sendMessage(pl.config.getString("messages.teleport.success").replaceAll("%town%", t.getName()));
                        return false;
                    }
                }
                s.sendMessage(pl.config.getString("messages.owner.not"));
                return false;
            }else{
                Town t = pl.towns.get(args[1]);
                if(t != null){
                    p.teleport(t.getSpawn());
                    s.sendMessage(pl.config.getString("messages.teleport.success").replaceAll("%town%", t.getName()));
                }else{
                    s.sendMessage(pl.config.getString("messages.unknowtown").replaceAll("%name%", args[1]));
                }
                return false;
            }
        }
        if(args[0].equalsIgnoreCase("settp") && (args.length == 1 || args.length == 2)){
            if(args.length == 1){
                for(Town t : pl.towns.values()){
                    if(t.getOwner().toString().equals(p.getUniqueId().toString())){
                        t.setSpawn(p.getLocation());
                        s.sendMessage(pl.config.getString("messages.teleport.set").replaceAll("%town%", t.getName()));
                        File save = new File(pl.townsPath, t.getName()+".json");
                        if(save.exists()){
                            save.delete();
                        }
                        String json = pl.serializerManager.serialize(t);
                        pl.serializerManager.saveToFile(save, json);
                        pl.towns.remove(t.getName());
                        pl.towns.put(t.getName(), t);
                        return false;
                    }
                }
                s.sendMessage(pl.config.getString("messages.owner.not"));
                return false;
            }else{
                Town t = pl.towns.get(args[1]);
                if(t != null){
                    if(t.getAdmins().stream().anyMatch(uuid -> uuid.toString().equals(p.getUniqueId().toString()))){
                        t.setSpawn(p.getLocation());
                        s.sendMessage(pl.config.getString("messages.teleport.set").replaceAll("%town%", t.getName()));
                        File save = new File(pl.townsPath, t.getName()+".json");
                        if(save.exists()){
                            save.delete();
                        }
                        String json = pl.serializerManager.serialize(t);
                        pl.serializerManager.saveToFile(save, json);
                        pl.towns.remove(t.getName());
                        pl.towns.put(t.getName(), t);
                    }else{
                        s.sendMessage(pl.config.getString("messages.notauthorize"));
                    }
                }else{
                    s.sendMessage(pl.config.getString("messages.unknowtown").replaceAll("%name%", args[1]));
                }
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("promote") && (args.length == 2)){
            if(pl.towns.values().stream().anyMatch(t -> {return t.getOwner().toString().equals(p.getUniqueId().toString());})){
                Town t = pl.towns.values().stream().filter(to -> {return to.getOwner().toString().equals(p.getUniqueId().toString());}).findFirst().get();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if(t.getMembers().stream().anyMatch(a -> {return a.toString().equals(target.getUniqueId().toString());})){
                    List<UUID> admin = t.getAdmins();
                    admin.add(target.getUniqueId());

                    File save = new File(pl.townsPath, t.getName()+".json");
                    if(save.exists()){
                        save.delete();
                    }
                    String json = pl.serializerManager.serialize(t);
                    pl.serializerManager.saveToFile(save, json);
                    pl.towns.remove(t.getName());
                    pl.towns.put(t.getName(), t);

                    s.sendMessage(pl.config.getString("messages.promote.success").replaceAll("%name%", target.getName()));
                }else{
                    s.sendMessage(pl.config.getString("messages.promote.notmember"));
                }
            }else{
                s.sendMessage(pl.config.getString("messages.owner.not"));
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("demote") && (args.length == 2)){
            if(pl.towns.values().stream().anyMatch(t -> {return t.getOwner().toString().equals(p.getUniqueId().toString());})){
                Town t = pl.towns.values().stream().filter(to -> {return to.getOwner().toString().equals(p.getUniqueId().toString());}).findFirst().get();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if(t.getAdmins().stream().anyMatch(a -> {return a.toString().equals(target.getUniqueId().toString());})){
                    List<UUID> admin = t.getAdmins();
                    admin.remove(target.getUniqueId());

                    File save = new File(pl.townsPath, t.getName()+".json");
                    if(save.exists()){
                        save.delete();
                    }
                    String json = pl.serializerManager.serialize(t);
                    pl.serializerManager.saveToFile(save, json);
                    pl.towns.remove(t.getName());
                    pl.towns.put(t.getName(), t);

                    s.sendMessage(pl.config.getString("messages.promote.demote").replaceAll("%name%", target.getName()));
                }else{
                    s.sendMessage(pl.config.getString("messages.promote.notmember"));
                }
            }else{
                s.sendMessage(pl.config.getString("messages.owner.not"));
            }
            return false;
        }
        if(args[0].equalsIgnoreCase("kick") && (args.length == 2)){
            if(pl.towns.values().stream().anyMatch(t -> {return t.getOwner().toString().equals(p.getUniqueId().toString());})){
                Town t = pl.towns.values().stream().filter(to -> {return to.getOwner().toString().equals(p.getUniqueId().toString());}).findFirst().get();
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if(t.getMembers().stream().anyMatch(a -> {return a.toString().equals(target.getUniqueId().toString());})){
                    List<UUID> members = t.getMembers();
                    members.remove(target.getUniqueId());
                    t.setMembers(members);
                    if(t.getAdmins().stream().anyMatch(b -> {return b.toString().equals(target.getUniqueId().toString());})){
                        List<UUID> admin = t.getAdmins();
                        admin.remove(target.getUniqueId());
                        t.setAdmins(admin);
                    }
                    File save = new File(pl.townsPath, t.getName()+".json");
                    if(save.exists()){
                        save.delete();
                    }
                    String json = pl.serializerManager.serialize(t);
                    pl.serializerManager.saveToFile(save, json);
                    pl.towns.remove(t.getName());
                    pl.towns.put(t.getName(), t);

                    s.sendMessage(pl.config.getString("messages.kick").replaceAll("%name%", target.getName()));
                }else{
                    s.sendMessage(pl.config.getString("messages.promote.notmember"));
                }
            }else{
                s.sendMessage(pl.config.getString("messages.owner.not"));
            }
            return false;
        }
        sendHelpMessage(s);
        return false;
    }

    public void sendHelpMessage(CommandSender s){
        for(String str : pl.config.getStringList("messages.help")){
            s.sendMessage("§e"+str);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            List<String> ostr = new ArrayList<>();
            List<String> str;
            ostr.add("create");
            ostr.add("claim");
            ostr.add("unclaim");
            ostr.add("owner");
            ostr.add("invite");
            ostr.add("join");
            ostr.add("tp");
            ostr.add("settp");
            ostr.add("promote");
            ostr.add("demote");
            ostr.add("kick");
            if(!strings[0].equals("")){
                str = new ArrayList<>();
                for(String s5 : ostr){
                    if(s5.startsWith(strings[0])) str.add(s5);
                }
            }else{
                str = ostr;
            }
            return str;
        }
        if(strings.length == 2 && (strings[0].equalsIgnoreCase("claim") || strings[0].equalsIgnoreCase("settp") || strings[0].equalsIgnoreCase("invite"))){
            if(commandSender instanceof Player){
                Player p = (Player)commandSender;
                List<String> str = new ArrayList<>();
                for (Town t: pl.towns.values()){
                    if(t.getAdmins().stream().anyMatch(a -> {return a.toString().equals(p.getUniqueId().toString());})){
                        str.add(t.getName());
                    }
                }
                return str;
            }
        }
        if(strings.length == 2 && (strings[0].equalsIgnoreCase("join") || strings[0].equalsIgnoreCase("tp"))){
            List<String> str = new ArrayList<>();
            for (Map.Entry<String, Town> name : SimpleTowns.getInstance().towns.entrySet()){
                str.add(name.getKey());
            }
            return str;
        }
        return null;
    }
}