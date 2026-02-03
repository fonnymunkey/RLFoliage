package betterfoliage.mixin;

import betterfoliage.render.feature.RenderingHandler;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RenderChunk.class)
public abstract class RenderChunkOptifineMixin {
	
	@Dynamic
	@ModifyVariable(
			method = "rebuildChunk",
			at = @At("STORE"),
			name = "canRenderInLayer"
	)
	private boolean betterfoliage_vanillaNonOptifineRenderChunk_rebuildChunk_canRenderInLayer(boolean original, @Local Block block, @Local IBlockState state, @Local BlockRenderLayer layer) {
		return RenderingHandler.canRenderBlockInLayer(block, state, layer);
	}
}