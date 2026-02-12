package betterfoliage.mixin;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.feature.RenderingHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RenderChunk.class)
public abstract class RenderChunkMixin {
	
	@WrapOperation(
			method = "rebuildChunk",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z")
	)
	private boolean betterfoliage_vanillaRenderChunk_rebuildChunk_renderBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRenderer, Operation<Boolean> original, @Local BlockRenderLayer layer) {
		if(!ForgeConfigHandler.GLOBAL.enabled) return original.call(dispatcher, state, pos, blockAccess, worldRenderer);
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.BLOCK_CONTEXT.set(blockAccess, pos, state);
		
		boolean renderCutout;
		boolean renderPrimary;
		if(ForgeConfigHandler.GLOBAL.renderLayerAdjustments) {
			//Previous render check fixes proper cutout layers so specific check is not needed
			renderCutout = layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.CUTOUT_MIPPED;
			//Render the base block if it normally renders non-cutout, or renders in either of the cutouts overriden by mip check
			renderPrimary = !renderCutout || state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT) || state.getBlock().canRenderInLayer(state, BlockRenderLayer.CUTOUT_MIPPED);
		}
		else {
			renderPrimary = state.getBlock().canRenderInLayer(state, layer);
			renderCutout = layer == BlockRenderLayer.CUTOUT;
		}
		
		for(RenderingHandler renderer : RenderingHandler.RENDERERS) {
			if(renderer.isEligible(modelRenderer.BLOCK_CONTEXT, renderPrimary, renderCutout)) {
				return renderer.render(modelRenderer.BLOCK_CONTEXT, dispatcher, worldRenderer, layer, renderPrimary, renderCutout);
			}
		}
		
		return renderPrimary ? original.call(dispatcher, state, pos, blockAccess, worldRenderer) : false;
	}
}