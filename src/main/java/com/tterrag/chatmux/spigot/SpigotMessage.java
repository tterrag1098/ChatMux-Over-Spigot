package com.tterrag.chatmux.spigot;

import org.bukkit.BanList;
import org.bukkit.entity.Player;

import com.tterrag.chatmux.bridge.ChatMessage;

import reactor.core.publisher.Mono;

public class SpigotMessage extends ChatMessage {

    private final Player player;
    private final boolean action;

    public SpigotMessage(String content, Player player, boolean action) {
        super(SpigotService.getInstance(), "Minecraft", player.getName(), content, null);
        this.player = player;
        this.action = action;
    }
    
    public SpigotMessage(String content, String username, boolean action) {
        super(SpigotService.getInstance(), "Minecraft", username, content, null);
        this.player = null;
        this.action = action;
    }
    
    @Override
    public String getContent() {
        String ret = super.getContent();
        if (action) {
            ret = "*" + ret + "*";
        }
        return ret;
    }

    @Override
    public Mono<Void> delete() {
        return Mono.empty();
    }

    @Override
    public Mono<Void> kick() {
        return Mono.justOrEmpty(player).doOnNext(p -> p.kickPlayer("Kicked by ChatMux moderator.")).then();
    }

    @Override
    public Mono<Void> ban() {
        return Mono.justOrEmpty(player)
                   .map(Player::getName)
                   .map(name -> ChatMuxPlugin.instance.getServer().getBanList(BanList.Type.NAME)
                           .addBan(name, "Banned by ChatMux moderator.", null, name))
                   .then();
    }

}
