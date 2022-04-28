package io.github.crumcreators.topocrafty;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrablender.api.*;

import java.util.function.Consumer;

public class Topocrafty implements ModInitializer, TerraBlenderApi {

    public static final String MOD_ID = "topocrafty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final MaterialRules.MaterialRule DIRT = makeStateRule(Blocks.DIRT);
    private static final MaterialRules.MaterialRule GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final MaterialRules.MaterialRule RED_TERRACOTTA = makeStateRule(Blocks.RED_TERRACOTTA);

    protected static MaterialRules.MaterialRule makeRules() {
        MaterialRules.MaterialCondition isAtOrAboveWaterLevel = MaterialRules.water(-1, 0);
        MaterialRules.MaterialRule grassSurface = MaterialRules.sequence(MaterialRules.condition(isAtOrAboveWaterLevel, GRASS_BLOCK), DIRT);

        return MaterialRules.sequence(
                MaterialRules.condition(MaterialRules.biome(MyBiomeMyStructure.BIOME_KEY), RED_TERRACOTTA),
                MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR, grassSurface)
        );
    }

    private static MaterialRules.MaterialRule makeStateRule(Block block) {
        return MaterialRules.block(block.getDefaultState());
    }

    @Override
    public void onInitialize() {
    }

    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new MyRegion());
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, makeRules());
    }

    private static class MyRegion extends Region {

        public MyRegion() {
            super(new Identifier(MOD_ID, "overworld"), RegionType.OVERWORLD, 2);
        }

        @Override
        public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
            this.addModifiedVanillaOverworldBiomes(mapper, builder -> builder.replaceBiome(BiomeKeys.DESERT, MyBiomeMyStructure.BIOME_KEY));
        }
    }
}
