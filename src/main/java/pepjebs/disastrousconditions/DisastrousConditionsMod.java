package pepjebs.disastrousconditions;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import pepjebs.disastrousconditions.config.DisastrousConditionsConfig;
import pepjebs.disastrousconditions.entity.ExtinguisherFoamEntity;

import java.util.List;

public class DisastrousConditionsMod implements ModInitializer {

    public static final String MOD_ID = "disastrous_conditions";
    public static final DisastrousConditionsConfig CONFIG =
            AutoConfig.register(DisastrousConditionsConfig.class, JanksonConfigSerializer::new).getConfig();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Identifier FIRE_HELMET = new Identifier(MOD_ID, "fire_helmet");
    public static Identifier SOOT = new Identifier(MOD_ID, "soot");
    public static Identifier SOOT_LAYER = new Identifier(MOD_ID, "soot_layer");
    public static Identifier SOOT_BLOCK = new Identifier(MOD_ID, "soot_block");
    public static Identifier ASH = new Identifier(MOD_ID, "ash");
    public static Identifier ASH_LAYER = new Identifier(MOD_ID, "ash_layer");
    public static Identifier ASH_BLOCK = new Identifier(MOD_ID, "ash_block");
    public static Identifier BURNED_CROP_ID = new Identifier(MOD_ID, "burned_crop");
    public static Identifier BURNED_LOG_ID = new Identifier(MOD_ID, "burned_log");
    public static Identifier BURNED_STRIPPED_LOG_ID = new Identifier(MOD_ID, "burned_stripped_log");
    public static Identifier BURNED_PLANKS_ID = new Identifier(MOD_ID, "burned_planks");
    public static Identifier BURNED_PLANK_STAIRS_ID = new Identifier(MOD_ID, "burned_plank_stairs");
    public static Identifier BURNED_LEAVES_ID = new Identifier(MOD_ID, "burned_leaves");
    public static Identifier BURNED_FLOWER_ID = new Identifier(MOD_ID, "burned_flower");
    public static Identifier BURNED_GRASS_ID = new Identifier(MOD_ID, "burned_grass");
    public static Identifier BURNED_GRASS_BLOCK_ID = new Identifier(MOD_ID, "burned_grass_block");
    public static Identifier EXTINGUISHER_ID = new Identifier(MOD_ID, "extinguisher");
    private static final Identifier EXTINGUISHER_RUNNING_SOUND_ID = new Identifier(MOD_ID, "extinguisher_running");
    public static SoundEvent EXTINGUISHER_RUNNING_SOUND_EVENT = SoundEvent.of(EXTINGUISHER_RUNNING_SOUND_ID);
    public static final DefaultParticleType EXTINGUISHER_FOAM_PARTICLE = FabricParticleTypes.simple();
    public static final Identifier EXTINGUISHER_FOAM_PARTICLE_ID = new Identifier(MOD_ID, "extinguisher_foam");
    public static final Identifier EXTINGUISHER_FOAM_ID = new Identifier(MOD_ID, "extinguisher_foam");
    public static final Item EXTINGUISHER_FOAM_ITEM = Registry.register(
            Registries.ITEM, EXTINGUISHER_FOAM_ID, new Item(new Item.Settings()));
    public static final EntityType<ExtinguisherFoamEntity> EXTINGUISHER_FOAM = Registry.register(
            Registries.ENTITY_TYPE,
            EXTINGUISHER_FOAM_ID,
            FabricEntityTypeBuilder.<ExtinguisherFoamEntity>create(SpawnGroup.MISC, ExtinguisherFoamEntity::new)
                    .fireImmune().build()
    );

    @Override
    public void onInitialize() {
        // Set Grass as flammable
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.GRASS_BLOCK, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.DIRT_PATH, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.WHEAT, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.POTATOES, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.CARROTS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.BEETROOTS, 5, 20);

