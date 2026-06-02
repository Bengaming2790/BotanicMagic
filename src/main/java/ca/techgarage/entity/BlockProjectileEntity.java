package ca.techgarage.entity;

import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class BlockProjectileEntity extends ThrowableProjectile {

    private BlockState blockState;
    private float damage = 4.0f;
    private BlockPos originPos;
    private Display.BlockDisplay display;
    public static ArrayList<Block> deniedBlocks = new ArrayList<>();

    public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> type, Level level) {
        super(type, level);
    }
    public BlockProjectileEntity(EntityType<? extends BlockProjectileEntity> type, Level level, LivingEntity owner) {
        super(type, owner.getX(), owner.getEyeY() - 0.1, owner.getZ(), level);
        this.setOwner(owner);
    }
    private void createDisplay() {
        if (!(level() instanceof ServerLevel serverLevel)) return;

        display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level());
        if (display == null) return;

        display.setBlockState(blockState != null ? blockState : Blocks.DIRT.defaultBlockState());

        display.setPos(this.getX(), this.getY() - 0.5, this.getZ());

        serverLevel.addFreshEntity(display);
    }
    private static final EntityDataAccessor<String> BLOCK_ID =
            SynchedEntityData.defineId(BlockProjectileEntity.class, EntityDataSerializers.STRING);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(BLOCK_ID, "minecraft:dirt");
    }

    public void setBlockState(BlockState state) {
        this.blockState = state;
        String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .getKey(state.getBlock())
                .toString();
        this.entityData.set(BLOCK_ID, id);
    }

    public BlockState getVisualBlockState() {
        String id = this.entityData.get(BLOCK_ID);
        return BuiltInRegistries.BLOCK
                .getOptional(Identifier.parse(id))
                .map(Block::defaultBlockState)
                .orElse(Blocks.DIRT.defaultBlockState());
    }
    public void setOriginBlockPos(BlockPos pos) {
        this.originPos = pos;
    }
    @Override
    public void tick() {
        super.tick();

        if (display == null && !level().isClientSide()) {
            createDisplay();
            display.setTransformation(
                    new Transformation(
                            new Vector3f(-0.5f, -0.5f, -0.5f),
                            new Quaternionf(),
                            new Vector3f(1f, 1f, 1f),
                            new Quaternionf()
                    )
            );
        }

        if (display != null) {
            display.setPos(this.getX(), this.getY(), this.getZ());
        }

        if (level() instanceof ServerLevel serverLevel && blockState != null) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    2,
                    0.05, 0.05, 0.05,
                    0.01
            );
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        Entity entity = EntityReference.get(owner, this.level(), Entity.class);
        if (!level().isClientSide() && blockState != null) {

            BlockPos placePos = BlockPos.containing(result.getLocation());
            if (entity instanceof Player player && !player.gameMode().equals(GameType.ADVENTURE) && !deniedBlocks.contains(blockState.getBlock())) {
                if (level().getBlockState(placePos).canBeReplaced()) {
                    level().setBlock(placePos, blockState, 3);
                } else {
                    level().setBlock(placePos.above(), blockState, 3);
                }
            }


            if (level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                        result.getLocation().x,
                        result.getLocation().y,
                        result.getLocation().z,
                        20,
                        0.2, 0.2, 0.2,
                        0.05
                );
            }

            if (display != null) {
                display.discard();
            }

            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (!level().isClientSide() && result.getEntity() instanceof LivingEntity target) {
            target.hurt(this.damageSources().thrown(this, this.getOwner()), damage);
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }


    public void setDamage(float damage) {
        this.damage = damage;
    }
}