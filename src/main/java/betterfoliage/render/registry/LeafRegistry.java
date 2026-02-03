package betterfoliage.render.registry;

import betterfoliage.BetterFoliage;
import betterfoliage.config.BlockMatcherRegistry;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.config.ModelTextureList;
import betterfoliage.render.feature.ResourceHandler;
import betterfoliage.render.math.Pair;
import betterfoliage.render.generator.LeafGenerator;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class LeafRegistry extends ModelRenderRegistryRoot<LeafRegistry.LeafInfo> {
	public static final ModelRenderRegistryRoot<LeafInfo> LEAF_REGISTRY = new LeafRegistry();
	
	private LeafRegistry() {
		super();
	}
	
	public static class StandardLeafRegistry extends ModelRenderRegistryBase<LeafInfo> {
		public static final StandardLeafRegistry STANDARD_LEAF_REGISTRY = new StandardLeafRegistry();
		
		private StandardLeafRegistry() {
			super(BetterFoliage.LOGGER_DETAIL);
			LEAF_REGISTRY.addRegistry(this);
		}
		
		@Override
		public BlockMatcherRegistry.BlockMatcher matchClasses() {
			return ForgeConfigHandler.BLOCKS.leavesClassesMatcher;
		}
		
		@Override
		public List<ModelTextureList> modelTextures() {
			return ForgeConfigHandler.BLOCKS.leavesModelsTextures;
		}
		
		@Nullable
		@Override
		public ModelRenderKey<LeafInfo> processModel(IBlockState state, List<String> textures) {
			return new StandardLeafKey(this.logger, textures.get(0));
		}
	}
	
	public static class StandardLeafKey implements ModelRenderKey<LeafInfo> {
		private static final String logName = "StandardLeafKey";
		private final Logger logger;
		private final String textureName;
		private String leafType = null;
		private ResourceLocation generated = null;
		
		public StandardLeafKey(Logger logger, String textureName) {
			this.logger = logger;
			this.textureName = textureName;
		}
		
		@Override
		public void onPreStitch(TextureMap atlas) {
			this.leafType = LeafParticleRegistry.LEAF_PARTICLE_REGISTRY.typeMappings.getType(this.textureName);
			if(this.leafType == null) this.leafType = "default";
			
			this.generated = LeafGenerator.LEAF_GENERATOR.generatedResource(this.textureName, new Pair<>("type", this.leafType));
			atlas.registerSprite(this.generated);
			
			this.logger.log(Level.DEBUG, "{}: leaf texture {}", logName, this.textureName);
			this.logger.log(Level.DEBUG, "{}:      particle {}", logName, this.leafType);
		}
		
		@Override
		public LeafInfo resolveSprites(TextureMap atlas) {
			TextureAtlasSprite sprite = atlas.getTextureExtry(this.generated.toString());
			if(sprite == null) sprite = atlas.getMissingSprite();
			return new LeafInfo(sprite, this.leafType);
		}
	}
	
	public static class LeafInfo {
		private static final int defaultLeafColor = 0;
		public final TextureAtlasSprite roundLeafTexture;
		public final String leafType;
		public final int averageColor;
		
		private LeafInfo(TextureAtlasSprite roundLeafTexture, String leafType) {
			this.roundLeafTexture = roundLeafTexture;
			this.leafType = leafType;
			Integer avg = RenderUtil.averageColor(roundLeafTexture);
			this.averageColor = avg == null ? defaultLeafColor : avg;
		}
		
		public ResourceHandler.IconSet particleTextures() {
			return LeafParticleRegistry.LEAF_PARTICLE_REGISTRY.get(this.leafType);
		}
	}
}