package ca.techgarage.spells.effect;

import ca.techgarage.entity.BlockProjectileEntity;
import ca.techgarage.entity.ModEntities;
import ca.techgarage.entity.SpellProjectileEntity;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LightEffect implements Spell {
    private final float damage;
    private final MagicShape shape;
    public LightEffect(float damage, MagicShape shape) {
        this.damage = damage;
        this.shape = shape;
    }

    @Override
    public void apply(Level level, LivingEntity caster, LivingEntity target) {
        caster.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1.0f, 0f);

        if (shape == MagicShape.COLUMN) {
            applyColumn(level, caster);
            return;
        }

        if (shape == MagicShape.CONE) {
            applyCone(level, caster);
            return;
        }
        if (shape == MagicShape.AOE) {
            applyAoeDamage(caster, 5f);
        }
        if (shape == MagicShape.PROJECTILE) {
            if (target == null) {
                spawnProjectile(level, caster);
            } else {
                target.hurt(level.damageSources().playerAttack((Player) caster), damage);
            }
            return;
        }

        if (target == caster || shape == MagicShape.BUFF_SELF) {
            caster.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 25, 3, true, false, true));
            return;
        }

        if (target != null) {
            target.hurt(level.damageSources().playerAttack((Player) caster), damage);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 1, true, true, true));
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 5, 1, true, true, true));
        }
    }

    private void spawnProjectile(Level level, LivingEntity caster) {
        SpellProjectileEntity projectile =
                new SpellProjectileEntity(
                        ModEntities.SPELL_PROJECTILE,
                        level,
                        0xFF5500,
                        new DustParticleOptions(0x000000, 1)
                );

        projectile.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
        projectile.setOnHitEffect((lvl, c, target) -> {
            if (target == null || !(lvl instanceof ServerLevel sl)) return;
            target.hurtServer(sl, lvl.damageSources().playerAttack((Player) c), damage);
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 1, true, true, true));
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 5, 1, true, true, true));
        }, caster);
        Vec3 direction = caster.getLookAngle().scale(0.5);
        projectile.setDeltaMovement(direction);

        level.addFreshEntity(projectile);
    }

    private void applyColumn(Level level, LivingEntity caster) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 look = caster.getLookAngle().normalize();

        double maxDistance = 10.0;
        double step = 0.25;

        double baseHeight = 5.5;
        double radius = 0.6;

        for (double d = 0; d <= maxDistance; d += step) {

            Vec3 forward = caster.getEyePosition().add(look.scale(d));

            BlockPos ground = BlockPos.containing(forward);

            while (ground.getY() > level.getMinY()
                    && level.getBlockState(ground).isAir()) {
                ground = ground.below();
            }

            double x = ground.getX() + 0.5;
            double z = ground.getZ() + 0.5;
            double baseY = ground.getY() + 1;

            double wobbleX = (Math.random() - 0.5) * 0.15;
            double wobbleZ = (Math.random() - 0.5) * 0.15;

            double height = baseHeight + Math.random() * 0.6;

            for (double y = 0; y <= height; y += 0.4) {
                serverLevel.sendParticles(
                        ParticleTypes.WAX_OFF,
                        x + wobbleX,
                        baseY + y,
                        z + wobbleZ,
                        1,
                        0.02, 0.02, 0.02,
                        0.0
                );
            }

            var box = new net.minecraft.world.phys.AABB(
                    x - radius, baseY, z - radius,
                    x + radius, baseY + baseHeight, z + radius
            );

            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
                if (entity == caster) continue;
                if (level.getRandom().nextInt(200) == 0) {
                    entity.hurt(level.damageSources().sonicBoom(caster), 30);
                }
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 1, true, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 5, 1, true, true, true));
                entity.hurt(level.damageSources().sonicBoom(caster), damage);

            }
        }
    }
    private void applyCone(Level level, LivingEntity caster) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 origin = caster.getEyePosition();
        Vec3 look = caster.getLookAngle().normalize();

        double maxDistance = 8.0;
        double maxRadius = 5.0;
        double step = 0.5;

        for (double d = 0; d <= maxDistance; d += step) {

            Vec3 center = origin.add(look.scale(d));

            double radius = (d / maxDistance) * maxRadius;

            for (int i = 0; i < 8; i++) {
                double angle = Math.random() * Math.PI * 2;

                double rx = Math.cos(angle) * radius;
                double rz = Math.sin(angle) * radius;

                serverLevel.sendParticles(
                        ParticleTypes.WAX_OFF,
                        center.x + rx,
                        center.y + (Math.random() - 0.5) * radius,
                        center.z + rz,
                        15,
                        0, 0, 0,
                        0
                );
            }

            var box = new net.minecraft.world.phys.AABB(
                    center.x - radius,
                    center.y - radius,
                    center.z - radius,
                    center.x + radius,
                    center.y + radius,
                    center.z + radius
            );

            for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
                if (target == caster) continue;
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 1, true, true, true));
                target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 5, 1, true, true, true));
                if (level.getRandom().nextInt(200) == 0) {
                    target.hurt(level.damageSources().playerAttack((Player) caster), 30);
                }

                target.hurt(level.damageSources().playerAttack((Player) caster), damage);
            }
        }
    }

    private void applyAoeDamage(LivingEntity attacker, float radius) {
        Level world = attacker.level();

        AABB box = new AABB(
                attacker.getX() - radius, attacker.getY() - radius, attacker.getZ() - radius,
                attacker.getX() + radius, attacker.getY() + radius, attacker.getZ() + radius
        );

        List<LivingEntity> entities = world.getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e != attacker && e.isAlive()
        );

        for (LivingEntity entity : entities) {
            if (entity.distanceToSqr(attacker) <= radius * radius) {

                entity.hurtServer(
                        (ServerLevel) world,
                        world.damageSources().playerAttack((Player) attacker),
                        damage
                );
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 5, 1, true, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 5, 1, true, true, true));
            }
        }

        if (world instanceof ServerLevel serverLevel) {
            int points = 480;

            for (int i = 0; i < points; i++) {

                double theta = serverLevel.getRandom().nextDouble() * Math.PI * 2.0;
                double phi = Math.acos(2.0 * serverLevel.getRandom().nextDouble() - 1.0);

                double x = attacker.getX() + radius * Math.sin(phi) * Math.cos(theta);
                double y = attacker.getY() + 1.0 + radius * Math.cos(phi);
                double z = attacker.getZ() + radius * Math.sin(phi) * Math.sin(theta);

                serverLevel.sendParticles(
                        ParticleTypes.WAX_OFF,
                        x, y, z,
                        5,
                        0, 0, 0,
                        0
                );

            }
        }
    }
}
