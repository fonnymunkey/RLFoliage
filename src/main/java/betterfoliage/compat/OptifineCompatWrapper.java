package betterfoliage.compat;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.feature.RenderingHandler;
import fermiumbooter.FermiumRegistryAPI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.logging.log4j.Level;

public abstract class OptifineCompatWrapper {
	private static final String Optifine_MODID = "optifine";
	private static Boolean optifineLoaded = null;
	
	public static void init() {
		if(isOptifineLoaded()) OptifineHandler.init();
		else RenderingHandler.log(Level.INFO, "Optifine support is disabled");
	}
	
	public static boolean isOptifineLoaded() {
		if(optifineLoaded == null) optifineLoaded = FermiumRegistryAPI.isModPresent(Optifine_MODID);
		return optifineLoaded;
	}
	
	public static int getBlockColor(BlockContext ctx, IBlockState state, BlockPos pos) {
		Integer ofColor = null;
		if(isOptifineLoaded()) {
			ofColor = OptifineHandler.getBlockColor(ctx, state, pos);
		}
		if(ofColor == null || ofColor == -1) return ctx.getBlockData(state, pos).color;
		else return ofColor;
	}
	
	public static final long GRASS_DEFAULT_ID = blockIdFor(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS));
	public static final long LEAVES_DEFAULT_ID = blockIdFor(Blocks.LEAVES.getDefaultState());
	
	public static long blockIdFor(IBlockState state) {
		return (long)Block.REGISTRY.getIDForObject(state.getBlock()) & 65535L;
	}
	
	public static long getBlockIdOverride(long original, IBlockState state) {
		if(ForgeConfigHandler.BLOCKS.leavesClassesMatcher.matchesClass(state.getBlock())) return ForgeConfigHandler.SHADERS.leavesId;
		if(ForgeConfigHandler.BLOCKS.cropsClassesMatcher.matchesClass(state.getBlock())) return ForgeConfigHandler.SHADERS.grassId;
		return original;
	}
	
	public static void renderAs(long blockId, EnumBlockRenderType renderType, BufferBuilder renderer, boolean enabled, Runnable func) {
		if(enabled && isOptifineLoaded()) {
			blockId = blockId | (long)(renderType.ordinal() << 16);
			OptifineHandler.renderAsOptifine(blockId, renderer, func);
		}
		else func.run();
	}
	
	public static void renderAs(IBlockState state, EnumBlockRenderType renderType, BufferBuilder renderer, boolean enabled, Runnable func) {
		renderAs(blockIdFor(state), renderType, renderer, enabled, func);
	}
	
	public static void grass(BufferBuilder renderer, boolean enabled, Runnable func) {
		renderAs(ForgeConfigHandler.SHADERS.grassId, EnumBlockRenderType.MODEL, renderer, enabled, func);
	}
	
	public static void leaves(BufferBuilder renderer, boolean enabled, Runnable func) {
		renderAs(ForgeConfigHandler.SHADERS.leavesId, EnumBlockRenderType.MODEL, renderer, enabled, func);
	}
	
	public static void setQuadSprite(BufferBuilder worldRenderer, TextureAtlasSprite sprite) {
		if(isOptifineLoaded()) OptifineHandler.setQuadSprite(worldRenderer, sprite);
	}
	
	public static boolean isCacheOF(IBlockAccess access) {
		if(isOptifineLoaded()) return OptifineHandler.isCacheOF(access);
		return false;
	}
	
	public static boolean isBlockLoadedSafe(IBlockAccess access, BlockPos pos) {
		return OptifineHandler.isBlockLoadedSafe(access, pos);
	}
	
	public static float getDiffusedMult(EnumFacing face) {
		if(!isOptifineLoaded()) {
			switch(face) {
				case DOWN: return 0.5F;
				case UP: return 1.0F;
				case NORTH:
				case SOUTH: return 0.8F;
				default: return 0.6F;
			}
		}
		else return OptifineHandler.getDiffusedMult(face);
	}
}