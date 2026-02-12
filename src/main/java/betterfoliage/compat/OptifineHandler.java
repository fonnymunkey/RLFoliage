package betterfoliage.compat;

import betterfoliage.mixin.optifine.ChunkCacheOFAccessor;
import betterfoliage.render.BlockContext;
import betterfoliage.render.feature.RenderingHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.optifine.CustomColors;
import net.optifine.override.ChunkCacheOF;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.Level;

public abstract class OptifineHandler {
	private static final ThreadLocal<RenderEnv> RENDER_ENV = ThreadLocal.withInitial(() -> new RenderEnv(null, null));
	private static final BakedQuad FAKE_QUAD = new BakedQuad(new int[0], 1, EnumFacing.UP, null, true, DefaultVertexFormats.BLOCK);
	
	public static void init() {
		RenderingHandler.log(Level.INFO, "RLFoliage Optifine support is enabled");
	}
	
	public static int getBlockColor(BlockContext ctx, IBlockState state, BlockPos pos) {
		if(((IGameSettingsOptifineMixin)Minecraft.getMinecraft().gameSettings).betterfoliage$getOFCustomColors()) {
			RenderEnv env = RENDER_ENV.get();
			env.reset(state, pos);
			return CustomColors.getColorMultiplier(FAKE_QUAD, state, ctx.getWorld(), pos, env);
		}
		return -1;
	}
	
	public static void renderAsOptifine(long blockId, BufferBuilder renderer, Runnable func) {
		SVertexBuilder vertexBuilder = ((IBufferBuilderOptifineMixin)renderer).betterfoliage$getSVertexBuilder();
		vertexBuilder.pushEntity(blockId);
		func.run();
		vertexBuilder.popEntity();
	}
	
	public static void setQuadSprite(BufferBuilder worldRenderer, TextureAtlasSprite sprite) {
		((IBufferBuilderOptifineMixin)worldRenderer).betterfoliage$setQuadSprite(sprite);
	}
	
	public static boolean isBlockLoadedSafe(IBlockAccess access, BlockPos pos) {
		if(access instanceof World) return ((World)access).isBlockLoaded(pos, false);
		if(access instanceof ChunkCache) return ((ChunkCache)access).world.isBlockLoaded(pos, false);
		if(access instanceof ChunkCacheOF) return ((ChunkCacheOFAccessor)access).getChunkCache().world.isBlockLoaded(pos, false);
		return false;
	}
	
	public static float getDiffusedMult(EnumFacing face) {
		if(!Shaders.isOldLighting()) return 1.0F;
		switch(face) {
			case DOWN: {
				if(Shaders.shaderPackLoaded) return Shaders.blockLightLevel05;
				return 0.5F;
			}
			case UP: return 1.0F;
			case NORTH:
			case SOUTH: {
				if(Shaders.shaderPackLoaded) return Shaders.blockLightLevel08;
				return 0.8F;
			}
			default: {
				if(Shaders.shaderPackLoaded) return Shaders.blockLightLevel06;
				return 0.6F;
			}
		}
	}
}