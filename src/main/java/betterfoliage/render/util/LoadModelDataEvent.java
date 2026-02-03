package betterfoliage.render.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LoadModelDataEvent extends Event {
	private final List<Map.Entry<IBlockState,ModelResourceLocation>> stateMappings;
	private final Function<ModelResourceLocation,IModel> modelFunction;
	
	public LoadModelDataEvent(List<Map.Entry<IBlockState,ModelResourceLocation>> stateMappings, Function<ModelResourceLocation,IModel> modelFunction) {
		this.stateMappings = stateMappings;
		this.modelFunction = modelFunction;
	}
	
	public List<Map.Entry<IBlockState,ModelResourceLocation>> getStateMappings() {
		return this.stateMappings;
	}
	
	@Nullable
	public IModel getModelFromResource(ModelResourceLocation loc) {
		return this.modelFunction.apply(loc);
	}
}