package com.tterrag.chatmux.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.tterrag.chatmux.bridge.ChatMessage;
import com.tterrag.chatmux.bridge.ChatService;
import com.tterrag.chatmux.bridge.ChatSource;

import net.md_5.bungee.api.chat.TextComponent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SpigotSource implements ChatSource<TextComponent, String> {
	
	private Listener listener;

	@Override
	public ChatService<TextComponent, String> getType() {
		return SpigotService.getInstance();
	}

	@Override
	public Flux<? extends ChatMessage> connect(String channel) {
		return Flux.<SpigotMessage>create(sink -> {
			ChatMuxPlugin.instance.getServer().getPluginManager().registerEvents(listener = new Listener() {
				
				@EventHandler
				public void onChat(AsyncPlayerChatEvent event) {
					sink.next(new SpigotMessage(event.getMessage(), event.getPlayer()));
				}
			}, ChatMuxPlugin.instance);
		}).doOnCancel(() -> {
			AsyncPlayerChatEvent.getHandlerList().unregister(listener);
			listener = null;
		});
	}

	@Override
	public Mono<Void> send(String channel, ChatMessage payload, boolean raw) {
		return Mono.fromRunnable(() -> 
				ChatMuxPlugin.instance.getServer().broadcastMessage(payload.toString()));
	}

	@Override
	public void disconnect(String channel) {}

}
