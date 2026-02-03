package betterfoliage.render.registry;

import betterfoliage.config.BlockMatcherRegistry;
import betterfoliage.config.ModelTextureList;
import betterfoliage.render.math.Pair;
import betterfoliage.render.util.LoadModelDataEvent;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ModelRenderRegistryBase<T> implements ModelRenderRegistry<T> {
	protected final Logger logger;
	protected final String logName;
	
	private final Map<IBlockState,ModelRenderKey<T>> stateToKey = new HashMap<>();
	private Map<IBlockState,T> stateToValue = new HashMap<>();
	
	protected ModelRenderRegistryBase(Logger logger) {
		this.logger = logger;
		this.logName = this.getClass().getSimpleName();
	}
	
	public abstract BlockMatcherRegistry.BlockMatcher matchClasses();
	public abstract List<ModelTextureList> modelTextures();
	@Nullable
	public abstract ModelRenderKey<T> processModel(IBlockState state, List<String> textures);

	@Nullable
	@Override
	public T get(IBlockState state, IBlockAccess world, BlockPos pos) {
		return this.stateToValue.get(state);
	}
	
	@Nullable
	public ModelRenderKey<T> processModel(IBlockState state, ModelResourceLocation modelLoc, IModel model) {
		String matchClass = this.matchClasses().matchingClassName(state.getBlock());
		if(matchClass == null) return null;
		this.logger.log(Level.DEBUG, "{}: block state {}", this.logName, state);
		this.logger.log(Level.DEBUG, "{}:       class {} matches {}", this.logName, state.getBlock().getClass().getName(), matchClass);
		
		List<Pair<ModelBlock,ResourceLocation>> allModels = RenderUtil.distinctByLoc(RenderUtil.modelBlockAndLoc(model));
		if(allModels.isEmpty()) {
			this.logger.log(Level.DEBUG, "{}:       no models found", this.logName);
			return null;
		}
		
		for(Pair<ModelBlock,ResourceLocation> pair : allModels) {
			ModelTextureList modelMatch = this.modelTextures().stream()
											  .filter(list -> RenderUtil.derivesFrom(pair.l, pair.r, list.modelLocation))
											  .findFirst().orElse(null);
			if(modelMatch != null) {
				this.logger.log(Level.DEBUG, "{}:       model {} matches {}", this.logName, pair.r, modelMatch.modelLocation);
				List<Pair<String,String>> textures = modelMatch.textureNames.stream()
																			.map(name -> new Pair<>(name, pair.l.resolveTextureName(name)))
																			.collect(Collectors.toList());
				String texMapString = textures.stream()
											  .map(p -> p.l + "=" + p.r)
											  .collect(Collectors.joining(", "));
				this.logger.log(Level.DEBUG, "{}:       textures [{}]", this.logName, texMapString);
				if(textures.stream().noneMatch(p -> p.r.equals("missingno"))) {
					return processModel(state, textures.stream().map(p -> p.r).collect(Collectors.toList()));
				}
			}
		}
		return null;
	}
	
	@SubscribeEvent
	public void handleLoadModelData(LoadModelDataEvent event) {
		this.stateToValue = new HashMap<>();
		for(Map.Entry<IBlockState,ModelResourceLocation> mapping : event.getStateMappings()) {
			IBlockState key = mapping.getKey();
			ModelResourceLocation value = mapping.getValue();
			if(key != null && value != null) {
				//Avoid forcing all models to load which defeats the point of VintageFix (Hopefully doesn't cause other issues)
				if(!this.matchClasses().matchesClass(key.getBlock())) continue;
				IModel model = event.getModelFromResource(value);
				if(model != null) {
					try {
						ModelRenderKey<T> processed = this.processModel(key, value, model);
						if(processed != null) this.stateToKey.put(key, processed);
					}
					catch(Exception e) {
						this.logger.log(Level.WARN, "{}: Exception while trying to process model {}", this.logName, value, e);
					}
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void handlePreStitch(TextureStitchEvent.Pre event) {
		for(ModelRenderKey<T> key : this.stateToKey.values()) {
			key.onPreStitch(event.getMap());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void handlePostStitch(TextureStitchEvent.Post event) {
		for(Map.Entry<IBlockState,ModelRenderKey<T>> entry : this.stateToKey.entrySet()) {
			this.stateToValue.put(entry.getKey(), entry.getValue().resolveSprites(event.getMap()));
		}
		this.stateToKey.clear();
	}
}