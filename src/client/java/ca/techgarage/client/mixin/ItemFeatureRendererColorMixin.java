package ca.techgarage.client.mixin;

import ca.techgarage.client.render.ElementRenderData;
import ca.techgarage.ElementColors;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFeatureRenderer.class)
public class ItemFeatureRendererColorMixin {

    @Inject(method = "getLayerColorSafe", at = @At("HEAD"), cancellable = true)
    private static void techgarage$glintColor(int[] layers, int layer, CallbackInfoReturnable<Integer> cir) {

        var element = ElementRenderData.get();
        if (element == null) return;

        int rgb = ElementColors.getRGB(element);

        cir.setReturnValue(rgb);
    }
}
