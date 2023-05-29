package showheart.by.milkatiger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KillStatManager {
    Map<UUID, KillStat> DailyKills = new HashMap<>();
    Map<UUID, KillStat> KillStreak = new HashMap<>();
    KillStatList Daily = new KillStatList();
    KillStatList Streak = new KillStatList();
    String DailyString = "Daily", StreakString = "Streak";
    public KillStatManager(Main plugin, ConfigurationSection cs) {
        ConfigurationSection DailySection = Optional.ofNullable(cs.getConfigurationSection(DailyString)).orElse(cs.createSection(DailyString)),
                StreakSection = Optional.ofNullable(cs.getConfigurationSection(StreakString)).orElse(cs.createSection(StreakString));
        for(String keys:DailySection.getKeys(false)) {
            KillStat ks = new KillStat(UUID.fromString(keys), DailySection.getInt(keys));
            Daily.addLast(ks);
            DailyKills.put(ks.id(), ks);
            while (ks._prev != null && ks._prev.value() < ks.value()) {
                ks._prev.swapNext();
                if (ks._next._next == null) {
                    Daily.Last = ks._next;
                }
                if (ks._prev == null) {
                    Daily.First = ks;
                }
            }
        }
        for(String keys:StreakSection.getKeys(false)) {
            KillStat ks = new KillStat(UUID.fromString(keys), StreakSection.getInt(keys));
            Streak.addLast(ks);
            KillStreak.put(ks.id(), ks);
            while (ks._prev != null && ks._prev.value() < ks.value()) {
                ks._prev.swapNext();
                if (ks._next._next == null) {
                    Streak.Last = ks._next;
                }
                if (ks._prev == null) {
                    Streak.First = ks;
                }
            }
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long howMany = (c.getTimeInMillis()-System.currentTimeMillis())/50;
        new BukkitRunnable(){
            @Override
            public void run() {
                DailyKills.clear();
                KillStreak.clear();
                Daily.clear();
                Streak.clear();
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.YELLOW +""+ ChatColor.BOLD + "The Daily Stats LeaderBoard has reset!");
                Bukkit.broadcastMessage("");
            }
        }.runTaskTimer(plugin, howMany, 1728000);
    }
    public void saveSB(ConfigurationSection cs) {
        ConfigurationSection DailySection = Optional.ofNullable(cs.getConfigurationSection(DailyString)).orElse(cs.createSection(DailyString)),
                StreakSection = Optional.ofNullable(cs.getConfigurationSection(StreakString)).orElse(cs.createSection(StreakString));
        for(Map.Entry<UUID, KillStat> entry: DailyKills.entrySet()){
            DailySection.set(entry.getKey().toString(), entry.getValue().value());
        }
        for(Map.Entry<UUID, KillStat> entry: KillStreak.entrySet()){
            StreakSection.set(entry.getKey().toString(), entry.getValue().value());
        }
    }
    public void addDailyKills(UUID id) {
        KillStat ks = DailyKills.getOrDefault(id, null);
        if (ks == null) {
            ks = new KillStat(id);
            Daily.addLast(ks);
            DailyKills.put(id, ks);
            ks.addOne();
        } else {
            ks.addOne();
            while (ks._prev != null && ks._prev.value() < ks.value()) {
                ks._prev.swapNext();
                if (ks._next._next == null) {
                    Daily.Last = ks._next;
                }
                if (ks._prev == null) {
                    Daily.First = ks;
                }
            }
        }
    }

    public void addKillStreak(UUID id) {
        KillStat ks = KillStreak.getOrDefault(id, null);
        if (ks == null) {
            ks = new KillStat(id);
            Streak.addLast(ks);
            KillStreak.put(id, ks);
            ks.addOne();
        } else {
            ks.addOne();
            while (ks._prev != null && ks._prev.value() < ks.value()) {
                ks._prev.swapNext();
                if (ks._next._next == null) {
                    Streak.Last = ks._next;
                }
                if (ks._prev == null) {
                    Streak.First = ks;
                }
            }
        }
    }

    public void noKillStreak(UUID id) {
        KillStat ks = KillStreak.remove(id);
        if (ks == null) {
            return;
        }
        if (ks._next == null) {
            Streak.Last = ks._prev;
        } else {
            ks._next._prev = ks._prev;
        }
        if (ks._prev == null) {
            Streak.First = ks._next;
        } else {
            ks._prev._next = ks._next;
        }
    }
}
