package ca.techgarage.client.entity.model;

import ca.techgarage.BotanicMagic;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public class ModEntityModelLayers {
    public static final ModelLayerLocation SPELL_PROJECTILE = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "spell_projectile"), "main"
    );
}