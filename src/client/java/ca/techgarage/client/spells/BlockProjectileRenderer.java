package ca.techgarage.client.spells;

import ca.techgarage.client.spells.BlockProjectileRenderState;
import ca.techgarage.entity.BlockProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

public class BlockProjectileRenderer extends EntityRenderer<BlockProjectileEntity, BlockProjectileRenderState> {

    private final BlockModelResolver blockModelResolver;
    private final BlockModelRenderState blockModelRenderState = new BlockModelRenderState();

    public BlockProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public BlockProjectileRenderState createRenderState() {
        return new BlockProjectileRenderState();
    }

    @Override
    public void extractRenderState(BlockProjectileEntity entity, BlockProjectileRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        var visual = entity.getVisualBlockState();
        state.blockState = visual != null ? visual : Blocks.DIRT.defaultBlockState();
    }

    @Override
    public void submit(BlockProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);

        blockModelRenderState.clear();
        blockModelResolver.update(blockModelRenderState, state.blockState, BlockDisplayContext.create());

        if (!blockModelRenderState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(
                    state.x - camera.pos.x,
                    state.y - camera.pos.y,
                    state.z - camera.pos.z
            );
            poseStack.scale(0.75f, 0.75f, 0.75f);
            poseStack.translate(-0.5, -0.5, -0.5);

            blockModelRenderState.submit(
                    poseStack,
                    collector,
                    state.lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0
            );

            poseStack.popPose();
        }
    }

}