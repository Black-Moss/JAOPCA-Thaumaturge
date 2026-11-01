package com.blackmoss.jaopcathaumaturge;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(JAOPCAThaumaturge.MODID)
public class JAOPCAThaumaturge {
    public static final String MODID = "jaopcathaumaturge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JAOPCAThaumaturge(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
