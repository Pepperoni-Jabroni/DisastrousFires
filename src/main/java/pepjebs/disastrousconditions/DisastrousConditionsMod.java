package pepjebs.disastrousconditions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.Random;

public class DisastrousConditionsMod implements ModInitializer {

    public static final String MOD_ID = "disastrous_conditions";

    public static Identifier BURNED_LOG_ID = new Identifier(MOD_ID, "burned_log");
    public static Identifier BURNED_STRIPPED_LOG_ID = new Identifier(MOD_ID, "burned_stripped_log");
    public static Identifier BURNED_PLANKS_ID = new Identifier(MOD_ID, "burned_planks");
    public static Identifier BURNED_LEAVES_ID = new Identifier(MOD_ID, "burned_leaves");
    public static Identifier BURNED_FLOWER_ID = new Identifier(MOD_ID, "burned_flower");
    public static Identifier BURNED_GRASS_ID = new Identifier(MOD_ID, "burned_grass");
    public static Identifier BURNED_GRASS_BLOCK_ID = new Identifier(MOD_ID, "burned_grass_block");

    @Override
    public void onInitialize() {
        // Set Grass as flammable
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.GRASS_BLOCK, 5, 20);

        // Register burned blocks
        // TODO: Make this programmatic
        registerBlock(
                BURNED_LOG_ID,
                new PillarBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_STRIPPED_LOG_ID,
                new PillarBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_PLANKS_ID,
                new Block(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_LEAVES_ID,
                new Block(FabricBlockSettings.of(Material.LEAVES).nonOpaque().ticksRandomly()
                        .sounds(BlockSoundGroup.BAMBOO)) {
                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        if (random.nextInt(0, 30) == 0) {
                            world.removeBlock(pos, false);
                        }
                    }
                }
        );

        registerBlock(
                BURNED_FLOWER_ID,
                new PlantBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)) {
                    @Override
                    public VoxelShape getOutlineShape(
                            BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                        return Block.createCuboidShape(
                                5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
                    }
                }
        );

        registerBlock(
                BURNED_GRASS_ID,
                new PlantBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)) {
                    @Override
                    public VoxelShape getOutlineShape(
                            BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                        return Block.createCuboidShape(
                                2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
                    }
                }
        );

        registerBlock(
                BURNED_GRASS_BLOCK_ID,
                new Block(FabricBlockSettings.of(Material.PLANT).ticksRandomly().sounds(BlockSoundGroup.GRASS)) {
                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        if (random.nextInt(0, 50) == 0) {
                            BlockState toSet = random.nextBoolean() ? Blocks.DIRT.getDefaultState()
                                    : Blocks.GRASS_BLOCK.getDefaultState();
                            world.setBlockState(pos, toSet);
                            if (random.nextInt(0, 8) == 0) {
                                BlockPos above = pos.mutableCopy().add(0, 1, 0);
                                int type = random.nextInt(0, 5);
                                if (type < 2) {
                                    world.setBlockState(above, Blocks.GRASS.getDefaultState());
                                } else if (type < 4) {
                                    world.setBlockState(above, Blocks.FERN.getDefaultState());
                                } else {
                                    world.setBlockState(above, Blocks.DANDELION.getDefaultState());
                                }
                            }
                        }
                    }
                }
        );
    }

    private static void registerBlock(
            Identifier id,
            Block block
    ) {
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
    }
}
