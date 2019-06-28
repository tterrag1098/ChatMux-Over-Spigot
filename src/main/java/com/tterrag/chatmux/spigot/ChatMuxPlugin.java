package com.tterrag.chatmux.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import com.tterrag.chatmux.Main;
import com.tterrag.chatmux.links.LinkManager;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

public class ChatMuxPlugin extends JavaPlugin {

    public static volatile ChatMuxPlugin instance;

    private static Disposable bot;

    @Override
    public void onEnable() {
        instance = this;
        try {
            bot = Main.start(null).subscribe();
        } catch (ExceptionInInitializerError e) {
            getServer().broadcastMessage("ChatMux-Over-Spigot does not support /reload!");
        }
    }
    
    @Override
    public void onDisable() {
        LinkManager.INSTANCE.getLinks().stream()
                .filter(link -> link.getFrom().getType() instanceof SpigotService)
                .map(link -> link.getTo().getType().getSource().send(link.getTo().getName(), new SpigotMessage("Server Stopped!", false), false))
                .reduce(Mono::when)
                .ifPresent(Mono::block);
        bot.dispose();
    }
}
