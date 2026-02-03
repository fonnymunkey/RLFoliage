package betterfoliage.mixin.optifine;

import betterfoliage.compat.OptifineCompatWrapper;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.state.IBlockState;
import net.optifine.shaders.SVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SVertexBuilder.class)
public abstract class SVertexBuilderMixin {
	
	@ModifyArg(
			method = "pushEntity(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/BufferBuilder;)V",
			at = @At(value = "INVOKE", target = "Lnet/optifine/shaders/SVertexBuilder;pushEntity(J)V"),
			remap = false
	)
	private static long betterfoliage_optifineSVertexBuilder_pushEntity(long data, @Local(argsOnly = true) IBlockState blockState) {
		return OptifineCompatWrapper.getBlockIdOverride(data, blockState);
	}
}