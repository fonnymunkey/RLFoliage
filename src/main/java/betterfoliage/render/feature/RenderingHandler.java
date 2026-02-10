package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class RenderingHandler extends ResourceHandler {
	public static final List<RenderingHandler> RENDERERS = new ArrayList<>();
	
	protected RenderingHandler() {
		super();
	}
	
	public abstract boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout);
	public abstract boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout);
	
	public static boolean renderWorldBlockBase(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer) {
		return dispatcher.renderBlock(ctx.getState(), ctx.getPos(), ctx.getWorld(), renderer);
	}
	
	public static boolean canRenderBlockInLayer(Block block, IBlockState state, BlockRenderLayer layer) {
		if(!ForgeConfigHandler.GLOBAL.enabled) return block.canRenderInLayer(state, layer);
		
		boolean cutout = layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.CUTOUT_MIPPED;
		//Dont modify non-cutout layers
		if(!cutout) return block.canRenderInLayer(state, layer);
		
		if(!ForgeConfigHandler.GLOBAL.renderLayerAdjustments) {
			if(layer == BlockRenderLayer.CUTOUT_MIPPED) return block.canRenderInLayer(state, BlockRenderLayer.CUTOUT_MIPPED);
			return layer == BlockRenderLayer.CUTOUT;
		}
		
		//Cutout pass, modify based on mipped
		//Replicate normal Optifine handling that is disabled when BetterFoliage is installed
		if(Minecraft.getMinecraft().gameSettings.mipmapLevels > 0) {
			//Redstone and Cactus always on base cutout
			if(block instanceof BlockRedstoneWire) {
				return layer == BlockRenderLayer.CUTOUT;
			}
			if(block instanceof BlockCactus) {
				return layer == BlockRenderLayer.CUTOUT;
			}
			//If mipped, render cutouts on mipped cutout
			return layer == BlockRenderLayer.CUTOUT_MIPPED;
		}
		else {
			//If not mipped, render cutouts on base cutout
			return layer == BlockRenderLayer.CUTOUT;
		}
	}
	
	private static final Set<IBlockState> ERRORED_STATES = new HashSet<>();
	
	public static void log(Level level, String msg) {
		BetterFoliage.LOGGER.log(level, "[BetterFoliage] {}", msg);
		BetterFoliage.LOGGER_DETAIL.log(level, msg);
	}
	
	public static void logRenderError(IBlockState state, BlockPos pos) {
		if(state == null || pos == null || ERRORED_STATES.contains(state)) return;
		ERRORED_STATES.add(state);
		
		String blockName = Block.REGISTRY.getNameForObject(state.getBlock()).toString();
		String blockPos = pos.getX() + "," + pos.getY() + "," + pos.getZ();
		if(ForgeConfigHandler.GLOBAL.logRenderErrorToChat) {
			ITextComponent first = new TextComponentString(blockName);
			ITextComponent second = new TextComponentString(blockPos);
			first.getStyle().setColor(TextFormatting.GOLD);
			second.getStyle().setColor(TextFormatting.GOLD);
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
					new TextComponentTranslation("betterfoliage.rendererror", first, second));
		}
		BetterFoliage.LOGGER_DETAIL.log(Level.DEBUG, "Error rendering block {} at {}", state, blockPos);
	}
}