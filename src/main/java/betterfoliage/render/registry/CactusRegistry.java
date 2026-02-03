package betterfoliage.render.registry;

import betterfoliage.BetterFoliage;
import betterfoliage.config.BlockMatcherRegistry;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.config.ModelTextureList;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CactusRegistry extends ModelRenderRegistryRoot<CactusRegistry.CactusInfo> {
	public static final ModelRenderRegistryRoot<CactusInfo> CACTUS_REGISTRY = new CactusRegistry();
	
	private CactusRegistry() {
		super();
	}
	
	public static class StandardCactusRegistry extends ModelRenderRegistryBase<CactusInfo> {
		public static final StandardCactusRegistry STANDARD_CACTUS_REGISTRY = new StandardCactusRegistry();
		
		private StandardCactusRegistry() {
			super(BetterFoliage.LOGGER_DETAIL);
			CACTUS_REGISTRY.addRegistry(this);
		}
		
		@Override
		public BlockMatcherRegistry.BlockMatcher matchClasses() {
			return ForgeConfigHandler.BLOCKS.cactusClassesMatcher;
		}
		
		@Override
		public List<ModelTextureList> modelTextures() {
			return ForgeConfigHandler.BLOCKS.cactusModelsTextures;
		}
		
		@Nullable
		@Override
		public ModelRenderKey<CactusInfo> processModel(IBlockState state, List<String> textures) {
			return new StandardCactusKey(this.logger, textures);
		}
	}
	
	public static class StandardCactusKey implements ModelRenderKey<CactusInfo> {
		private static final String logName = "StandardCactusKey";
		private final List<String> textures;
		private final Logger logger;
		
		private StandardCactusKey(Logger logger, List<String> textures) {
			this.logger = logger;
			this.textures = textures;
		}
		
		@Override
		public CactusInfo resolveSprites(TextureMap atlas) {
			String topTextureName = this.textures.get(0);
			TextureAtlasSprite topTexture = atlas.getTextureExtry(new ResourceLocation(topTextureName).toString());
			if(topTexture == null) topTexture = atlas.getMissingSprite();
			this.logger.log(Level.DEBUG, "{}: texture top {}", logName, topTextureName);
			String bottomTextureName = this.textures.get(1);
			TextureAtlasSprite bottomTexture = atlas.getTextureExtry(new ResourceLocation(bottomTextureName).toString());
			if(bottomTexture == null) bottomTexture = atlas.getMissingSprite();
			this.logger.log(Level.DEBUG, "{}: texture bottom {}", logName, bottomTextureName);
			List<TextureAtlasSprite> sideTextures = new ArrayList<>();
			for(int i = 2; i < this.textures.size(); i++) {
				String sideTextureName = this.textures.get(i);
				TextureAtlasSprite sideTexture = atlas.getTextureExtry(new ResourceLocation(sideTextureName).toString());
				if(sideTexture == null) sideTexture = atlas.getMissingSprite();
				this.logger.log(Level.DEBUG, "{}: texture side {} {}", logName, i, sideTextureName);
				sideTextures.add(sideTexture);
			}
			return new CactusInfo(topTexture, bottomTexture, sideTextures);
		}
	}
	
	public static class CactusInfo {
		private static final int[] dirToIdx = new int[] { 0, 1, 2, 3, 4, 5 };
		
		public final TextureAtlasSprite topTexture;
		public final TextureAtlasSprite bottomTexture;
		public final ShaderUtil.QuadIconResolver sideResolver;
		
		private CactusInfo(TextureAtlasSprite topTexture,
						   TextureAtlasSprite bottomTexture,
						   List<TextureAtlasSprite> sideTextures) {
			this.topTexture = topTexture;
			this.bottomTexture = bottomTexture;
			this.sideResolver = (m, i, q) -> {
				EnumFacing face = MathUtil.rotate((i & 1) == 0 ? EnumFacing.SOUTH : EnumFacing.EAST, m.rotation);
				int sideIdx = sideTextures.size() > 1 ? (m.BLOCK_CONTEXT.getRandom(1) + dirToIdx[face.ordinal()]) % sideTextures.size() : 0;
				return sideTextures.get(sideIdx);
			};
		}
	}
}