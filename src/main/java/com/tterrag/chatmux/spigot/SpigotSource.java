package com.tterrag.chatmux.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.tterrag.chatmux.bridge.ChatMessage;
import com.tterrag.chatmux.bridge.ChatService;
import com.tterrag.chatmux.bridge.ChatSource;

import emoji4j.Emoji;
import emoji4j.EmojiManager;
import emoji4j.EmojiUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SpigotSource implements ChatSource {

    private Listener listener;

    @Override
    public ChatService getType() {
        return SpigotService.getInstance();
    }

    @Override
    public Flux<? extends ChatMessage> connect(String channel) {
        return Flux.<SpigotMessage> create(sink -> {
            sink.next(new SpigotMessage("Server Online!", false));
            ChatMuxPlugin.instance.getServer().getPluginManager().registerEvents(listener = new Listener() {

                @EventHandler
                public void onChat(AsyncPlayerChatEvent event) {
                    sink.next(new SpigotMessage(event.getMessage(), event.getPlayer(), false));
                }
                
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    sink.next(new SpigotMessage("joined the game", event.getPlayer(), true));
                }
                
                @EventHandler
                public void onLeave(PlayerQuitEvent event) {
                    sink.next(new SpigotMessage("left the game", event.getPlayer(), true));
                }
                
                @EventHandler
                public void onDeath(PlayerDeathEvent event) {
                    String msg = event.getDeathMessage();
                    if (msg.startsWith(event.getEntity().getName())) {
                        msg = msg.replace(event.getEntity().getName(), "");
                    }
                    sink.next(new SpigotMessage(msg, event.getEntity(), true));
                }
                
                @EventHandler
                public void onServerCommand(ServerCommandEvent event) {
                    if (SpigotService.getInstance().getData().sendCommands()) {
                        sink.next(new SpigotMessage("sent command: " + event.getCommand(), true));
                    } else if (event.getCommand().startsWith("say ")) {
                        sink.next(new SpigotMessage(event.getCommand().substring(4), false));
                    }
                }
                
                @EventHandler
                public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
                    if (SpigotService.getInstance().getData().sendCommands()) {
                        sink.next(new SpigotMessage("sent command: " + event.getMessage().substring(1), event.getPlayer(), true));
                    }
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
                ChatMuxPlugin.instance.getServer().broadcastMessage(
                        cleanupEmojis(formatMinecraft(payload))));
    }
    
    private static final String FORMAT_CODE = "\u00A7";
    private static final String GRAY = FORMAT_CODE + '7';
    private static final String RESET = FORMAT_CODE + 'r';
    
    private static String formatMinecraft(ChatMessage payload) {
        return GRAY + "[" + payload.getSource().getName() + "/" + payload.getChannel() + "] " + RESET + "<" + payload.getUser() + "> " + payload.getContent();
    }

    private static String cleanupEmojis(String text) {
        String emojifiedText = EmojiUtils.emojify(text);
        for (Emoji emoji : EmojiManager.data()) {
            StringBuilder shortCodeBuilder = new StringBuilder();
            if (emoji.getEmoticons() != null && emoji.getEmoticons().size() > 0) {
                shortCodeBuilder.append(emoji.getEmoticons().get(0));
            } else {
                shortCodeBuilder.append(":").append(emoji.getAliases().get(0)).append(":");
            }

            emojifiedText = emojifiedText.replace(emoji.getEmoji(), shortCodeBuilder.toString());
        }
        return emojifiedText;
    }

    @Override
    public void disconnect(String channel) {}

}
