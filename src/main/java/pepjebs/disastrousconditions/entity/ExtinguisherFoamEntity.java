package pepjebs.disastrousconditions.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

public class ExtinguisherFoamEntity extends ThrownItemEntity {

    public ExtinguisherFoamEntity(World world, PlayerEntity user) {
        super(DisastrousConditionsMod.EXTINGUISHER_FOAM, user, world);
    }

    public ExtinguisherFoamEntity(EntityType<? extends ExtinguisherFoamEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    @Override
    protected Item getDefaultItem() {
        return DisastrousConditionsMod.EXTINGUISHER_FOAM_ITEM;
    }

    @Override
    public boolean cannotBeSilenced() {
        return super.cannotBeSilenced();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockPos source = blockHitResult.getBlockPos();
        boolean playSound = false;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                for (int k = -1; k < 2; k++) {
                    BlockPos p = source.add(i, j, k);
                    if (this.world.getBlockState(p).getBlock() == Blocks.FIRE) {
                        this.world.setBlockState(p, Blocks.AIR.getDefaultState());
                        playSound = true;
                    }
                }
            }
        }
        if (playSound) {
            this.world.playSound(null, source,
                    SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        if (this.world.getBlockState(source).isOpaque()) {
            this.kill();
            this.world.addParticle(
                DisastrousConditionsMod.EXTINGUISHER_FOAM_PARTICLE,
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
