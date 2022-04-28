package io.github.crumcreators.topocrafty;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.structure.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static io.github.crumcreators.topocrafty.Topocrafty.MOD_ID;

public class MyBiomeMyStructure {

    public static final RegistryKey<Biome> BIOME_KEY = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "my_biome"));
    public static final StructurePieceType MY_STRUCTURE_PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, new Identifier(MOD_ID, "my_structure_piece"), (context, nbt) -> new MyStructureFeature.Piece(nbt));

    static {
        final MyStructureFeature STRUCTURE_FEATURE = Registry.register(Registry.STRUCTURE_FEATURE, new Identifier(MOD_ID, "my_structure"), new MyStructureFeature());
        final RegistryEntry<Biome> BIOME_ENTRY = BuiltinRegistries.add(BuiltinRegistries.BIOME, BIOME_KEY, createBiome());
        final RegistryKey<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCT_KEY = RegistryKey.of(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, new Identifier(MOD_ID, "my_structure"));
        final RegistryEntry<ConfiguredStructureFeature<?, ?>> CONFIGURED_STRUCT_ENTRY = BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, CONFIGURED_STRUCT_KEY, new ConfiguredStructureFeature<>(STRUCTURE_FEATURE, DefaultFeatureConfig.INSTANCE, RegistryEntryList.of(BIOME_ENTRY), false, Map.of()));
        final RegistryKey<StructureSet> STRUCTURE_SET_KEY = RegistryKey.of(Registry.STRUCTURE_SET_KEY, new Identifier(MOD_ID, "my_structure"));
        BuiltinRegistries.add(BuiltinRegistries.STRUCTURE_SET, STRUCTURE_SET_KEY, new StructureSet(CONFIGURED_STRUCT_ENTRY, MyStructurePlacement.INSTANCE));
    }

    private static int getSkyColor(float temperature) {
        final float f = MathHelper.clamp(temperature / 3.0F, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
    }

    public static Biome createBiome() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        DefaultBiomeFeatures.addBatsAndMonsters(builder);

        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addLandCarvers(builder2);
        DefaultBiomeFeatures.addAmethystGeodes(builder2);
        DefaultBiomeFeatures.addDungeons(builder2);
        DefaultBiomeFeatures.addMineables(builder2);
        DefaultBiomeFeatures.addSprings(builder2);
        DefaultBiomeFeatures.addFrozenTopLayer(builder2);
        DefaultBiomeFeatures.addForestFlowers(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addForestGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);

        return new Biome.Builder()
                .precipitation(Biome.Precipitation.NONE)
                .category(Biome.Category.DESERT)
                .temperature(0.7F)
                .downfall(0.8F)
                .effects(new BiomeEffects.Builder()
                        .waterColor(4159204)
                        .waterFogColor(329011).
                        fogColor(12638463)
                        .skyColor(getSkyColor(0.7F))
                        .grassColorModifier(BiomeEffects.GrassColorModifier.DARK_FOREST)
                        .moodSound(BiomeMoodSound.CAVE)
                        .build())
                .spawnSettings(builder.build())
                .generationSettings(builder2.build())
                .build();
    }


    public static class MyStructureFeature extends StructureFeature<DefaultFeatureConfig> {

        public MyStructureFeature() {
            super(DefaultFeatureConfig.CODEC, context -> canGenerate(context) ? Optional.of(MyStructureFeature::addPieces) : Optional.empty());
        }

        private static boolean canGenerate(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {
            return true;
        }

        private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
            collector.addPiece(new Piece(context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
        }

        @Override
        public GenerationStep.Feature getGenerationStep() {
            return GenerationStep.Feature.SURFACE_STRUCTURES;
        }

        public static class Piece extends StructurePiece {


            public Piece(int x, int z) {
                super(MY_STRUCTURE_PIECE_TYPE, 0, new BlockBox(x, 10, z, x + 16, 300, z + 16));
            }

            public Piece(NbtCompound nbt) {
                super(MY_STRUCTURE_PIECE_TYPE, nbt);
            }

            private static double f(double x) {
                x = Math.abs(0.01 * x);
                return 100.0 * (Math.pow(x, 0.1) * 2 * Math.exp(-1.3 * x * x) - 0.3 * Math.exp(-Math.pow(x, 10)));
            }

            @Override
            protected void writeNbt(StructureContext context, NbtCompound nbt) {
            }

            @Override
            protected void addBlock(StructureWorldAccess world, BlockState block, int x, int y, int z, BlockBox box) {
                BlockPos blockPos = new BlockPos(x, y, z);
                if (box.contains(blockPos)) {
                    world.setBlockState(blockPos, block, Block.NOTIFY_LISTENERS);
                }
            }

            @Override
            public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pos) {
                ChunkPos realStart = MyStructurePlacement.INSTANCE.getRealStart(world.getSeed(), chunkPos.x, chunkPos.z);
                int r = 200;
                int xoff = realStart.getStartX() + r;
                int zoff = realStart.getStartZ() + r;

                for (int x = -r; x <= r; ++x) {
                    int cx = x + xoff;
                    int maxZ = (int) MathHelper.sqrt(r * r - x * x);
                    for (int z = -maxZ; z <= maxZ; ++z) {
                        int cz = z + zoff;
                        int maxY = (int) f(Math.sqrt(x * x + z * z));
                        for (int y = -70; y <= maxY; ++y) {
                            this.addBlock(world, Blocks.MELON.getDefaultState(), cx, y + 70, cz, chunkBox);
                        }
                    }
                }
            }
        }
    }


    public static final class MyStructurePlacement extends RandomSpreadStructurePlacement {

        private static final MyStructurePlacement INSTANCE = new MyStructurePlacement();
        public static final StructurePlacementType<MyStructurePlacement> MY_SPREAD = Registry.register(Registry.STRUCTURE_PLACEMENT, new Identifier(MOD_ID, "my_spread"), () -> MapCodec.unit(INSTANCE).codec());
        private final RandomSpreadStructurePlacement placement = new RandomSpreadStructurePlacement(80, 20, SpreadType.TRIANGULAR, 1237987);

        private MyStructurePlacement() {
            super(0, 0, SpreadType.TRIANGULAR, 0, Vec3i.ZERO);
        }

        private ChunkPos getRealStart(long seed, int x, int z) {
            return placement.getStartChunk(seed, x, z);
        }

        @Override
        public ChunkPos getStartChunk(long seed, int x, int z) {
            ChunkPos realStart = getRealStart(seed, x, z);
            if (x >= realStart.x && x <= realStart.x + 26) {
                if (z >= realStart.z && z <= realStart.z + 26) {
                    return new ChunkPos(x, z);
                }
            }
            return new ChunkPos(realStart.x + 26, realStart.z + 26);
        }

        @Override
        public StructurePlacementType<?> getType() {
            return MY_SPREAD;
        }
    }
}
