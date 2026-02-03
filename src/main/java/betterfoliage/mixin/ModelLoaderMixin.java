package betterfoliage.mixin;

import betterfoliage.render.util.LoadModelDataEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin extends ModelBakery {
	
	@Shadow(remap = false)
	@Final
	private Map<ModelResourceLocation,IModel> stateModels;
	
	public ModelLoaderMixin(IResourceManager p_i46085_1_, TextureMap p_i46085_2_, BlockModelShapes p_i46085_3_) {
		super(p_i46085_1_, p_i46085_2_, p_i46085_3_);
	}
	
	@Inject(
			method = "setupModelRegistry",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER)
	)
	private void betterfoliage_vanillaModelLoader_setupModelRegistry_loadSprites(CallbackInfoReturnable<IRegistry<ModelResourceLocation,IBakedModel>> cir) {
		List<Map.Entry<IBlockState,ModelResourceLocation>> stateMappings = new ArrayList<>();
		for(Block block : ForgeRegistries.BLOCKS) {
			IStateMapper mapper = this.blockModelShapes.getBlockStateMapper().blockStateMap.get(block);
			if(!(mapper instanceof IStateMapper)) mapper = new DefaultStateMapper();
			stateMappings.addAll(mapper.putStateModelLocations(block).entrySet());
		}
		MinecraftForge.EVENT_BUS.post(new LoadModelDataEvent(stateMappings, loc -> this.stateModels.get(loc)));
	}
}