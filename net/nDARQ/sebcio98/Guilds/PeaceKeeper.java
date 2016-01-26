package net.nDARQ.sebcio98.Guilds;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PeaceKeeper implements Listener
{
	@EventHandler
	public void a(EntityDamageByEntityEvent e)
	{
		if (!(e.getEntity() instanceof Player)) return;
		
		final String attackedGuild = PlayerHandler.getPlayer(e.getEntity().getName()).getGuild() == null ? "#" : PlayerHandler.getPlayer(e.getEntity().getName()).getGuild();
		if ((e.getDamager() instanceof Player && attackedGuild == PlayerHandler.getPlayer(e.getDamager().getName()).getGuild()) || (e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() instanceof Player && attackedGuild == PlayerHandler.getPlayer(((Player)((Projectile)e.getDamager()).getShooter()).getName()).getGuild())) e.setCancelled(true);
	}
}
