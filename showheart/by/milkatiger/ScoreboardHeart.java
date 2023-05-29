// 
// Decompiled by Procyon v0.5.36
// 

package showheart.by.milkatiger;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHeart
{
    ScoreboardManager sm;
    Scoreboard board;
    Objective obj;
    
    public ScoreboardHeart() {
        this.sm = Bukkit.getScoreboardManager();
        this.board = this.sm.getNewScoreboard();
        (this.obj = this.board.registerNewObjective("aaa", "bbb")).setDisplayName("§6\u2764");
        this.obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }
    
    public void updateScoreboard(final Player player, final int heart) {
        this.obj.getScore(player).setScore(heart);
        player.setScoreboard(this.board);
    }
}
