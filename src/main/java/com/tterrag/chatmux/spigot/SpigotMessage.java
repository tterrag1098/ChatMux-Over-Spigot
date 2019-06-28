package com.tterrag.chatmux.spigot;

import org.bukkit.BanList;
import org.bukkit.entity.Player;

import com.tterrag.chatmux.bridge.ChatMessage;

import reactor.core.publisher.Mono;

public class SpigotMessage extends ChatMessage {

    private final Player player;

    public SpigotMessage(String content, Player player) {
        super(SpigotService.getInstance(), "Minecraft", player.getName(), content, null);
        this.player = player;
    }

    @Override
    public Mono<Void> delete() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> kick() {
        return Mono.fromRunnable(() -> player.kickPlayer("Kicked by ChatMux moderator."));
    }

    @Override
    public Mono<Void> ban() {
        return Mono.just(ChatMuxPlugin.instance.getServer().getBanList(BanList.Type.NAME))
                   .map(list -> list.addBan(player.getName(), "Banned by ChatMux moderator.", null, player.getName()))
                   .then();
    }

}
