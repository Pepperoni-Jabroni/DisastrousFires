package pepjebs.disastrousfires.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.particle.SpitParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.registry.Registries;
import pepjebs.disastrousfires.DisastrousFiresMod;

public class DisastrousFiresClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Extinguisher foam client prep
        ParticleFactoryRegistry.getInstance().register(
                DisastrousFiresMod.EXTINGUISHER_FOAM_PARTICLE, SpitParticle.Factory::new);
        EntityRendererRegistry.register(DisastrousFiresMod.EXTINGUISHER_FOAM, FlyingItemEntityRenderer::new);

        // Soot client prep
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(DisastrousFiresMod.SOOT_LAYER),
                RenderLayer.getCutout()
        );

        // TODO: Make this programmatic
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(DisastrousFiresMod.BURNED_CROP_ID),
                RenderLayer.getCutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(DisastrousFiresMod.BURNED_GRASS_ID),
                RenderLayer.getCutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(DisastrousFiresMod.BURNED_FLOWER_ID),
                RenderLayer.getCutout()
        );
        BlockRenderLayerMap.INSTANCE.putBlock(
                Registries.BLOCK.get(DisastrousFiresMod.BURNED_LEAVES_ID),
                RenderLayer.getTranslucent()
        );
    }
}
