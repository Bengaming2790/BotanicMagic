package ca.techgarage.spells.effect;

import ca.techgarage.entity.BlockProjectileEntity;
import ca.techgarage.entity.ModEntities;
import ca.techgarage.entity.SpellProjectileEntity;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EarthEffect implements Spell {
    private final float damage;
    private final MagicShape shape;
    public EarthEffect(float damage, MagicShape shape) {
        this.damage = damage;
        this.shape = shape;
    }

    @Override
    public void apply(Level level, LivingEntity caster, LivingEntity target) {

        if (shape == MagicShape.COLUMN) {
            applyColumn(level, caster);
            return;
        }
        if (shape == MagicShape.CONE) {
            applyCone(level, caster);
        }
        if (shape == MagicShape.PROJECTILE) {
            spawnProjectile(level, caster);
        } else if (target == caster || shape == MagicShape.BUFF_SELF) {
            caster.addEffect(
                    new MobEffectInstance(MobEffects.REGENERATION, 20 * 3, 1, true, true, true),
                    target
            );
        } else {
            if (level instanceof ServerLevel serverLevel) {
                target.hurt(level.damageSources().onFire(), damage);
                target.igniteForSeconds(5);
            }
        }
    }

    private void spawnProjectile(Level level, LivingEntity caster) {
        Vec3 look = caster.getLookAngle().normalize();
        Vec3 eyePos = caster.getEyePosition();

        double maxDistance = 5.0;
        double step = 0.25;

        BlockPos targetPos = null;
        BlockState foundState = null;

        for (double d = 1.0; d <= maxDistance; d += step) {
            Vec3 checkPos = eyePos.add(look.scale(d));
            BlockPos checkBlock = BlockPos.containing(checkPos);
            BlockState state = level.getBlockState(checkBlock);

            if (!state.isAir() && state.getFluidState().isEmpty()) {
                targetPos = checkBlock;
                foundState = state;
                break;
            }
        }

        if (targetPos == null) {
            BlockPos ground = BlockPos.containing(caster.getX(), caster.getY() - 1, caster.getZ());
            BlockState state = level.getBlockState(ground);
            if (!state.isAir()) {
                targetPos = ground;
                foundState = state;
            }
        }

        if (targetPos == null || foundState == null) return;

        BlockProjectileEntity projectile = new BlockProjectileEntity(
                ModEntities.BLOCK_PROJECTILE,
                level,
                caster
        );
        projectile.setBlockState(foundState);
        projectile.setOriginBlockPos(targetPos);


        if (!(caster instanceof Player player && player.gameMode().equals(GameType.ADVENTURE))) {
            level.removeBlock(targetPos, false);
        }

        projectile.setPos(caster.getX(), caster.getEyeY(), caster.getZ());
        projectile.setDeltaMovement(look.scale(1.2));

        level.addFreshEntity(projectile);
    }

    private void applyColumn(Level level, LivingEntity caster) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Vec3 look = caster.getLookAngle().normalize();

        double maxDistance = 8.0;
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
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
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
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()),
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
                        new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()),
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
}
