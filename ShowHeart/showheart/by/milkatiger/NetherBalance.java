package showheart.by.milkatiger;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class NetherBalance {
    public static HashMap<UUID, Integer> balanceMap = new HashMap<>();
    public NetherBalance(ConfigurationSection cs){
        for(String keys:cs.getKeys(false)) {
            balanceMap.put(UUID.fromString(keys), cs.getInt(keys));
        }
    }
    public void save(ConfigurationSection cs){
        for (Map.Entry<UUID, Integer> entry: balanceMap.entrySet()){
            cs.set(entry.getKey().toString(), entry.getValue());
        }
    }
    public void playerKill(UUID Killer, UUID victim){
        balanceMap.put(Killer, balanceMap.getOrDefault(Killer, 0)+Optional.ofNullable(balanceMap.remove(victim)).orElse(0)+5);
    }
    public void addNetherStar(UUID uuid, int amount){
        balanceMap.put(uuid, balanceMap.getOrDefault(uuid, 0) + amount);
    }
    public int getNetherStar(UUID uuid){
        return balanceMap.getOrDefault(uuid, 0);
    }
    public int removeNetherStar(UUID uuid){
        return Optional.ofNullable(balanceMap.remove(uuid)).orElse(0);
    }
}
