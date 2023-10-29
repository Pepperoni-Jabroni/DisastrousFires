package pepjebs.disastrousconditions.mixin;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import pepjebs.disastrousconditions.DisastrousConditionsMod;

import static net.minecraft.state.property.Properties.*;

@Mixin(FireBlock.class)
public class BurnedBlockEmplacementMixin {

    @Inject(
            method = "scheduledTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/registry/entry/RegistryEntry;"
            )
    )
    public void emplaceSootBlocks(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (random.nextInt(8) != 0) return;
        for (int i = 0; i < 6; i++) {
            pos = pos.up();
            var block = world.getBlockState(pos).getBlock();
            if (block == Blocks.AIR) {
                BlockState toSet = Registries.BLOCK.get(DisastrousConditionsMod.SOOT_LAYER).getDefaultState();
                if (world.getBlockState(pos.north()).isFullCube(world, pos.north())) {
                    toSet = toSet.with(NORTH, true);
                }
                if (world.getBlockState(pos.east()).isFullCube(world, pos.east())) {
                    toSet = toSet.with(EAST, true);
                }
                if (world.getBlockState(pos.south()).isFullCube(world, pos.south())) {
                    toSet = toSet.with(SOUTH, true);
                }
                if (world.getBlockState(pos.west()).isFullCube(world, pos.west())) {
                    toSet = toSet.with(WEST, true);
                }
                if (world.getBlockState(pos.up()).isFullCube(world, pos.up())) {
                    toSet = toSet.with(UP, true);
                }
                if (toSet.get(NORTH) || toSet.get(EAST) || toSet.get(SOUTH) || toSet.get(WEST) || toSet.get(UP)) {
                    world.setBlockState(pos, toSet);
                }
                break;
            } else if (block != Registries.BLOCK.get(DisastrousConditionsMod.SOOT_LAYER)) {
                break;
            }
        }
    }

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
        // TODO: Refactor this whole fn
        if (FlammableBlockRegistry.getDefaultInstance().get(state.getBlock()) == null || world.isClient) {
            return;
        }
        var blockId = Registries.BLOCK.getId(state.getBlock());
        if (blockId == DisastrousConditionsMod.SOOT_BLOCK) {
            world.removeBlock(pos, false);
            world.createExplosion(
                    null, world.getDamageSources().create(DamageTypes.EXPLOSION), null,
                    pos.toCenterPos(), 3.0F, false, World.ExplosionSourceType.BLOCK);
            return;
        }
        if (rand.nextInt(10) < 3) {
            if (state.streamTags().anyMatch(t -> t == BlockTags.DIRT)) {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            } else {
                world.removeBlock(pos, false);
            }
            return;
        }
        if(blockId.toString().contains("wool")) {
            world.setBlockState(pos, Blocks.BLACK_WOOL.getDefaultState());
        } if (blockId.toString().contains("stripped_log")) {
            BlockState toSet = Registries.BLOCK.get(DisastrousConditionsMod.BURNED_STRIPPED_LOG_ID).getDefaultState();
            if (state.getProperties().contains(AXIS)) {
                toSet = toSet.with(AXIS, state.get(AXIS));
            }
            world.setBlockState(pos, toSet);
        } else if (blockId.toString().contains("log")) {
            BlockState toSet = Registries.BLOCK.get(DisastrousConditionsMod.BURNED_LOG_ID).getDefaultState();
            if (rand.nextInt(4) == 0) {
                toSet = Registries.BLOCK.get(DisastrousConditionsMod.BURNED_STRIPPED_LOG_ID).getDefaultState();
            }
            if (state.getProperties().contains(AXIS)) {
                toSet = toSet.with(AXIS, state.get(AXIS));
            }
            world.setBlockState(pos, toSet);
        } else if (blockId.toString().contains("stairs")) {
            BlockState toSet = Registries.BLOCK.get(DisastrousConditionsMod.BURNED_PLANK_STAIRS_ID).getDefaultState();
            for(var b : new Property[]{HORIZONTAL_FACING, BLOCK_HALF, STAIR_SHAPE}) {
                if (state.getProperties().contains(b)) {
                    toSet = toSet.with(b, state.get(b));
                }
            }
            world.setBlockState(pos, toSet);
        } else if (blockId.toString().contains("planks")) {
            world.setBlockState(pos, Registries.BLOCK.get(DisastrousConditionsMod.BURNED_PLANKS_ID).getDefaultState());
        } else if (blockId.toString().contains("leaves") && rand.nextBoolean()) {
            world.setBlockState(pos, Registries.BLOCK.get(DisastrousConditionsMod.BURNED_LEAVES_ID).getDefaultState());
            BlockPos below = pos.add(0, -1, 0);
            boolean didCast = false;
            BlockState castBlock = world.getBlockState(below);
            while (castBlock.getBlock() == Blocks.AIR) {
                below = below.add(0, -1, 0);
                didCast = true;
                castBlock = world.getBlockState(below);
            }
            if (didCast) {
                if (castBlock.getBlock() == Registries.BLOCK.get(DisastrousConditionsMod.ASH_LAYER)) {
                    world.setBlockState(below, castBlock.with(LAYERS, castBlock.get(LAYERS) + 1));
                } else if (castBlock.isOpaque()){
                    world.setBlockState(below.add(0, 1, 0),
                            Registries.BLOCK.get(DisastrousConditionsMod.ASH_LAYER).getDefaultState());
                }
            }
        } else if (state.getBlock() == Blocks.GRASS_BLOCK || state.getBlock() == Blocks.DIRT_PATH) {
            if (rand.nextInt(4) < 3) {
                world.setBlockState(pos, Registries.BLOCK.get(
                        DisastrousConditionsMod.BURNED_GRASS_BLOCK_ID).getDefaultState());
            } else {
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            }
            BlockPos above = pos.mutableCopy().add(0 ,1, 0);
            BlockState aboveState = world.getBlockState(above);
            if (aboveState.getBlock() == Blocks.GRASS) {
                world.setBlockState(above,
                        Registries.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID).getDefaultState());
            } else if (aboveState.getBlock() instanceof FlowerBlock) {
                world.setBlockState(above,
                        Registries.BLOCK.get(DisastrousConditionsMod.BURNED_FLOWER_ID).getDefaultState());
            } else if (aboveState.getBlock() == Blocks.AIR && rand.nextBoolean()) {
                world.setBlockState(above, Registries.BLOCK.get(DisastrousConditionsMod.ASH_LAYER).getDefaultState());
            }
        } else if (state.getBlock() == Blocks.GRASS) {
            world.setBlockState(pos, Registries.BLOCK.get(DisastrousConditionsMod.BURNED_GRASS_ID).getDefaultState());
            emplaceBlockBelow(world, pos);
        } else if (state.getBlock() instanceof FlowerBlock) {
            world.setBlockState(pos, Registries.BLOCK.get(DisastrousConditionsMod.BURNED_FLOWER_ID).getDefaultState());
            emplaceBlockBelow(world, pos);
        } else if (state.getBlock() instanceof CropBlock) {
            world.setBlockState(pos, Registries.BLOCK.get(DisastrousConditionsMod.BURNED_CROP_ID).getDefaultState());
            emplaceBlockBelow(world, pos);
        }
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

    private static void emplaceBlockBelow(World world, BlockPos pos) {
        BlockPos below = pos.mutableCopy().add(0, -1, 0);
        if (world.getBlockState(below).getBlock() == Blocks.GRASS_BLOCK) {
            world.setBlockState(below, Registries.BLOCK.get(
                    DisastrousConditionsMod.BURNED_GRASS_BLOCK_ID).getDefaultState());
        } else {
            world.setBlockState(below, Blocks.DIRT.getDefaultState());
        }
    }
}
