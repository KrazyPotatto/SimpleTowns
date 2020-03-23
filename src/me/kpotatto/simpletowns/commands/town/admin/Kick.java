package me.kpotatto.simpletowns.commands.town.admin;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Kick {

    @Deprecated
    public static void execute(CommandSender s, String[] args){
        SimpleTowns pl = SimpleTowns.getInstance();
        if(!pl.towns.containsKey(args[2])){
            s.sendMessage(pl.config.getString("messages.unknowtown"));
            return;
        }
        Town t = pl.towns.get(args[2]);
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
    }

}
