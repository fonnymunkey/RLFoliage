package betterfoliage.mixin;

import betterfoliage.render.util.MipmapUtil;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextureUtil.class)
public abstract class TextureUtilMixin {
	
	@Redirect(
			method = "generateMipmapData",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureUtil;blendColors(IIIIZ)I")
	)
	private static int betterfoliage_vanillaTextureUtil_generateMipmapData(int c0, int c1, int c2, int c3, boolean a) {
		return MipmapUtil.average(c0, c1, c2, c3);
	}
}