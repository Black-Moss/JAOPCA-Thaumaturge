package com.blackmoss.jaopcathaumaturge;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(JAOPCAThaumaturge.MODID)
public class JAOPCAThaumaturge {
    public static final String MODID = "jaopcathaumaturge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JAOPCAThaumaturge(IEventBus modEventBus, ModContainer modContainer) {
    }
}
