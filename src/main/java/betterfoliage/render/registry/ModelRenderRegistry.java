package betterfoliage.render.registry;

import betterfoliage.render.BlockContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public interface ModelRenderRegistry<T> {
	@Nullable
	T get(IBlockState state, IBlockAccess world, BlockPos pos);
	@Nullable
	default T get(BlockContext ctx) {
		return this.get(ctx.getState(), ctx.getWorld(), ctx.getPos());
	}
}