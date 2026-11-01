package com.blackmoss.jaopcathaumaturge.compat.thaumaturge.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import thelm.jaopca.api.recipes.IRecipeSerializer;

public class CrucibleClusterRecipeSerializer implements IRecipeSerializer {
    private static final int METALLUM_AMOUNT = 5;
    private static final int ORDO_AMOUNT = 5;

    private final String materialName;

    public CrucibleClusterRecipeSerializer(String materialName) {
        this.materialName = materialName;
    }

    @Override
    public JsonElement get() {
        JsonObject recipe = new JsonObject();

        JsonArray conditions = new JsonArray();
        JsonObject not = new JsonObject();
        not.addProperty("type", "neoforge:not");
        JsonObject tagEmpty = new JsonObject();
        tagEmpty.addProperty("type", "neoforge:tag_empty");
        tagEmpty.addProperty("tag", "c:raw_materials/" + materialName);
        not.add("value", tagEmpty);
        conditions.add(not);
        recipe.add("neoforge:conditions", conditions);

        recipe.addProperty("type", "thaumaturge:crucible");

        JsonArray aspects = new JsonArray();
        JsonObject metallum = new JsonObject();
        metallum.addProperty("amount", METALLUM_AMOUNT);
        metallum.addProperty("aspect", "thaumaturge:metallum");
        aspects.add(metallum);
        JsonObject ordo = new JsonObject();
        ordo.addProperty("amount", ORDO_AMOUNT);
        ordo.addProperty("aspect", "thaumaturge:ordo");
        aspects.add(ordo);
        recipe.add("aspects", aspects);

        JsonObject catalyst = new JsonObject();
        catalyst.addProperty("tag", "c:raw_materials/" + materialName);
        recipe.add("catalyst", catalyst);

        JsonObject research = new JsonObject();
        research.addProperty("entry", "thaumaturge:metal_purification");
        recipe.add("research", research);

        JsonObject result = new JsonObject();
        result.addProperty("id", "jaopca:thaumaturge_cluster." + materialName);
        recipe.add("result", result);

        return recipe;
    }
}
