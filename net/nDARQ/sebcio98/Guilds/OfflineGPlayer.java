package net.nDARQ.sebcio98.Guilds;

import java.util.ArrayList;
import java.util.List;

public class OfflineGPlayer implements Cloneable
{
	protected final String nick;
	protected String guild;
	protected List<String> guildMembers;
	
	@SuppressWarnings("unchecked")
	public OfflineGPlayer (final String nick)
	{
		this.nick = nick;
		guild = Main.guilds.getString(nick + ".guild");
		guildMembers = Main.guilds.getList(nick) == null ? new ArrayList<String>() : (List<String>) Main.guilds.getList(nick);
	}
	public Object clone() throws CloneNotSupportedException {return super.clone();}
	public void saveToConfig()
	{
		Main.guilds.set(nick + ".guild", guild);
		Main.guilds.set(nick, guildMembers);
	}
	
	public String getName() {return nick;}
	
	public List<String> getGuildMembers() {return guildMembers;}
	public String getGuild() {return guild;}
	public void setGuild(String guild) {this.guild = guild;}
	
}
