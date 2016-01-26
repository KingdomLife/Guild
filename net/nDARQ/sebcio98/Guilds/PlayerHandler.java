package net.nDARQ.sebcio98.Guilds;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener
{
	public static HashMap<String,GPlayer> players = new HashMap<String,GPlayer>();

	public static GPlayer getPlayer(final String nick) {return players.get(nick);}
	public static OfflineGPlayer getOfflinePlayer(final String nick) {return getPlayer(nick) == null ? new OfflineGPlayer(nick) : getPlayer(nick);}
	public static HashMap<String,GPlayer> getPlayersHashMap() {return players;}
	public static Collection<GPlayer> getAllPlayers() {return players.values();}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {players.put(e.getPlayer().getName(), new GPlayer(e.getPlayer()));}
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		Bukkit.getScheduler().cancelTask(players.get(e.getPlayer().getName()).getPendingInvitationID());
		
		players.get(e.getPlayer().getName()).saveToConfig();
		players.remove(e.getPlayer().getName());
	}
}
