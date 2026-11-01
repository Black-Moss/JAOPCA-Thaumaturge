package com.blackmoss.jaopcathaumaturge.compat.thaumaturge;

import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.JAOPCAModule;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@JAOPCAModule(modDependencies = "thaumaturge")
public class ThaumaturgeCompatModule implements IModule {
    public static final String FORM_NAME = "thaumaturge_cluster";

    private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
            "brass",
            "cinnabar",
            "copper",
            "gold",
            "iron",
            "lead",
            "quartz",
            "silver",
            "thaumium",
            "tin"));


    @Override
    public String getName() {
        return "thaumaturge";
    }

    @Override
    public Set<String> getDefaultMaterialBlacklist() {
        return BLACKLIST;
    }
}
