package com.tterrag.chatmux.spigot;

import com.electronwill.nightconfig.core.conversion.Path;
import com.tterrag.chatmux.config.ServiceData;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode
@ToString
@Getter
public class SpigotData implements ServiceData {
    
    @Path("send_commands")
    @Accessors(fluent = true)
    private boolean sendCommands = false;
    
    @Path("server_avatar")
    private String serverAvatar = null;
    
}
