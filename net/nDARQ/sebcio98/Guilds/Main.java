package net.nDARQ.sebcio98.Guilds;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public static final String prefix = "§3[§aGuilds§3] ";
	public static Main getPlugin;
	public static boolean permissionsEnabled;
	static File guildsFile;
	public static FileConfiguration conf;
	public static YamlConfiguration guilds;
	
	@Override
	public void onEnable()
	{
		getPlugin = this;
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		conf = getConfig();
		guilds = new YamlConfiguration();
		
		try
		{
			if(!getDataFolder().exists())
				getDataFolder().mkdir();
			guildsFile = new File(getDataFolder(), "guilds.yml");
			if (!guildsFile.exists())
				guildsFile.createNewFile();
		}
		catch (IOException exc)
			{exc.printStackTrace();}
		
		YamlConfiguration.loadConfiguration(guildsFile);
		
		permissionsEnabled = conf.getBoolean("permissionsEnabled");
		
		getCommand("guild").setExecutor(new CommandHandler());
		getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
		getServer().getPluginManager().registerEvents(new PeaceKeeper(), this);
		
		
	}
	
	@Override
	public void onDisable()
	{
		//saveConfig();
		try
			{guilds.save(guildsFile);}
		catch (IOException exc)
			{exc.printStackTrace();}
	}
}
