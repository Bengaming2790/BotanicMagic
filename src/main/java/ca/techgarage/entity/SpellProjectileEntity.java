package ca.techgarage.entity;

import ca.techgarage.spells.Spell;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.List;

public class SpellProjectileEntity extends Entity {

    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(SpellProjectileEntity.class, EntityDataSerializers.INT);

    private Spell effect;
    private LivingEntity caster;
    private int lifetimeTicks = 0;
    private static final int MAX_LIFETIME = 100;
    private boolean sonicBoomOnHit = false;
    public ParticleOptions particle;

    public SpellProjectileEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SpellProjectileEntity(EntityType<?> type, Level level, int color, ParticleOptions particle) {
        this(type, level);
        setColor(color);
        this.particle = particle;
    }

    public void setEffect(Spell effect, LivingEntity caster) {
        this.effect = effect;
        this.caster = caster;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COLOR, 0xFFFFFF);
    }

    public void setColor(int color) {
        this.entityData.set(COLOR, color);
    }

    public int getColor() {
        return this.entityData.get(COLOR);
    }
    public void setSonicBoomOnHit(boolean value) {
        this.sonicBoomOnHit = value;
    }
    @Override
    public void tick() {
        super.tick();
        lifetimeTicks++;

        if (lifetimeTicks > MAX_LIFETIME) {
            this.discard();
            return;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.horizontalCollision || this.verticalCollision) {
            this.discard();
            return;
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (particle instanceof net.minecraft.core.particles.DustParticleOptions dust) {
                serverLevel.sendParticles(
                        dust,
                        this.getX(), this.getY(), this.getZ(),
                        10,
                        0.05, 0.05, 0.05,
                        0.0
                    );
                } else {
                    serverLevel.sendParticles(
                            particle,
                            this.getX(), this.getY(), this.getZ(),
                            10,
                            0.1, 0.1, 0.1,
                            0.01
                    );
                }

            if (effect != null) {
                checkHit();
            }
        }
    }

    private void checkHit() {
        AABB hitbox = new AABB(
                getX() - 0.3, getY() - 0.3, getZ() - 0.3,
                getX() + 0.3, getY() + 0.3, getZ() + 0.3
        );

        List<LivingEntity> targets = this.level().getEntitiesOfClass(
                LivingEntity.class,
                hitbox,
                e -> e != caster && e.isAlive()
        );

        targets.stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                .ifPresent(target -> {
                    effect.apply(this.level(), caster, target);

                    if (sonicBoomOnHit && this.level().getRandom().nextInt(200) == 0) {
                        target.hurt(this.level().damageSources().sonicBoom(caster), 30.0F);
                    }

                    this.discard();
                });
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {}

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {}
}