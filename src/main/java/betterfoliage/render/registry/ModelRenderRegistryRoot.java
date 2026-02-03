package betterfoliage.render.registry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModelRenderRegistryRoot<T> implements ModelRenderRegistry<T> {
	private final List<ModelRenderRegistry<T>> subRegistries = new ArrayList<>();
	
	@Nullable
	@Override
	public T get(IBlockState state, IBlockAccess world, BlockPos pos) {
		for(ModelRenderRegistry<T> reg : this.subRegistries) {
			T val = reg.get(state, world, pos);
			if(val != null) return val;
		}
		return null;
	}
	
	public void addRegistry(ModelRenderRegistry<T> registry) {
		this.subRegistries.add(registry);
		MinecraftForge.EVENT_BUS.register(registry);
	}
}