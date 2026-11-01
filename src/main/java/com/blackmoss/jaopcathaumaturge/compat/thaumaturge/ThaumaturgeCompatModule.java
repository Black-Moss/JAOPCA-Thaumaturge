package com.blackmoss.jaopcathaumaturge.compat.thaumaturge;

import com.blackmoss.jaopcathaumaturge.JAOPCAThaumaturge;
import net.minecraft.resources.Identifier;
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

    private static final Set<String> BLACKLIST = new TreeSet<>(List.of(
            "cinnabar",
            "copper",
            "gold",
            "iron",
            "lead",
            "quartz",
            "silver",
            "tin"));

    private static final Set<MaterialType> MATERIAL_TYPES = EnumSet.of(
            MaterialType.INGOT);

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
                "Should recipes that produce Thaumaturge clusters from ore and smelt them back into 2 materials be added.");
    }

    @Override
    public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
        if (moduleConfig != null) {
            addClusterRecipes = moduleConfig.getDefinedBoolean("recipes.cluster_recipes", addClusterRecipes,
                    "Should recipes that produce Thaumaturge clusters from ore and smelt them back into 2 materials be added.");
        }
        JAOPCAApi api = ApiImpl.INSTANCE;
        IMiscHelper miscHelper = MiscHelper.INSTANCE;
        IItemFormType itemFormType = ItemFormType.INSTANCE;
        Set<Identifier> itemTags = api.getItemTags();
        int clusterCount = 0;
        int recipeCount = 0;
        for (IMaterial material : clusterForm.getMaterials()) {
            IItemInfo clusterInfo = itemFormType.getMaterialFormInfo(clusterForm, material);
            if (clusterInfo == null) {
                continue;
            }
            Identifier oreLocation = miscHelper.getTagLocation("ores", material.getName());
            Identifier rawMaterialLocation = miscHelper.getTagLocation("raw_materials", material.getName());
            Identifier materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());
            if (!itemTags.contains(materialLocation)) {
                JAOPCAThaumaturge.LOGGER.warn(
                        "Skipping Thaumaturge cluster for material {} since it lacks the material tag {}",
                        material.getName(), materialLocation);
                continue;
            }
            Identifier sourceLocation = itemTags.contains(rawMaterialLocation)
                    ? rawMaterialLocation
                    : (itemTags.contains(oreLocation)
                    ? oreLocation
                    : null);
            ++clusterCount;
            if (!addClusterRecipes) {
                continue;
            }
            if (sourceLocation != null
                    && api.registerShapelessRecipe(
                    JAOPCAThaumaturge.identifier("cluster_" + material.getName() + "_from_source"),
                    clusterInfo, 1, sourceLocation)) {
                ++recipeCount;
            }
            if (api.registerSmeltingRecipe(
                    JAOPCAThaumaturge.identifier("cluster_" + material.getName() + "_smelting"),
                    clusterInfo, materialLocation, 2, 1F, 200)) {
                ++recipeCount;
            }
            if (api.registerBlastingRecipe(
                    JAOPCAThaumaturge.identifier("cluster_" + material.getName() + "_blasting"),
                    clusterInfo, materialLocation, 2, 1F, 100)) {
                ++recipeCount;
            }
        }
        JAOPCAThaumaturge.LOGGER.info("Registered Thaumaturge clusters for {} materials with {} recipes", clusterCount, recipeCount);
    }
}
