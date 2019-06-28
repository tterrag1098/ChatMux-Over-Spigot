package com.tterrag.chatmux.spigot;

import org.pf4j.Extension;

import com.tterrag.chatmux.bridge.ChatService;
import com.tterrag.chatmux.bridge.ChatSource;
import com.tterrag.chatmux.config.ServiceConfig;
import com.tterrag.chatmux.config.SimpleServiceConfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Extension
public class SpigotService extends ChatService {

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
    protected ChatSource createSource() {
        return new SpigotSource();
    }
    
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private SpigotData data = new SpigotData();

    @Override
    public ServiceConfig<?> getConfig() {
        return new SimpleServiceConfig<SpigotData>(SpigotData::new, this::setData);
    }
}
