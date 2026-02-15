package betterfoliage.mixin;

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
		Boolean result = RenderingHandler.wrapRenderBlock(() -> original.call(dispatcher, state, pos, blockAccess, worldRenderer), state, pos, blockAccess, () -> worldRenderer, layer);
		if(result == null) return original.call(dispatcher, state, pos, blockAccess, worldRenderer);
		else return result;
	}
}