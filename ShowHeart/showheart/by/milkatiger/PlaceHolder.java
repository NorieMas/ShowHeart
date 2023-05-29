package showheart.by.milkatiger;


import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlaceHolder extends PlaceholderExpansion {
    private Main plugin;


    public PlaceHolder(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "KSLB";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        String key;
        if (identifier.startsWith("Daily")) {
            key = identifier.substring(5);
            try {
                KillStat ks = plugin.ksm.Daily.get(Integer.parseInt(key));
                return ks == null? ChatColor.GRAY+"----": ChatColor.GRAY + Bukkit.getOfflinePlayer(ks.id()).getName() + ChatColor.DARK_GRAY +" - " + ChatColor.WHITE + ks.value();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }else if (identifier.startsWith("Streak")) {
            key = identifier.substring(6);
            try {
                KillStat ks = plugin.ksm.Streak.get(Integer.parseInt(key));
                return ks == null? ChatColor.GRAY+"----": ChatColor.GRAY + Bukkit.getOfflinePlayer(ks.id()).getName() + ChatColor.DARK_GRAY +" - " + ChatColor.WHITE + ks.value();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
