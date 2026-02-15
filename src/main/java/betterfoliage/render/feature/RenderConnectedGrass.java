package betterfoliage.render.feature;

import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.generator.ConnectedGrassGenerator;
import betterfoliage.render.math.Pair;
import betterfoliage.render.registry.GrassRegistry;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.function.Supplier;

public class RenderConnectedGrass extends RenderingHandler {
	public static final RenderConnectedGrass RENDER_CONNECTED_GRASS = new RenderConnectedGrass();
	
	private static final int[] uvRot = new int[] { 2, 2, 3, 0, 3, 1 };
	
	private final IconHolder snowShortOverlayTexture = getIconStatic(ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource("minecraft:blocks/snow", new Pair<>("long", false)));
	private final IconHolder snowLongOverlayTexture = getIconStatic(ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource("minecraft:blocks/snow", new Pair<>("long", true)));
	
	private RenderConnectedGrass() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.CONNECTEDGRASS.enabled &&
				ForgeConfigHandler.BLOCKS.dirtClassesMatcher.matchesClass(ctx.getBlock()) &&
				(ForgeConfigHandler.CONNECTEDGRASS.snowEnabled || !RenderUtil.isSnow(ctx.getState(0, 2, 0).getMaterial())) &&
				GrassRegistry.GRASS_REGISTRY.get(ctx.getState(0, 1, 0), ctx.getWorld(), ctx.getPos(0, 1, 0)) != null;
	}
	
	@Override
	public boolean render(BlockContext ctx, Supplier<Boolean> renderBase, Supplier<BufferBuilder> worldRenderer, boolean renderPrimary, boolean renderCutout) {
		if(!renderCutout) return false;
		IBlockState stateUp = ctx.getState(0, 1, 0);
		BlockPos posUp = ctx.getPos(0, 1, 0);
		GrassRegistry.GrassInfo grassInfoUp = GrassRegistry.GRASS_REGISTRY.get(stateUp, ctx.getWorld(), posUp);
		if(grassInfoUp == null) {
			logRenderError(stateUp, posUp);
			return renderBase.get();
		}
		
		//Get face textures based on visibility of above grass block
		//then filter based on base block visibility
		boolean[] sideOverlay = new boolean[6];
		boolean shouldRenderOverlay = false;
		BlockPos.MutableBlockPos offsetPos = new BlockPos.MutableBlockPos();
		for(int i = 0; i < MathUtil.FORGEDIRS.length; i++) {
			EnumFacing face = MathUtil.FORGEDIRS[i];
			if(face == EnumFacing.UP || face == EnumFacing.DOWN) sideOverlay[i] = false;
			else {
				offsetPos.setPos(posUp).move(face);
				sideOverlay[i] = !ctx.getWorld().getBlockState(offsetPos).isOpaqueCube();
				shouldRenderOverlay |= sideOverlay[i];
			}
		}
		if(!shouldRenderOverlay) return renderBase.get();
		shouldRenderOverlay = false;
		boolean[] sideBase = new boolean[6];
		for(int i = 0; i < MathUtil.FORGEDIRS.length; i++) {
			EnumFacing face = MathUtil.FORGEDIRS[i];
			if(face == EnumFacing.UP) {
				sideBase[i] = false;
				sideOverlay[i] = false;
			}
			else {
				offsetPos.setPos(ctx.getPos()).move(face);
				sideBase[i] = !ctx.getWorld().getBlockState(offsetPos).isOpaqueCube();
				sideOverlay[i] &= sideBase[i];
				shouldRenderOverlay |= sideOverlay[i] && face != EnumFacing.DOWN;
			}
		}
		if(!shouldRenderOverlay) return renderBase.get();
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(sideBase);
		
		int blockColorUp = OptifineCompatWrapper.getBlockColor(ctx, stateUp, posUp);
		boolean isSnowed = RenderUtil.isSnow(ctx.getState(0, 2, 0).getMaterial());
		
		TextureAtlasSprite sideSpriteOverlay;
		if(ForgeConfigHandler.CONNECTEDGRASS.longerSides) sideSpriteOverlay = isSnowed ? this.snowLongOverlayTexture.icon : grassInfoUp.sideLongOverlayTexture;
		else sideSpriteOverlay = isSnowed ? this.snowShortOverlayTexture.icon : grassInfoUp.sideShortOverlayTexture;
		
		int[] rand = ctx.getSemiRandomArray(1);
		BufferBuilder renderer = worldRenderer.get();
		OptifineCompatWrapper.renderAs(stateUp, EnumBlockRenderType.MODEL, renderer, true, () -> modelRenderer.render(
				renderer,
				RenderGrass.RENDER_GRASS.FULL_CUBE.model,
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				(i,q) -> sideBase[i],
				(m, i, q) -> grassInfoUp.grassBottomTexture,
				(rv, m, i, q, i2, v) -> {
					rv.rotateUV(i == 0 || i == 1 ? rand[0] : uvRot[i]);
				}));
		OptifineCompatWrapper.renderAs(stateUp, EnumBlockRenderType.MODEL, renderer, true, () -> modelRenderer.render(
				renderer,
				RenderGrass.RENDER_GRASS.FULL_CUBE.model,
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				(i,q) -> sideOverlay[i],
				(m, i, q) -> sideSpriteOverlay,
				(rv, m, i, q, i2, v) -> {
					rv.rotateUV(uvRot[i]);
					if(!isSnowed && grassInfoUp.overrideColor == null) {
						rv.multiplyColor(blockColorUp);
					}
				}));
		return true;
	}
}