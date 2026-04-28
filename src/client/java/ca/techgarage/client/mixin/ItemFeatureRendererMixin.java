package ca.techgarage.client.mixin;

import ca.techgarage.ModDataComponents;
import ca.techgarage.client.render.ElementRenderData;
import ca.techgarage.items.SpellFlowerItem;
import ca.techgarage.spells.MagicEssenceData;
import ca.techgarage.spells.SpellElement;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFeatureRenderer.class)
public class ItemFeatureRendererMixin {

    @Inject(method = "getFoilBuffer", at = @At("HEAD"), cancellable = true)
    private static void modifyGlint(
            MultiBufferSource bufferSource,
            RenderType renderType,
            boolean sheeted,
            boolean hasFoil,
            CallbackInfoReturnable<VertexConsumer> cir
    ) {
        if (!hasFoil) return;


        RenderType customGlint = RenderTypes.glint();

        cir.setReturnValue(
                VertexMultiConsumer.create(
                        bufferSource.getBuffer(customGlint),
                        bufferSource.getBuffer(renderType)
                )
        );
    }
}