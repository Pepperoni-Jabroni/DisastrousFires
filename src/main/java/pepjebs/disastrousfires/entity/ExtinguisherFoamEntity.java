package pepjebs.disastrousfires.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pepjebs.disastrousfires.DisastrousFiresMod;

public class ExtinguisherFoamEntity extends ThrownItemEntity {

    public ExtinguisherFoamEntity(World world, PlayerEntity user) {
        super(DisastrousFiresMod.EXTINGUISHER_FOAM, user, world);
    }

    public ExtinguisherFoamEntity(EntityType<? extends ExtinguisherFoamEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return DisastrousFiresMod.EXTINGUISHER_FOAM_ITEM;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity().isOnFire()) {
            entityHitResult.getEntity().setOnFire(false);
            entityHitResult.getEntity().setFireTicks(0);
            this.getWorld().playSound(null, entityHitResult.getEntity().getBlockPos(),
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos source = blockHitResult.getBlockPos();
        boolean playSound = false;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    BlockPos p = source.add(i, j, k);
                    if (this.getWorld().getBlockState(p).getBlock() == Blocks.FIRE) {
                        this.getWorld().setBlockState(p, Blocks.AIR.getDefaultState());
                        playSound = true;
                    }
                }
            }
        }
        if (playSound) {
            this.getWorld().playSound(null, source,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        if (this.getWorld().getBlockState(source).isOpaque()) {
            this.kill();
            this.getWorld().addParticle(
                DisastrousFiresMod.EXTINGUISHER_FOAM_PARTICLE,
                source.getX() + 0.5f + blockHitResult.getSide().getOffsetX(),
                source.getY() + 0.5f + blockHitResult.getSide().getOffsetY(),
                source.getZ() + 0.5f + blockHitResult.getSide().getOffsetZ(),
                0,
                0,
                0
            );
        }
        super.onBlockHit(blockHitResult);
    }
}
