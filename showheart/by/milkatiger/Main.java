
package showheart.by.milkatiger;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
    ScoreboardHeart sh;
    NetherBalance _nb;
    Economy econ;
    PotionManager pm;
    File configFile;
    KillStatManager ksm;
    String sbSectionString = "scoreboard", nbSectionString = "netherbalance";
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("if").setExecutor(new Commands(this));
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.sh.updateScoreboard(player, this.getHealth(player));
        }
        saveResource("data.yml",false);
        configFile = Paths.get(getDataFolder().toString(), "data.yml").toFile();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ConfigurationSection sbSection = Optional.ofNullable(config.getConfigurationSection(sbSectionString)).orElse(config.createSection(sbSectionString))
                , nbSection = Optional.ofNullable(config.getConfigurationSection(nbSectionString)).orElse(config.createSection(nbSectionString));

        _nb = new NetherBalance(nbSection);


        sh = new ScoreboardHeart();
        pm = new PotionManager(this);
        ksm = new KillStatManager(this, sbSection);


        new PlaceHolder(this).register();
        new NetherBalancePHAPI(this).register();
    }

    public void onDisable() {
        YamlConfiguration config = new YamlConfiguration();
        ConfigurationSection sbSection = Optional.ofNullable(config.getConfigurationSection(sbSectionString)).orElse(config.createSection(sbSectionString))
                , nbSection = Optional.ofNullable(config.getConfigurationSection(nbSectionString)).orElse(config.createSection(nbSectionString));
        ksm.saveSB(sbSection);
        _nb.save(nbSection);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.broadcastMessage("§6ShowHeart");
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public Economy getEcon(){
        return econ;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        this.sh.updateScoreboard(event.getPlayer(), this.getHealth(event.getPlayer()));
    }
    @EventHandler
    public void onQuit(final PlayerQuitEvent event){
        int a;
        if(!isInProtectedRegion(event.getPlayer()) && (a = _nb.removeNetherStar(event.getPlayer().getUniqueId())) > 0){
            Item i = event.getPlayer().getLocation().getWorld().dropItem(event.getPlayer().getLocation(), new ItemStack(Material.NETHER_STAR, a));
            i.setCustomNameVisible(true);
            i.setCustomName(ChatColor.GREEN + "Sell me for $1 each in Spawn");
        }
        event.getPlayer().sendMessage(ChatColor.RED + "Because You Quit in PVP Zone, You Dropped all of your netherstars");
    }
    @EventHandler
    public void onKill(final PlayerDeathEvent event){
        if(event.getEntity().hasMetadata("NPC")){
            return;
        }
        ksm.noKillStreak(event.getEntity().getUniqueId());

        if(event.getEntity().getKiller() != null){
            UUID uuid = event.getEntity().getKiller().getUniqueId();
            if (event.getEntity().getUniqueId().equals(uuid)){
                return;
            }
            _nb.playerKill(uuid, event.getEntity().getUniqueId());
            ksm.addDailyKills(uuid);
            ksm.addKillStreak(uuid);
        }
        else {
            _nb.removeNetherStar(event.getEntity().getUniqueId());
            Item i = event.getEntity().getLocation().getWorld().dropItem(event.getEntity().getLocation(), new ItemStack(Material.NETHER_STAR, 5));
            i.setCustomNameVisible(true);
            i.setCustomName(ChatColor.GREEN + "Sell me for $1 each in Spawn");
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        new BukkitRunnable(){
            @Override
            public void run(){
                sh.updateScoreboard(event.getPlayer(), (int)event.getPlayer().getMaxHealth());
            }
        }.runTaskLater(this, 0L);
    }
    @EventHandler
    public void onRegain(final EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            this.sh.updateScoreboard(player, (int)Math.min(player.getHealth() + event.getAmount(), player.getMaxHealth()));
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && !event.isCancelled()) {
            final Player player = (Player)event.getEntity();
            this.sh.updateScoreboard(player, (int)Math.max(player.getHealth() - event.getFinalDamage(), 0));
        }
    }
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && event.getItem() != null ) {
            if(event.getItem().getType() == Material.FIREBALL) {
                ItemStack is = event.getItem();
                is.setAmount(event.getItem().getAmount()-1);
                event.getPlayer().setItemInHand(is);
                Location eye = event.getPlayer().getEyeLocation();
                Location loc = eye.add(eye.getDirection().multiply(1.2));
                Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
                fireball.setVelocity(loc.getDirection().normalize().multiply(1));
                fireball.setShooter(event.getPlayer());
                fireball.setFireTicks(0);
                fireball.setIsIncendiary(false);
                event.setCancelled(true);
            }else if (event.getItem().getType() == Material.DIAMOND_HOE && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Firework fw = (Firework) event.getClickedBlock().getWorld().spawnEntity(event.getClickedBlock().getLocation().add(0,1,0), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.addEffect(FireworkEffect.builder().withColor(Color.BLUE).with(FireworkEffect.Type.STAR).build());
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerPickUp(PlayerPickupItemEvent event){
        if(event.getItem().getItemStack().getType() == Material.NETHER_STAR){
            _nb.addNetherStar(event.getPlayer().getUniqueId(), event.getItem().getItemStack().getAmount());
            event.getItem().remove();
            event.setCancelled(true);
        }
    }


    // ======================= <Helper Methods> ===================
    public int getHealth(final Player player) {
        return (int)StrictMath.ceil(this.damageable(player).getHealth());
    }

    public Damageable damageable(final Player player) {
        return (Damageable)player;
    }

    public boolean isInProtectedRegion(Player player){
        return !WGBukkit.getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation()).allows(DefaultFlag.PVP);
    }
}