        // Register soot
        Registry.register(Registries.ITEM, SOOT, new Item(new Item.Settings()));
        registerBlock(SOOT_LAYER, new VineBlock(FabricBlockSettings.create().sounds(BlockSoundGroup.VINE)
                .nonOpaque().noCollision().breakInstantly().notSolid().pistonBehavior(PistonBehavior.DESTROY)));
        Block b = registerBlock(
                SOOT_BLOCK,
                new FallingBlock(FabricBlockSettings.create().hardness(0.6F)
                        .sounds(BlockSoundGroup.VINE)) {
                    @Override
                    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
                        super.onDestroyedByExplosion(world, pos, explosion);
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                        world.createExplosion(null, pos.getX(),
                                pos.getY(), pos.getZ(), 2.0F, World.ExplosionSourceType.BLOCK);
                    }
                }
        );
        FlammableBlockRegistry.getDefaultInstance().add(b, 60, 20);

        // Register fire helmet
        Registry.register(Registries.ITEM, FIRE_HELMET,
                new Item(new FabricItemSettings().equipmentSlot(item -> EquipmentSlot.HEAD)){
                    @Override
                    public void appendTooltip(
                            ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        super.appendTooltip(stack, world, tooltip, context);
                        tooltip.add(Text.translatable("item.modifiers.head").formatted(Formatting.GRAY));
                        tooltip.add(
                                Text.literal(DisastrousConditionsMod.CONFIG.fireTickDamageReductionPct + "% ")
                                        .append(Text.translatable(
                                                "attribute.disastrous_conditions.fire_helmet.modifier"))
                                        .formatted(Formatting.BLUE)
                                );
                    }
                });

        // Register extinguisher
        Registry.register(Registries.SOUND_EVENT, EXTINGUISHER_RUNNING_SOUND_ID, EXTINGUISHER_RUNNING_SOUND_EVENT);
        Registry.register(Registries.PARTICLE_TYPE, EXTINGUISHER_FOAM_PARTICLE_ID, EXTINGUISHER_FOAM_PARTICLE);
        Registry.register(Registries.ITEM, EXTINGUISHER_ID, new Item(new Item.Settings()) {
            @Override
            public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
                user.getWorld().playSound(null, user.getBlockPos(),
                        EXTINGUISHER_RUNNING_SOUND_EVENT,
                        SoundCategory.PLAYERS, 1.0F, 1.0F);
                for(int i = 0; i < 30; i++) {
                    ExtinguisherFoamEntity e = new ExtinguisherFoamEntity(world, user);
                    e.setPosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
                    e.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 20.0F);
                    world.spawnEntity(e);
                }
                return super.use(world, user, hand);
            }
        });

        // Register burned blocks
        Registry.register(Registries.ITEM, ASH, new Item(new Item.Settings()));
        // TODO: Make this programmatic
        Registry.register(Registries.BLOCK, BURNED_CROP_ID,
                new Block(FabricBlockSettings.create().sounds(BlockSoundGroup.CROP)
                        .nonOpaque().noCollision().breakInstantly().replaceable()) {
                    @Override
                    public VoxelShape getOutlineShape(
                            BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                        return Block.createCuboidShape(
                                0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
                    }
                });

        registerBlock(
                ASH_BLOCK,
                new FallingBlock(FabricBlockSettings.create().hardness(0.6F)
                        .sounds(BlockSoundGroup.SAND)) {

                    @Override
                    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        super.scheduledTick(state, world, pos, random);
                        for (int i = -2; i < 3; i++) {
                            for (int j = -2; j < 3; j++) {
                                for (int k = -2; k < 3; k++) {
                                    BlockPos query = pos.mutableCopy().add(i, j, k);
                                    if (world.getBlockState(query).getBlock() == Blocks.FIRE) {
                                        world.setBlockState(query, Blocks.AIR.getDefaultState());
                                        world.playSound(null, query,
                                                SoundEvents.BLOCK_FIRE_EXTINGUISH,
                                                SoundCategory.BLOCKS, 1.0F, 1.0F);
                                        world.syncWorldEvent(2009, query, 0);
                                    }
                                }
                            }
                        }
                    }
                }
        );
        registerBlock(
                ASH_LAYER,
                new SnowBlock(FabricBlockSettings.create().hardness(0.1F)
                        .sounds(BlockSoundGroup.SAND).replaceable()) {

                    @Override
                    public boolean hasRandomTicks(BlockState state) {
                        return true;
                    }

                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        int decomposeBound = 30 + Math.abs(pos.getX() + pos.getZ() % 150);
                        if (random.nextInt(decomposeBound) == 0) {
                            world.removeBlock(pos, false);
                        }
                    }
                }
        );

        registerBlock(
                BURNED_LOG_ID,
                new PillarBlock(FabricBlockSettings.create().hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_STRIPPED_LOG_ID,
                new PillarBlock(FabricBlockSettings.create().hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_PLANKS_ID,
                new Block(FabricBlockSettings.create().hardness(2.0f)
                        .sounds(BlockSoundGroup.WOOD))
        );
        registerBlock(
                BURNED_PLANK_STAIRS_ID,
                new StairsBlock(
                        Registries.BLOCK.get(BURNED_PLANKS_ID).getDefaultState(),
                        FabricBlockSettings.create().hardness(2.0f)
                                .sounds(BlockSoundGroup.WOOD)));

        registerBlock(
                BURNED_LEAVES_ID,
                new Block(FabricBlockSettings.create().nonOpaque().ticksRandomly()
                        .sounds(BlockSoundGroup.BAMBOO)) {

                    @Override
                    public boolean hasRandomTicks(BlockState state) {
                        return true;
                    }

                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        int decomposeBound = 30 + Math.abs(pos.getX() + pos.getZ() % 150);
                        if (random.nextInt(decomposeBound) == 0) {
                            world.removeBlock(pos, false);
                        }
                    }
                }
        );

        registerBlock(
                BURNED_FLOWER_ID,
                new PlantBlock(FabricBlockSettings.create().noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS).replaceable()) {
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
                new PlantBlock(FabricBlockSettings.create().noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS).replaceable()) {
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
                new Block(FabricBlockSettings.create().ticksRandomly().sounds(BlockSoundGroup.GRASS).hardness(0.6F)) {

                    @Override
                    public boolean hasRandomTicks(BlockState state) {
                        return true;
                    }

                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        int decomposeBound = 100 + Math.abs(pos.getX() + pos.getZ() % 150);
                        if (random.nextInt(decomposeBound) == 0) {
                            BlockState toSet = random.nextBoolean() ? Blocks.DIRT.getDefaultState()
                                    : Blocks.GRASS_BLOCK.getDefaultState();
                            world.setBlockState(pos, toSet);
                            BlockPos above = pos.mutableCopy().add(0, 1, 0);
                            var aboveId = Registries.BLOCK.getId(world.getBlockState(above).getBlock());
                            if (aboveId == DisastrousConditionsMod.BURNED_GRASS_ID ||
                                    aboveId == DisastrousConditionsMod.BURNED_FLOWER_ID) {
                                world.setBlockState(above, Blocks.AIR.getDefaultState());
                            }
                            if (random.nextInt(8) == 0) {
                                int type = random.nextInt(5);
                                if (type < 2) {
                                    world.setBlockState(above, Blocks.GRASS.getDefaultState());
                                } else if (type < 4) {
                                    world.setBlockState(above, Blocks.FERN.getDefaultState());
                                } else {
                                    world.setBlockState(above, Blocks.DANDELION.getDefaultState());
                                }
                            }
                        }
                    }
                }
        );
    }

    private static Block registerBlock(
            Identifier id,
            Block block
    ) {
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }
}
