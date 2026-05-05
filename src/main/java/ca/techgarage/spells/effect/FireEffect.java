package ca.techgarage.spells.effect;

import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.Spell;
import ca.techgarage.entity.ModEntities;
import ca.techgarage.entity.SpellProjectileEntity;
import ca.techgarage.spells.SpellElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FireEffect implements Spell {

    private final float damage;
    private final MagicShape shape;
    public FireEffect(float damage, MagicShape shape) {
        this.damage = damage;
        this.shape = shape;
    }

    @Override
    public void apply(Level level, LivingEntity caster, LivingEntity target) {
        caster.playSound(SoundEvents.FIRECHARGE_USE, 1.0f, 1.0f);

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
            spawnProjectile(level, caster);
        } else if (target == caster || shape == MagicShape.BUFF_SELF) {
            caster.addEffect(
                    new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 25, 1, true, true, true),
                    target
            );
        } else {
            if (level instanceof ServerLevel serverLevel) {
                if (target != null) {
                    target.hurt(
                            level.damageSources().playerAttack((Player) caster),
                            damage
                    );
                }
            }
        }
    }

    private void spawnProjectile(Level level, LivingEntity caster) {
        SpellProjectileEntity projectile =
                new SpellProjectileEntity(ModEntities.SPELL_PROJECTILE, level, 0xFF5500, ParticleTypes.FLAME);
        projectile.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
        projectile.setOnHitEffect((lvl, c, target) -> {
            if (target == null || !(lvl instanceof ServerLevel sl)) return;
            target.hurtServer(sl, lvl.damageSources().onFire(), damage);
            target.igniteForSeconds(3);
        }, caster);
        projectile.setDeltaMovement(caster.getLookAngle().scale(0.5));
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

                if (Math.random() < 0.15) continue;

                serverLevel.sendParticles(
                        ParticleTypes.FLAME,
                        x + wobbleX,
                        baseY + y,
                        z + wobbleZ,
                        1,
                        0.02, 0.02, 0.02,
                        0.0
                );
            }

            if (Math.random() < 0.08) {
                serverLevel.sendParticles(
                        ParticleTypes.LAVA,
                        x, baseY + 0.5, z,
                        3,
                        0.2, 0.4, 0.2,
                        0.01
                );
            }

            var box = new net.minecraft.world.phys.AABB(
                    x - radius, baseY, z - radius,
                    x + radius, baseY + baseHeight, z + radius
            );

            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, box)) {
                if (entity == caster) continue;

                entity.hurt(level.damageSources().onFire(), damage);
                entity.igniteForSeconds(2);
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
                        ParticleTypes.FLAME,
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

                target.hurt(level.damageSources().onFire(), damage);
                target.igniteForSeconds(3);
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

                entity.igniteForSeconds(3);
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
                        ParticleTypes.FLAME,
                        x, y, z,
                        5,
                        0, 0, 0,
                        0
                );
            }
        }
    }

    public static class FireProjectileHitSpell implements Spell {
        private final float damage;

        public FireProjectileHitSpell(float damage) {
            this.damage = damage;
        }

        @Override
        public void apply(Level level, LivingEntity caster, LivingEntity target) {
            if (!(level instanceof ServerLevel serverLevel)) return;
            if (target == null) return;

            target.hurtServer(
                    serverLevel,
                    level.damageSources().playerAttack((Player) caster),
                    damage
            );
            target.igniteForSeconds(3);
        }
    }

}

