package betterfoliage.render.registry;

import betterfoliage.BetterFoliage;
import betterfoliage.config.BlockMatcherRegistry;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.config.ModelTextureList;
import betterfoliage.render.generator.ConnectedGrassGenerator;
import betterfoliage.render.math.Pair;
import betterfoliage.render.model.HSB;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

public class GrassRegistry extends ModelRenderRegistryRoot<GrassRegistry.GrassInfo> {
	public static final ModelRenderRegistryRoot<GrassInfo> GRASS_REGISTRY = new GrassRegistry();
	
	private GrassRegistry() {
		super();
	}
	
	public static class StandardGrassRegistry extends ModelRenderRegistryBase<GrassInfo> {
		public static final StandardGrassRegistry STANDARD_GRASS_REGISTRY = new StandardGrassRegistry();
		
		private StandardGrassRegistry() {
			super(BetterFoliage.LOGGER_DETAIL);
			GRASS_REGISTRY.addRegistry(this);
		}
		
		@Override
		public BlockMatcherRegistry.BlockMatcher matchClasses() {
			return ForgeConfigHandler.BLOCKS.grassClassesMatcher;
		}
		
		@Override
		public List<ModelTextureList> modelTextures() {
			return ForgeConfigHandler.BLOCKS.grassModelsTextures;
		}
		
		@Nullable
		@Override
		public ModelRenderKey<GrassInfo> processModel(IBlockState state, List<String> textures) {
			return new StandardGrassKey(this.logger, textures.get(0), textures.get(1));
		}
	}
	
	public static class StandardGrassKey implements ModelRenderKey<GrassInfo> {
		private static final String logName = "StandardGrassKey";
		private static final int defaultGrassColor = 0;
		private final Logger logger;
		private final String textureTop;
		private final String textureBottom;
		private ResourceLocation generatedShortOverlay = null;
		private ResourceLocation generatedLongOverlay = null;
		
		private StandardGrassKey(Logger logger, String textureTop, String textureBottom) {
			this.logger = logger;
			this.textureTop = textureTop;
			this.textureBottom = textureBottom;
		}
		
		@Override
		public void onPreStitch(TextureMap atlas) {
			this.generatedShortOverlay = ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource(this.textureTop, new Pair<>("long", false));
			atlas.registerSprite(this.generatedShortOverlay);
			this.generatedLongOverlay = ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource(this.textureTop, new Pair<>("long", true));
			atlas.registerSprite(this.generatedLongOverlay);
			
			this.logger.log(Level.DEBUG, "{}: grass texture top {}", logName, this.textureTop);
			this.logger.log(Level.DEBUG, "{}:               bottom {}", logName, this.textureBottom);
		}
		
		@Override
		public GrassInfo resolveSprites(TextureMap atlas) {
			String topTextureName = this.textureTop;
			TextureAtlasSprite topTexture = atlas.getTextureExtry(new ResourceLocation(topTextureName).toString());
			if(topTexture == null) topTexture = atlas.getMissingSprite();
			this.logger.log(Level.DEBUG, "{}: texture top {}", logName, topTextureName);
			String bottomTextureName = this.textureBottom;
			TextureAtlasSprite bottomTexture = atlas.getTextureExtry(new ResourceLocation(bottomTextureName).toString());
			if(bottomTexture == null) bottomTexture = atlas.getMissingSprite();
			this.logger.log(Level.DEBUG, "{}: texture bottom {}", logName, bottomTextureName);
			
			Integer avgColor = RenderUtil.averageColor(topTexture);
			if(avgColor == null) avgColor = defaultGrassColor;
			HSB hsb = HSB.fromColor(avgColor);
			Integer overrideColor;
			if(hsb.saturation >= ForgeConfigHandler.SHORTGRASS.saturationThreshold) {
				this.logger.log(Level.DEBUG, "{}:       brightness {}", logName, hsb.brightness);
				this.logger.log(Level.DEBUG, "{}:       saturation {} >= {}, using texture color", logName, hsb.saturation, ForgeConfigHandler.SHORTGRASS.saturationThreshold);
				hsb.brightness = Math.min(0.9F, hsb.brightness * 2.0F);
				overrideColor = hsb.asColor();
			}
			else {
				this.logger.log(Level.DEBUG, "{}:       saturation {} < {}, using block color", logName, hsb.saturation, ForgeConfigHandler.SHORTGRASS.saturationThreshold);
				overrideColor = null;
			}
			
			TextureAtlasSprite spriteShortOverlay = atlas.getTextureExtry(this.generatedShortOverlay.toString());
			if(spriteShortOverlay == null) spriteShortOverlay = atlas.getMissingSprite();
			TextureAtlasSprite spriteLongOverlay = atlas.getTextureExtry(this.generatedLongOverlay.toString());
			if(spriteLongOverlay == null) spriteLongOverlay = atlas.getMissingSprite();
			
			return new GrassInfo(topTexture, bottomTexture, spriteShortOverlay, spriteLongOverlay, overrideColor);
		}
	}
	
	public static class GrassInfo {
		public final TextureAtlasSprite grassTopTexture;
		public final TextureAtlasSprite grassBottomTexture;
		public final TextureAtlasSprite sideShortOverlayTexture;
		public final TextureAtlasSprite sideLongOverlayTexture;
		public final Integer overrideColor;
		
		private GrassInfo(TextureAtlasSprite grassTopTexture,
						  TextureAtlasSprite grassBottomTexture,
						  TextureAtlasSprite sideShortOverlayTexture,
						  TextureAtlasSprite sideLongOverlayTexture,
						  @Nullable Integer overrideColor) {
			this.grassTopTexture = grassTopTexture;
			this.grassBottomTexture = grassBottomTexture;
			this.sideShortOverlayTexture = sideShortOverlayTexture;
			this.sideLongOverlayTexture = sideLongOverlayTexture;
			this.overrideColor = overrideColor;
		}
	}
}