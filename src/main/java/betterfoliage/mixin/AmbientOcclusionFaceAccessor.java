package betterfoliage.mixin;

import net.minecraft.client.renderer.BlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModelRenderer.AmbientOcclusionFace.class)
public interface AmbientOcclusionFaceAccessor {
	
	@Invoker(value = "<init>")
	static BlockModelRenderer.AmbientOcclusionFace betterfoliage$callInit(BlockModelRenderer renderer) {
		throw new AssertionError();
	}
}