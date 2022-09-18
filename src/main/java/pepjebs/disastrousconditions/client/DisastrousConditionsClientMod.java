package pepjebs.disastrousconditions.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.registry.Registry;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

public class DisastrousConditionsClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // TODO: Make this programmatic
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registry.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID),
                RenderLayer.getCutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registry.BLOCK.get(DisastrousConditionsMod.BURNED_FLOWER_ID),
                RenderLayer.getCutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registry.BLOCK.get(DisastrousConditionsMod.BURNED_LEAVES_ID),
                RenderLayer.getTranslucent()
        );
    }
}
