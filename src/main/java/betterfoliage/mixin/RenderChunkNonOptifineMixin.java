package betterfoliage.mixin;

import betterfoliage.render.feature.RenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderChunk.class)
public abstract class RenderChunkNonOptifineMixin {
	
	@Redirect(
			method = "rebuildChunk",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;canRenderInLayer(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockRenderLayer;)Z", remap = false)
	)
	private boolean betterfoliage_vanillaOptifineRenderChunk_rebuildChunk_canRenderInLayer(Block block, IBlockState state, BlockRenderLayer layer) {
		return RenderingHandler.canRenderBlockInLayer(block, state, layer);
	}
}