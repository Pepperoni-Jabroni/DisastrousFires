package pepjebs.disastrousconditions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class DisastrousConditionsMod implements ModInitializer {

    public static final String MOD_ID = "disastrous_conditions";

    public static Identifier BURNED_LOG_ID = new Identifier(MOD_ID, "burned_log");
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
                BURNED_PLANKS_ID,
                new Block(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_LEAVES_ID,
                new Block(FabricBlockSettings.of(Material.LEAVES).nonOpaque().sounds(BlockSoundGroup.BAMBOO))
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
                new Block(FabricBlockSettings.of(Material.PLANT).sounds(BlockSoundGroup.GRASS))
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
