package ca.techgarage.client.spells;


import ca.techgarage.client.spells.SpellProjectileRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.ARGB;

public class SpellProjectileModel extends EntityModel<SpellProjectileRenderState> {

    private final ModelPart root;

    public SpellProjectileModel(ModelPart root) {
        super(root);
        this.root = root;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition rootPart = mesh.getRoot();
        rootPart.addOrReplaceChild("cube",
                CubeListBuilder.create().addBox(-3, -3, -3, 6, 6, 6),
                PartPose.offset(0, 0, 0)
        );
        return LayerDefinition.create(mesh, 32, 16);
    }

    @Override
    public void setupAnim(SpellProjectileRenderState state) {
    }
}
