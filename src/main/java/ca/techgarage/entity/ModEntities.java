package ca.techgarage.entity;

import ca.techgarage.BotanicMagic;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;

public class ModEntities {

    public static final ResourceKey<EntityType<?>> SPELL_PROJECTILE_KEY = ResourceKey.create(
            BuiltInRegistries.ENTITY_TYPE.key(),
            Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "spell_projectile")
    );
    public static final ResourceKey<EntityType<?>> BLOCK_PROJECTILE_KEY = ResourceKey.create(
            BuiltInRegistries.ENTITY_TYPE.key(),
            Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "block_projectile")
    );
    public static EntityType<SpellProjectileEntity> SPELL_PROJECTILE;
    public static EntityType<BlockProjectileEntity> BLOCK_PROJECTILE;
    public static void register() {
        SPELL_PROJECTILE = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                SPELL_PROJECTILE_KEY,
                EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(4)
                        .updateInterval(10)
                        .build(SPELL_PROJECTILE_KEY)
        );
        BLOCK_PROJECTILE = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                BLOCK_PROJECTILE_KEY,
                EntityType.Builder.<BlockProjectileEntity>of(BlockProjectileEntity::new, MobCategory.MISC)
                        .sized(0.5f, 0.5f)
                        .clientTrackingRange(4)
                        .updateInterval(10)
                        .build(BLOCK_PROJECTILE_KEY)
        );
    }
}