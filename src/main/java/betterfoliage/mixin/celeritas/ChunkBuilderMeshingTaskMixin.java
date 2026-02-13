package betterfoliage.mixin.celeritas;

import betterfoliage.render.feature.RenderingHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.taumc.celeritas.impl.render.terrain.compile.task.ChunkBuilderMeshingTask;

@Mixin(ChunkBuilderMeshingTask.class)
public abstract class ChunkBuilderMeshingTaskMixin {

	@Redirect(
			method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;canRenderInLayer(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockRenderLayer;)Z"),
			remap = false
	)
	private boolean betterfoliage_celeritasChunkBuilderMeshingTask_execute_canRenderInLayer(Block block, IBlockState state, BlockRenderLayer layer) {
		return RenderingHandler.canRenderBlockInLayer(block, state, layer);
	}
	
	@WrapOperation(
			method = "execute(Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildContext;Lorg/embeddedt/embeddium/impl/util/task/CancellationToken;)Lorg/embeddedt/embeddium/impl/render/chunk/compile/ChunkBuildOutput;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;renderBlock(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)Z")
	)
	private boolean betterfoliage_nothiriumRenderChunkTaskCompile_renderBlockState_renderBlock(BlockRendererDispatcher dispatcher, IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRenderer, Operation<Boolean> original, @Local BlockRenderLayer layer) {
		Boolean result = RenderingHandler.wrapRenderBlock(dispatcher, state, pos, blockAccess, worldRenderer, layer);
		if(result == null) return original.call(dispatcher, state, pos, blockAccess, worldRenderer);
		else return result;
	}
}