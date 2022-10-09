package pepjebs.disastrousconditions.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

public class DisastrousConditionsClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Extinguisher foam client prep
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) -> {
            registry.register(new Identifier(DisastrousConditionsMod.MOD_ID, "entity/extinguisher_foam"));
        }));
        ParticleFactoryRegistry.getInstance().register(
                DisastrousConditionsMod.EXTINGUISHER_FOAM_PARTICLE, SpitParticle.Factory::new);
        EntityRendererRegistry.register(DisastrousConditionsMod.EXTINGUISHER_FOAM, FlyingItemEntityRenderer::new);

        // Soot client prep
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registry.BLOCK.get(DisastrousConditionsMod.SOOT),
                RenderLayer.getCutout()
        );

        // TODO: Make this programmatic
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registry.BLOCK.get(DisastrousConditionsMod.BURNED_CROP_ID),
                RenderLayer.getCutout()
        );
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
