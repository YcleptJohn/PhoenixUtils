package uk.co.ycleptjohn.phoenixutils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PhoenixUtils extends JavaPlugin implements Listener {
	public ConcurrentHashMap<String, UUID> userIdMap;
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender cmdSender, Command cmd, String cmdLabel, String[] args) {
		if (cmdLabel.equalsIgnoreCase("getid")) {
			if (userIdMap.containsKey(args[0])) {
				cmdSender.sendMessage(args[0] + " | " + userIdMap.get(args[0]));
			} else {
				cmdSender.sendMessage("Player not stored");
			}
			return true;
		}
		return false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onLogin(PlayerLoginEvent e) {
		String username = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				UUIDFetcher fetcher = new UUIDFetcher(Arrays.asList(username));
				try {
					if (userIdMap.isEmpty()) {
						userIdMap = (ConcurrentHashMap<String, UUID>) fetcher.call();
					} else {
						userIdMap.putIfAbsent(username, fetcher.call().get(username));
					}
				} catch (Exception e) {
					getLogger().warning("Exception whilst fetching uuid for player: " + username);
					e.printStackTrace();
				}
			}
		});
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e) {
		if (userIdMap.containsKey(e.getPlayer().getName())) {
			userIdMap.remove(e.getPlayer().getName());
			getLogger().info(e.getPlayer().getName() + " removed from the hashmap due to being kicked");
		}
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		if (userIdMap.containsKey(e.getPlayer().getName())) {
			userIdMap.remove(e.getPlayer().getName());
			getLogger().info(e.getPlayer().getName() + " removed from the hashmap due to a disconnect");
		}
	}
}
