package pepjebs.disastrousconditions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pepjebs.disastrousconditions.entity.ExtinguisherFoamEntity;

import java.util.List;
import java.util.Random;

public class DisastrousConditionsMod implements ModInitializer {

    public static final String MOD_ID = "disastrous_conditions";

    public static Identifier FIRE_HELMET = new Identifier(MOD_ID, "fire_helmet");
    public static Identifier SOOT = new Identifier(MOD_ID, "soot");
    public static Identifier ASH_LAYER = new Identifier(MOD_ID, "ash");
    public static Identifier ASH_BLOCK = new Identifier(MOD_ID, "ash_block");
    public static Identifier BURNED_LOG_ID = new Identifier(MOD_ID, "burned_log");
    public static Identifier BURNED_STRIPPED_LOG_ID = new Identifier(MOD_ID, "burned_stripped_log");
    public static Identifier BURNED_PLANKS_ID = new Identifier(MOD_ID, "burned_planks");
    public static Identifier BURNED_LEAVES_ID = new Identifier(MOD_ID, "burned_leaves");
    public static Identifier BURNED_FLOWER_ID = new Identifier(MOD_ID, "burned_flower");
    public static Identifier BURNED_GRASS_ID = new Identifier(MOD_ID, "burned_grass");
    public static Identifier BURNED_GRASS_BLOCK_ID = new Identifier(MOD_ID, "burned_grass_block");
    public static Identifier EXTINGUISHER_ID = new Identifier(MOD_ID, "extinguisher");
    private static final Identifier EXTINGUISHER_RUNNING_SOUND_ID = new Identifier(MOD_ID, "extinguisher_running");
    public static SoundEvent EXTINGUISHER_RUNNING_SOUND_EVENT = new SoundEvent(EXTINGUISHER_RUNNING_SOUND_ID);
    public static final DefaultParticleType EXTINGUISHER_FOAM_PARTICLE = FabricParticleTypes.simple();
    public static final Identifier EXTINGUISHER_FOAM_PARTICLE_ID = new Identifier(MOD_ID, "extinguisher_foam");
    public static final Identifier EXTINGUISHER_FOAM_ID = new Identifier(MOD_ID, "extinguisher_foam");
    public static final Item EXTINGUISHER_FOAM_ITEM = Registry.register(
            Registry.ITEM, EXTINGUISHER_FOAM_ID, new Item(new Item.Settings().group(ItemGroup.MISC)));
    public static final EntityType<ExtinguisherFoamEntity> EXTINGUISHER_FOAM = Registry.register(
            Registry.ENTITY_TYPE,
            EXTINGUISHER_FOAM_ID,
            FabricEntityTypeBuilder.<ExtinguisherFoamEntity>create(SpawnGroup.MISC, ExtinguisherFoamEntity::new)
                    .fireImmune().build()
    );

    @Override
    public void onInitialize() {
        // Set Grass as flammable
        FlammableBlockRegistry.getDefaultInstance().add(Blocks.GRASS_BLOCK, 5, 20);

        // Register soot
        registerBlock(SOOT, new VineBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).sounds(BlockSoundGroup.VINE)
                .nonOpaque()));

        // Register fire helmet
        Registry.register(Registry.ITEM, FIRE_HELMET,
                new Item(new FabricItemSettings().equipmentSlot(item -> EquipmentSlot.HEAD).group(ItemGroup.MISC)){
                    @Override
                    public void appendTooltip(
                            ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                        super.appendTooltip(stack, world, tooltip, context);
                        tooltip.add(new TranslatableText("item.modifiers.head").formatted(Formatting.GRAY));
                        tooltip.add(new TranslatableText("attribute.disastrous_conditions.fire_helmet.modifier")
                                .formatted(Formatting.BLUE));
                    }
                });

        // Register extinguisher
        Registry.register(Registry.SOUND_EVENT, EXTINGUISHER_RUNNING_SOUND_ID, EXTINGUISHER_RUNNING_SOUND_EVENT);
        Registry.register(Registry.PARTICLE_TYPE, EXTINGUISHER_FOAM_PARTICLE_ID, EXTINGUISHER_FOAM_PARTICLE);
        Registry.register(Registry.ITEM, EXTINGUISHER_ID, new Item(new Item.Settings().group(ItemGroup.MISC)) {
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
        // TODO: Make this programmatic
        registerBlock(
                ASH_BLOCK,
                new Block(FabricBlockSettings.of(Material.SOLID_ORGANIC).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.SAND))
        );
        registerBlock(
                ASH_LAYER,
                new SnowBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.SAND)){
                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {}
                }
        );

        registerBlock(
                BURNED_LOG_ID,
                new PillarBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_STRIPPED_LOG_ID,
                new PillarBlock(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_PLANKS_ID,
                new Block(FabricBlockSettings.of(Material.WOOD).hardness(2.0f).requiresTool()
                        .sounds(BlockSoundGroup.WOOD))
        );

        registerBlock(
                BURNED_LEAVES_ID,
                new Block(FabricBlockSettings.of(Material.LEAVES).nonOpaque().ticksRandomly()
                        .sounds(BlockSoundGroup.BAMBOO)) {
                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        int decomposeBound = 30 + Math.abs(pos.getX() + pos.getZ() % 150);
                        if (random.nextInt(0, decomposeBound) == 0) {
                            world.removeBlock(pos, false);
                        }
                    }
                }
        );

        registerBlock(
                BURNED_FLOWER_ID,
                new PlantBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)) {
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
                new PlantBlock(FabricBlockSettings.of(Material.PLANT).noCollision().breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)) {
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
                new Block(FabricBlockSettings.of(Material.PLANT).ticksRandomly().sounds(BlockSoundGroup.GRASS)) {
                    @Override
                    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                        int decomposeBound = 100 + Math.abs(pos.getX() + pos.getZ() % 150);
                        if (random.nextInt(0, decomposeBound) == 0) {
                            BlockState toSet = random.nextBoolean() ? Blocks.DIRT.getDefaultState()
                                    : Blocks.GRASS_BLOCK.getDefaultState();
                            world.setBlockState(pos, toSet);
                            if (random.nextInt(0, 8) == 0) {
                                BlockPos above = pos.mutableCopy().add(0, 1, 0);
                                int type = random.nextInt(0, 5);
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

    private static void registerBlock(
            Identifier id,
            Block block
    ) {
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
    }
}
