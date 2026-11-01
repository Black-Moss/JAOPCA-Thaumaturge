package com.blackmoss.jaopcathaumaturge.compat.thaumaturge;

import com.blackmoss.jaopcathaumaturge.JAOPCAThaumaturge;
import com.blackmoss.jaopcathaumaturge.compat.thaumaturge.recipes.CrucibleClusterRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.forms.IFormRequest;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.items.IItemFormType;
import thelm.jaopca.api.items.IItemInfo;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.items.ItemFormType;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

import java.util.*;

@JAOPCAModule(modDependencies = "thaumaturge")
public class ThaumaturgeCompatModule implements IModule {
    public static final String FORM_NAME = "thaumaturge_cluster";

    // Thaumaturge already provides native clusters for these materials.
    private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
            "cinnabar", "copper", "gold", "iron", "lead", "quartz", "silver", "tin"));

    // Only materials that have an ore, a raw material and an ingot (MaterialType.INGOT).
    private static final Set<MaterialType> MATERIAL_TYPES = EnumSet.of(MaterialType.INGOT);

    private static boolean addClusterRecipes = true;
    private static IDynamicSpecConfig moduleConfig;

    private final IForm clusterForm = ApiImpl.INSTANCE.newForm(this, FORM_NAME, ItemFormType.INSTANCE).
            setMaterialTypes(MATERIAL_TYPES).
            setSecondaryName("thaumaturge:clusters").
            setDefaultMaterialBlacklist(BLACKLIST);

    @Override
    public String getName() {
        return "thaumaturge";
    }

    @Override
    public List<IFormRequest> getFormRequests() {
        return List.of(clusterForm.toRequest());
    }

    @Override
    public Set<MaterialType> getMaterialTypes() {
        return MATERIAL_TYPES;
    }

    @Override
    public Set<String> getDefaultMaterialBlacklist() {
        return BLACKLIST;
    }

    @Override
    public void defineModuleConfig(IModuleData moduleData, IDynamicSpecConfig config) {
        moduleConfig = Objects.requireNonNull(config);
        addClusterRecipes = config.getDefinedBoolean("recipes.cluster_recipes", addClusterRecipes,
                "Should recipes that produce Thaumaturge clusters from raw materials in the crucible and smelt them back into 2 ingots be added.");
    }

    @Override
    public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
        if (moduleConfig != null) {
            addClusterRecipes = moduleConfig.getDefinedBoolean("recipes.cluster_recipes", addClusterRecipes,
                    "Should recipes that produce Thaumaturge clusters from raw materials in the crucible and smelt them back into 2 ingots be added.");
        }
        JAOPCAApi api = ApiImpl.INSTANCE;
        IMiscHelper miscHelper = MiscHelper.INSTANCE;
        IItemFormType itemFormType = ItemFormType.INSTANCE;
        Set<ResourceLocation> itemTags = api.getItemTags();
        int clusterCount = 0;
        int recipeCount = 0;
        for (IMaterial material : clusterForm.getMaterials()) {
            IItemInfo clusterInfo = itemFormType.getMaterialFormInfo(clusterForm, material);
            if (clusterInfo == null) {
                continue;
            }
            String name = material.getName();
            ResourceLocation rawMaterialLocation = miscHelper.getTagLocation("raw_materials", name);
            ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), name);
            if (!itemTags.contains(materialLocation)) {
                JAOPCAThaumaturge.LOGGER.warn(
                        "Skipping Thaumaturge cluster for material {} since it lacks the material tag {}",
                        name, materialLocation);
                continue;
            }
            if (!itemTags.contains(rawMaterialLocation)) {
                JAOPCAThaumaturge.LOGGER.warn(
                        "Skipping Thaumaturge cluster for material {} since it lacks the raw material tag {}",
                        name, rawMaterialLocation);
                continue;
            }
            ++clusterCount;
            if (!addClusterRecipes) {
                continue;
            }
            if (api.registerRecipe(
                    JAOPCAThaumaturge.identifier("crucible/" + name + "_cluster"),
                    new CrucibleClusterRecipeSerializer(name))) {
                ++recipeCount;
            }
            if (api.registerSmeltingRecipe(
                    JAOPCAThaumaturge.identifier("cluster_" + name + "_smelting"),
                    clusterInfo, materialLocation, 2, 1F, 200)) {
                ++recipeCount;
            }
            if (api.registerBlastingRecipe(
                    JAOPCAThaumaturge.identifier("cluster_" + name + "_blasting"),
                    clusterInfo, materialLocation, 2, 1F, 100)) {
                ++recipeCount;
            }
        }
        JAOPCAThaumaturge.LOGGER.info("Registered Thaumaturge clusters for {} materials with {} recipes", clusterCount, recipeCount);
    }
}
