package ca.techgarage.client.spells;

import ca.techgarage.entity.SpellProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SpellProjectileRenderer extends EntityRenderer<SpellProjectileEntity, EntityRenderState> {

    public SpellProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}