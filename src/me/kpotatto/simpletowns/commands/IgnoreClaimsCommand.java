package me.kpotatto.simpletowns.commands;

import me.kpotatto.simpletowns.SimpleTowns;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreClaimsCommand implements CommandExecutor {

    private SimpleTowns pl;

    public IgnoreClaimsCommand(SimpleTowns simpleTowns) {
        this.pl = simpleTowns;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String cmds, String[] args) {
        if(!(s instanceof Player)){
            s.sendMessage("§cYou need to be a player to execute this command!");
            return false;
        }
        Player p = (Player) s;
        if(pl.ignoreClaims.contains(p)){
            pl.ignoreClaims.remove(p);
            p.sendMessage(pl.config.getString("messages.ignoreclaims.off"));
            return false;
        }
        if(!p.hasPermission("simpletowns.admin")){
            s.sendMessage(pl.config.getString("messages.notauthorize"));
            return false;
        }
        pl.ignoreClaims.add(p);
        p.sendMessage(pl.config.getString("messages.ignoreclaims.on"));
        return false;
    }
}
