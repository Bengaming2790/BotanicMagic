package ca.techgarage.spells;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface Spell {
    void apply(Level level, LivingEntity caster, LivingEntity target);
}
