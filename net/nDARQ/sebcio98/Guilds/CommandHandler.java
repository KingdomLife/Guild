package net.nDARQ.sebcio98.Guilds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(Main.prefix + "§cThis command can be used only by a player!");
			return true;
		}
		
		final GPlayer gp = PlayerHandler.getPlayer(sender.getName());
		
		if (args.length < 1)
		{
			gp.sendMessage("Too few arguments", 'c');
			gp.showHelp();
			return true;
		}
		if (Main.permissionsEnabled && !gp.getPlayer().hasPermission("guilds.default"))
		{
			gp.sendMessage("You do not have permission to use this command!", '4');
			return true;
		}
		
		if (args[0].equalsIgnoreCase("invite"))
		{
			if (args.length < 2) {gp.showHelp("invite"); return true;}
			else if (args[1].equalsIgnoreCase("random"))
			{
				if (Main.permissionsEnabled && !gp.getPlayer().hasPermission("guilds.inviterandom")) {gp.sendMessage("You don't have permission to invite a random player.", '4'); return true;}
				if (!gp.canInviteRandom()) {gp.sendMessage("You are still on cooldown for this command.", 'c'); return true;}
				
				gp.inviteRandom();
			}
			else {gp.invite(args[1]);}
		}
		else if (args[0].equalsIgnoreCase("accept"))
		{
			if (gp.getPendingInvitation() == null) {gp.sendMessage("You don't have any pending invitations.", 'c'); return true;}
			if (PlayerHandler.getPlayer(gp.getPendingInvitation()) == null) {gp.sendMessage("The player who invited you has already left the game.", 'c'); return true;}
			if (PlayerHandler.getPlayer(gp.getPendingInvitation()).getGuild() == gp.getPendingInvitation() || PlayerHandler.getPlayer(gp.getPendingInvitation()).getGuild() == null)
			{
				if (gp.getGuild() == null)
				{
					final GPlayer guildMaster = PlayerHandler.getPlayer(gp.getPendingInvitation());
					
					gp.setGuild(guildMaster.getName());
					guildMaster.setGuild(guildMaster.getName());
					
					gp.sendMessage("You have successfully joined " + guildMaster.getName() + "'s guild.", 'a');
					guildMaster.sendMessage(sender.getName() + " has joined your guild.", 'a');
				}
				else {gp.sendMessage("You can't accept invitations while being in a guild!", '4');}
			}
			else {gp.sendMessage("That player has already joined another guild. You have to be invited by a guild leader to accept.", 'c');}
			
			Bukkit.getScheduler().cancelTask(gp.getPendingInvitationID());
			gp.cancelInvitation();
			
		}
		else if (args[0].equalsIgnoreCase("decline"))
		{
			if (gp.getPendingInvitation() == null) {gp.sendMessage("You don't have any pending invitations.", 'c'); return true;}
			if (PlayerHandler.getPlayer(gp.getPendingInvitation()) == null) {gp.sendMessage("The player who invited you has already left the game.", 'c');}
			else
			{
				gp.sendMessage("You have declined " + gp.getPendingInvitation() + "'s invitation.", 'c');
				PlayerHandler.getPlayer(gp.getPendingInvitation()).sendMessage(sender.getName() + " has declined your invitation", 'c');
			}
			
			Bukkit.getScheduler().cancelTask(gp.getPendingInvitationID());
			gp.cancelInvitation();
		}
		else if (args[0].equalsIgnoreCase("leave"))
		{
			gp.leaveGuild();
		}
		else if (args[0].equalsIgnoreCase("disband"))
		{
			gp.disbandGuild();
		}
		else if (args[0].equalsIgnoreCase("kick"))
		{
			if (args.length < 2) {gp.sendMessage("Too few arguments!", '4'); gp.showHelp("kick"); return true;}
			
			gp.kick(args[1]);
		}
		else if (args[0].equalsIgnoreCase("info"))
		{
			if (gp.getGuild() == null) {gp.sendMessage("You aren't a member of a guild.", 'c'); return true;}
			
			String online = "§a", offline = "§c";
			
			for (String member : PlayerHandler.getOfflinePlayer(gp.getGuild()).getGuildMembers())
				if (PlayerHandler.getPlayer(member) == null) offline += "§e, §c" + member;
				else online += "§e, §a" + member;
			
			sender.sendMessage("§3=====================================================");
			sender.sendMessage("§eGuild leader: §6§l" + gp.getGuild());
			sender.sendMessage("§eMembers online: " + (online.length() == 2 ? "none." : online.substring(0, online.length()-6)));
			sender.sendMessage("§eMembers offline: " + (offline.length() == 2 ? "none." : offline.substring(0, offline.length()-7)));
			sender.sendMessage("§3=====================================================");
		}
		else {gp.showHelp();}
		
		return true;
	}
}
