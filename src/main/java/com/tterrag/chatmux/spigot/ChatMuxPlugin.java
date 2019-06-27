package com.tterrag.chatmux.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import com.tterrag.chatmux.Main;

import reactor.core.Disposable;

public class ChatMuxPlugin extends JavaPlugin {
	
	public static volatile ChatMuxPlugin instance;
	
	private static Disposable bot;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		instance = this;
		bot = Main.start(null).subscribe();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		
		if (bot != null) {
			bot.dispose();
		}
	}
}
