package us.mcmagic.dreamwars.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.GameState;

import java.util.List;
import java.util.UUID;

public class ScoreboardUtil {
    private ScoreboardManager sbm = Bukkit.getScoreboardManager();

    public void join(Player player) {
        Scoreboard sb = getScoreboard();
        Team players = sb.getTeam("players");
        Team spec = sb.getTeam("spec");
        switch (GameState.getState()) {
            case PRE_GAME: {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    players.addEntry(tp.getName());
                }
                break;
            }
            case IN_GAME: {
                List<UUID> spectators = DreamWars.gameUtil.getSpectators();
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    if (spectators.contains(tp.getUniqueId())) {
                        spec.addEntry(tp.getName());
                    } else {
                        players.addEntry(tp.getName());
                    }
                }
                break;
            }
        }
        player.setScoreboard(sb);
    }

    private Scoreboard getScoreboard() {
        Scoreboard sb = sbm.getNewScoreboard();
        Team players = sb.registerNewTeam("players");
        Team spec = sb.registerNewTeam("spec");
        players.setPrefix(ChatColor.GREEN.toString());
        players.setAllowFriendlyFire(true);
        players.setCanSeeFriendlyInvisibles(false);
        spec.setPrefix(ChatColor.GRAY.toString());
        spec.setAllowFriendlyFire(false);
        spec.setCanSeeFriendlyInvisibles(true);
        return sb;
    }
}
