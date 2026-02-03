package betterfoliage.mixin.vintagefix;

import betterfoliage.render.util.LoadModelDataEvent;
import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.embeddedt.vintagefix.dynamicresources.model.DynamicModelProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(value = ModelManager.class, priority = 1500)
public abstract class MixinMixinModelManager {
	
	@Shadow
	public abstract BlockModelShapes getBlockModelShapes();
	
	//Really hacky workaround for compat but seems to work correctly
	@TargetHandler(
			mixin = "org.embeddedt.vintagefix.mixin.dynamic_resources.MixinModelManager",
			name = "func_110549_a(Lnet/minecraft/client/resources/IResourceManager;)V"
	)
	@Inject(
			method = "@MixinSquared:Handler",
			at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z", shift = At.Shift.AFTER)
	)
	private void betterfoliage_vfModelManager_onResourceManagerReload(IResourceManager resourceManager, CallbackInfo ci) {
		List<Map.Entry<IBlockState,ModelResourceLocation>> stateMappings = new ArrayList<>();
		for(Block block : ForgeRegistries.BLOCKS) {
			IStateMapper mapper = this.getBlockModelShapes().getBlockStateMapper().blockStateMap.get(block);
			if(!(mapper instanceof IStateMapper)) mapper = new DefaultStateMapper();
			stateMappings.addAll(mapper.putStateModelLocations(block).entrySet());
		}
		MinecraftForge.EVENT_BUS.post(new LoadModelDataEvent(stateMappings, loc -> {
			try {
				//Forestry leaves spam errors with VF loading and are handled separately regardless
				if(loc.getNamespace().equals("forestry")) return null;
				return DynamicModelProvider.instance.getObject(loc);
			}
			catch(Exception ex) {
				return null;
			}
		}));
	}
}