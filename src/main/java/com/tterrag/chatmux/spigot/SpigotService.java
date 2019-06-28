package com.tterrag.chatmux.spigot;

import org.pf4j.Extension;

import com.tterrag.chatmux.bridge.ChatService;
import com.tterrag.chatmux.bridge.ChatSource;
import com.tterrag.chatmux.config.ServiceConfig;

import net.md_5.bungee.api.chat.TextComponent;

@Extension
public class SpigotService extends ChatService<TextComponent, String> {

    public SpigotService() {
        super("spigot");
        instance = this;
    }

    private static SpigotService instance;

    public static SpigotService getInstance() {
        SpigotService inst = instance;
        if (inst == null) {
            throw new IllegalStateException("Discord service not initialized");
        }
        return inst;
    }

    @Override
    protected ChatSource<TextComponent, String> createSource() {
        return new SpigotSource();
    }

    @Override
    public ServiceConfig<?> getConfig() {
        return null;
    }
}
