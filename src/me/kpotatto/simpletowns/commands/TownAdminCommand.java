package me.kpotatto.simpletowns.commands;

import me.kpotatto.simpletowns.SimpleTowns;
import me.kpotatto.simpletowns.commands.town.admin.Kick;
import me.kpotatto.simpletowns.commands.town.admin.Unclaim;
import me.kpotatto.simpletowns.towns.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TownAdminCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String cmds, String[] args) {
        if(args[0].equalsIgnoreCase("kick") && (args.length == 3) && s.hasPermission("simpletowns.admin")){
            Kick.execute(s,args);
        }
        if(args[0].equalsIgnoreCase("unclaim") && s.hasPermission("simpletowns.admin") && (s instanceof Player)){
            Unclaim.execute((Player)s);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1){
            List<String> ostr = new ArrayList<>();
            List<String> str;
            ostr.add("unclaim");
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
        if(strings.length == 3 && strings[0].equalsIgnoreCase("kick")){
            if(commandSender instanceof Player){
                Player p = (Player)commandSender;
                List<String> str = new ArrayList<>();
                for (Town t: SimpleTowns.getInstance().towns.values()){
                    if(t.getAdmins().stream().anyMatch(a -> {return a.toString().equals(p.getUniqueId().toString());})){
                        str.add(t.getName());
                    }
                }
                return str;
            }
        }
        return null;
    }
}
