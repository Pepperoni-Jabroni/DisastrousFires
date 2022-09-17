package pepjebs.disastrousconditions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DisastrousConditionsMod implements ModInitializer {

    public static final String MOD_ID = "disastrous_conditions";

    public static Identifier BURNED_LOG_ID = new Identifier(MOD_ID, "burned_log");
    public static Identifier BURNED_LEAVES_ID = new Identifier(MOD_ID, "burned_leaves");
    public static Identifier BURNED_GRASS_ID = new Identifier(MOD_ID, "burned_grass");
    public static Identifier BURNED_GRASS_BLOCK_ID = new Identifier(MOD_ID, "burned_grass_block");

    @Override
    public void onInitialize() {
        // Set Grass as flammable
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.GRASS_BLOCK, 5, 20);

        // Register burned blocks
        Block burnedLog =
                Registry.register(
                        Registry.BLOCK,
                        BURNED_LOG_ID,
                        new PillarBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                                .sounds(BlockSoundGroup.WOOD))
                );
        Registry.register(
                Registry.ITEM,
                BURNED_LOG_ID,
                new BlockItem(burnedLog, new Item.Settings().group(ItemGroup.MISC))
        );

        Block burnedLeaves =
                Registry.register(
                        Registry.BLOCK,
                        BURNED_LEAVES_ID,
                        new Block(FabricBlockSettings.of(Material.LEAVES).nonOpaque().sounds(BlockSoundGroup.BAMBOO))
                );
        BlockRenderLayerMap.INSTANCE.putBlock(burnedLeaves, RenderLayer.getTranslucent());
        Registry.register(
                Registry.ITEM,
                BURNED_LEAVES_ID,
                new BlockItem(burnedLeaves, new Item.Settings().group(ItemGroup.MISC))
        );

        Block burnedGrass =
                Registry.register(
                        Registry.BLOCK,
                        BURNED_GRASS_ID,
                        new PlantBlock(FabricBlockSettings.of(Material.PLANT).nonOpaque().collidable(false)
                                .sounds(BlockSoundGroup.GRASS))
                );
        BlockRenderLayerMap.INSTANCE.putBlock(burnedGrass, RenderLayer.getTranslucent());
        Registry.register(
                Registry.ITEM,
                BURNED_GRASS_ID,
                new BlockItem(burnedGrass, new Item.Settings().group(ItemGroup.MISC))
        );

        Block burnedGrassBlock =
                Registry.register(
                        Registry.BLOCK,
                        BURNED_GRASS_BLOCK_ID,
                        new Block(FabricBlockSettings.of(Material.PLANT).sounds(BlockSoundGroup.GRASS))
                );
        Registry.register(
                Registry.ITEM,
                BURNED_GRASS_BLOCK_ID,
                new BlockItem(burnedGrassBlock, new Item.Settings().group(ItemGroup.MISC))
        );
    }
}
