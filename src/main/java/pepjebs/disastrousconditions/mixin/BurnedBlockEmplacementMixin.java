package pepjebs.disastrousconditions.mixin;

import net.minecraft.block.*;
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

import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.LAYERS;

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
        // TODO: Make this programmatic
        boolean setBurned = false;
        if (Registry.BLOCK.getId(state.getBlock()).toString().contains("stripped_log")) {
            BlockState toSet = Registry.BLOCK.get(DisastrousConditionsMod.BURNED_STRIPPED_LOG_ID).getDefaultState();
            if (state.getProperties().contains(AXIS)) {
                toSet = toSet.with(AXIS, state.get(AXIS));
            }
            world.setBlockState(pos, toSet);
            setBurned = true;
        } else if (Registry.BLOCK.getId(state.getBlock()).toString().contains("log")) {
            BlockState toSet = Registry.BLOCK.get(DisastrousConditionsMod.BURNED_LOG_ID).getDefaultState();
            if (rand.nextInt(0, 4) == 0) {
                toSet = Registry.BLOCK.get(DisastrousConditionsMod.BURNED_STRIPPED_LOG_ID).getDefaultState();
            }
            if (state.getProperties().contains(AXIS)) {
                toSet = toSet.with(AXIS, state.get(AXIS));
            }
            world.setBlockState(pos, toSet);
            setBurned = true;
        } else if (Registry.BLOCK.getId(state.getBlock()).toString().contains("planks")) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_PLANKS_ID).getDefaultState());
            setBurned = true;
        } else if (Registry.BLOCK.getId(state.getBlock()).toString().contains("leaves") && rand.nextBoolean()) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_LEAVES_ID).getDefaultState());
            BlockPos below = pos.add(0, -1, 0);
            boolean didCast = false;
            BlockState castBlock = world.getBlockState(below);
            while (castBlock.getBlock() == Blocks.AIR) {
                below = below.add(0, -1, 0);
                didCast = true;
                castBlock = world.getBlockState(below);
            }
            if (didCast) {
                if (castBlock.getBlock() == Registry.BLOCK.get(DisastrousConditionsMod.ASH_LAYER)) {
                    world.setBlockState(below, castBlock.with(LAYERS, castBlock.get(LAYERS) + 1));
                } else if (castBlock.isOpaque()){
                    world.setBlockState(below.add(0, 1, 0),
                            Registry.BLOCK.get(DisastrousConditionsMod.ASH_LAYER).getDefaultState());
                }
            }
            setBurned = true;
        } else if (state.getBlock() == Blocks.GRASS_BLOCK) {
            if (rand.nextInt(0, 4) < 3) {
                world.setBlockState(pos, Registry.BLOCK.get(
                        DisastrousConditionsMod.BURNED_GRASS_BLOCK_ID).getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
            BlockPos above = pos.mutableCopy().add(0 ,1, 0);
            BlockState aboveState = world.getBlockState(above);
            if (aboveState.getBlock() == Blocks.GRASS) {
                world.setBlockState(above,
                        Registry.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID).getDefaultState());
            } else if (aboveState.getBlock() instanceof FlowerBlock) {
                world.setBlockState(above,
                        Registry.BLOCK.get(DisastrousConditionsMod.BURNED_FLOWER_ID).getDefaultState());
            } else if (aboveState.getBlock() == Blocks.AIR && rand.nextBoolean()) {
                world.setBlockState(above, Registry.BLOCK.get(DisastrousConditionsMod.ASH_LAYER).getDefaultState());
            }
            setBurned = true;
        } else if (state.getBlock() == Blocks.GRASS) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID).getDefaultState());
            emplaceBlockBelow(world, pos);
            setBurned = true;
        } else if (state.getBlock() instanceof FlowerBlock) {
            world.setBlockState(pos, Registry.BLOCK.get(DisastrousConditionsMod.BURNED_FLOWER_ID).getDefaultState());
            emplaceBlockBelow(world, pos);
            setBurned = true;
        }
        if (setBurned) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        BlockPos query = pos.mutableCopy().add(i, j, k);
                        if (world.getBlockState(query).getBlock() == Blocks.AIR) {
                            world.setBlockState(query, Blocks.FIRE.getDefaultState());
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void emplaceBlockBelow(World world, BlockPos pos) {
        BlockPos below = pos.mutableCopy().add(0, -1, 0);
        if (world.getBlockState(below).getBlock() == Blocks.GRASS_BLOCK) {
            world.setBlockState(below, Registry.BLOCK.get(
                    DisastrousConditionsMod.BURNED_GRASS_BLOCK_ID).getDefaultState());
        } else {
            world.setBlockState(below, Blocks.DIRT.getDefaultState());
        }
    }
}
