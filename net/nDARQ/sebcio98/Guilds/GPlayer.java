package net.nDARQ.sebcio98.Guilds;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GPlayer extends OfflineGPlayer implements Cloneable
{
	private final Player p;
	private volatile boolean canInviteRandom;
	private volatile String pendingInvitation;
	private volatile int pendingInvitationID;
	
	public GPlayer (final Player p)
	{
		super(p.getName());
		
		this.p = (Player)p;
		canInviteRandom = true;
		pendingInvitation = null;
	}
	public Object clone() throws CloneNotSupportedException {return super.clone();}
	public void saveToConfig()
	{
		Main.guilds.set(p.getName() + ".guild", guild);
		Main.guilds.set(p.getName(), guildMembers);
	}
	
	public String getName() {return p.getName();}
	public Player getPlayer() {return p;}
	
	public boolean hasMember(final String nick) {return guildMembers.contains(nick);}
	public List<String> getGuildMembers() {return guildMembers;}
	public String getGuild() {return guild;}
	public void setGuild(String guild) {this.guild = guild;}
	public void leaveGuild() //TODO offline
	{
		if (guild == null) {sendMessage("You aren't a member of any guild!", 'c'); return;}
		if (this.nick == guild) {sendMessage("You can't just leave you guild! You have to disband it!", 'c');}
		
		sendMessage("You have left " + guild + "'s guild.", 'a');
		PlayerHandler.getPlayer(guild).leaveGuild(p.getName());
		guild = null;
	}
	public void leaveGuild(final String nick)
	{
		sendMessage(nick + " has left your guild.", 'c');
		
		guildMembers.remove(nick);
		if (guildMembers.size() < 1) {guild = null;}
	}
	public void disbandGuild()
	{
		 
	}
	
	public boolean canInviteRandom() {return canInviteRandom;}
	public void setCanInviteRandom(final boolean canInviteRandom) {this.canInviteRandom = canInviteRandom;}
	public void recieveInvitation(final String from)
	{
		pendingInvitation = from;
		pendingInvitationID = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				cancelInvitation();
				sendMessage("The invitation from " + from + " timed out.", 'c');
			}
		}.runTaskLater(Main.getPlugin, 30*20L).getTaskId();
	}
	public void cancelInvitation() {pendingInvitation = null; pendingInvitationID = -1;}
	public String getPendingInvitation() {return pendingInvitation;}
	public int getPendingInvitationID() {return pendingInvitationID;}
	
	public void invite(final String nick)
	{
		if (nick.equals(p.getName())) {sendMessage("You can't invite yourself!", '4'); return;}
		if (guild != null && guild != p.getName()) {sendMessage("You are already in someone's guild!", '4'); return;}
		if (Bukkit.getPlayer(nick) == null) {sendMessage("A player with this name isn't online!", '4'); return;}
		if (guildMembers.contains(nick)) {sendMessage("This players is already in your guild.", 'c'); return;}
		if (PlayerHandler.getPlayer(nick).getGuild() != null) {sendMessage("This player is already in a guild.", '4'); return;}
		if (PlayerHandler.getPlayer(nick).getPendingInvitation() != null) {sendMessage("This player has already a pending invitation from another player.", 'c'); return;}
		if (pendingInvitation != null) {((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + ChatColor.DARK_RED + "You have to \",\"extra\":[{\"text\":\"" + ChatColor.GREEN + "accept\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/guild accept\"}},{\"text\":\"" + ChatColor.DARK_RED + " or \"},{\"text\":\"" + ChatColor.DARK_RED + "decline\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/guild decline\"}},{\"text\":\"" + ChatColor.DARK_RED + " an invitation from " + pendingInvitation + " first!\"}]}"))); return;}
		
		if (guildMembers.size()<6 || (p.hasPermission("VIP.VIP") && guildMembers.size()<16)) {inviteValid(nick); return;}
		else {sendMessage("Your guild is full!", '4'); return;}
	}
	public void inviteRandom()
	{
		if ((guildMembers.size()>=6 && !p.hasPermission("VIP.VIP")) || guildMembers.size()>=16) {sendMessage("Your guild is full!", '4'); return;}
			
		HashMap<String,GPlayer> players =  new HashMap<String,GPlayer>();
		
		for (Entry<String,GPlayer> entry : PlayerHandler.getPlayersHashMap().entrySet())
			if ( entry.getValue().getGuild() != null || entry.getValue().getPendingInvitation() != null || entry.getKey().equals(p.getName()))
				players.put(entry.getKey(), entry.getValue());
		
		System.out.println("########");
		for (String nick : players.keySet())
			System.out.println(nick);
		System.out.println("########");
		//TODO
		
		final String randomNick = (String) players.keySet().toArray()[(int)Math.floor(Math.random()*players.size())];
		inviteValid(randomNick);
	}
	private void inviteValid(final String nick)
	{
		final GPlayer invited = PlayerHandler.getPlayer(nick);
		
		invited.recieveInvitation(p.getName());
		
		invited.sendMessage("You have been invited by " + p.getName() + " to his/her guild.", 'e');
	    ((CraftPlayer) invited.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + ChatColor.YELLOW + "Accept the invitation by using \",\"extra\":[{\"text\":\"" + ChatColor.GREEN + "/guild accept\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/guild accept\"}}]}")));
	    ((CraftPlayer) invited.getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + ChatColor.YELLOW + " or decline it with \",\"extra\":[{\"text\":\"" + ChatColor.RED + "/guild decline\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/guild decline\"}}]}")));
		invited.sendMessage("Notice: You can also accept/decline by clicking on the commands in the chat.", 'e');
		
		sendMessage("You have invited " + nick + " to your guild.", 'a');
	}
	
	public void kick()
	{
		guild = null;
		sendMessage("You've got kicked from the guild.", 'c');
	}
	public void kick(final String nick) //TODO Offline
	{
		if (this.nick != guild) {sendMessage("Only guild leader can do that.", '4'); return;}
		if (this.nick == nick) {sendMessage("You can't kick yourself!", 'c'); return;}
		if (!guildMembers.contains(nick)) {sendMessage("This player isn't a member of your guild!", '4'); return;}
		
		PlayerHandler.getPlayer(nick).kick();
		leaveGuild(nick);
	}
	
	public void sendMessage(final String msg, final char color) {p.sendMessage(Main.prefix + ChatColor.getByChar(color) + msg);}
	public void showHelp()
	{
		sendMessage("No help yet :/", 'e');
		//TODO
	}
	public void showHelp(final String cmd)
	{
		sendMessage("No help yet :/", 'e');
		//TODO
	}
}
