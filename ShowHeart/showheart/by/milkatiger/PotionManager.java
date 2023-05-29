package showheart.by.milkatiger;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PotionManager {
    HashMap<String, AtomicInteger> potMap = new HashMap<>();
    int maxAmount = 10;
    ItemStack SplashHeal;
    {
        Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setLevel(2);
        potion.setSplash(true);
        SplashHeal = potion.toItemStack(1);
    }
    public PotionManager(Main instance){
        new BukkitRunnable() {
            @Override
            public void run() {
                for(AtomicInteger integer: potMap.values()) {
                    integer.updateAndGet((a)-> (int)Math.round(Math.max(a*0.9-1, 0)));
                }
            }
        }.runTaskTimer(instance, 600L,600L);
    }
    public void givePots(Player player, String string) {
        int index;
        if((index = player.getInventory().firstEmpty()) == -1){
            player.sendMessage(ChatColor.RED + "Inventory Is Full, Cannot Refill Potion");
            return;
        }
        AtomicInteger currentPot = potMap.get(string);
        if (currentPot == null){
            potMap.put(string, currentPot = new AtomicInteger(1));
        }else if (maxAmount <= currentPot.intValue()) {
            player.sendMessage(ChatColor.RED + "This Refill Crystal Is Dry, Try Finding Another Crystal");
            return;
        }
        currentPot.addAndGet(1);
        player.getInventory().setItem(index, SplashHeal);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
    }
}
