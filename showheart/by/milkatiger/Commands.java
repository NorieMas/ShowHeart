package showheart.by.milkatiger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    Main _plugin;

    public Commands(Main Instance) {
        _plugin = Instance;
    }

    public static double netherPrice = 1d;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if(args.length == 0 && sender.hasPermission("showheart.if")) {
                int a;
                double b;

            }else if(sender.isOp()) {
                _plugin.pm.givePots((Player)sender, args[0].toLowerCase());
            }else {
                sender.sendMessage(ChatColor.RED + "You Don't Have Perms Sorry");
            }

        }
        return true;
    }
}
