package pepjebs.disastrousconditions.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

import java.util.Random;

@Mixin(FireBlock.class)
public class BurnedBlockEmplacementMixin {

    @Inject(
            method = "trySpreadingFire",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void emplaceBurnedBlock(World world, BlockPos pos, int spreadFactor, Random rand, int currentAge,
                                   CallbackInfo info, int idx, BlockState state) {
        boolean setBurned = false;
        if (Registry.BLOCK.getId(state.getBlock()).toString().contains("log")) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_LOG_ID).getDefaultState());
            setBurned = true;
        } else if (Registry.BLOCK.getId(state.getBlock()).toString().contains("leaves") && rand.nextBoolean()) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_LEAVES_ID).getDefaultState());
            setBurned = true;
        } else if (state.getBlock() == Blocks.GRASS_BLOCK && rand.nextBoolean()) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_BLOCK_ID).getDefaultState());
            setBurned = true;
        } else if (state.getBlock() == Blocks.GRASS && rand.nextBoolean()) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID).getDefaultState());
            setBurned = true;
        }
        if (setBurned) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        BlockPos query = pos.add(i, j, k);
                        if (world.getBlockState(query).getBlock() == Blocks.AIR) {
                            world.setBlockState(query, Blocks.FIRE.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}
