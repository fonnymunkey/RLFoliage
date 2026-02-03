package betterfoliage.render.feature;

import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.generator.ConnectedGrassGenerator;
import betterfoliage.render.math.Int3;
import betterfoliage.render.math.Pair;
import betterfoliage.render.model.Model;
import betterfoliage.render.registry.GrassRegistry;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RenderConnectedGrass extends RenderingHandler {
	public static final RenderConnectedGrass RENDER_CONNECTED_GRASS = new RenderConnectedGrass();
	
	private static final int[] uvRot = new int[] { 2, 2, 3, 0, 3, 1 };
	
	private final IconHolder shortSnowIcon = getIconStatic(ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource("minecraft:blocks/snow", new Pair<>("long", false)));
	private final IconHolder longSnowIcon = getIconStatic(ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR.generatedResource("minecraft:blocks/snow", new Pair<>("long", true)));
	
	private RenderConnectedGrass() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.CONNECTEDGRASS.enabled &&
				(ForgeConfigHandler.CONNECTEDGRASS.snowEnabled || !RenderUtil.isSnow(ctx.getState(0, 2, 0).getMaterial())) &&
				ForgeConfigHandler.BLOCKS.dirtClassesMatcher.matchesClass(ctx.getBlock()) &&
				GrassRegistry.GRASS_REGISTRY.get(ctx.getState(0, 1, 0), ctx.getWorld(), ctx.getPos(0, 1, 0)) != null;
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		if(!renderCutout) return rendered;
		
		IBlockState stateUp = ctx.getState(0, 1, 0);
		BlockPos posUp = ctx.getPos(0, 1, 0);
		GrassRegistry.GrassInfo grassInfoUp = GrassRegistry.GRASS_REGISTRY.get(stateUp, ctx.getWorld(), posUp);
		if(grassInfoUp == null) {
			logRenderError(stateUp, posUp);
			return rendered;
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
		if(!shouldRenderOverlay) return rendered;
		shouldRenderOverlay = false;
		for(int i = 0; i < MathUtil.FORGEDIRS.length; i++) {
			EnumFacing face = MathUtil.FORGEDIRS[i];
			if(face == EnumFacing.UP || face == EnumFacing.DOWN) sideOverlay[i] = false;
			else {
				offsetPos.setPos(ctx.getPos()).move(face);
				sideOverlay[i] &= !ctx.getWorld().getBlockState(offsetPos).isOpaqueCube();
				shouldRenderOverlay |= sideOverlay[i];
			}
		}
		if(!shouldRenderOverlay) return rendered;
		
		int blockColorUp = OptifineCompatWrapper.getBlockColor(ctx, stateUp, posUp);
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(Int3.ZERO, RenderUtil.ALL_FACES);
		boolean isSnowed = RenderUtil.isSnow(ctx.getState(0, 2, 0).getMaterial());
		
		TextureAtlasSprite sideSprite = ForgeConfigHandler.CONNECTEDGRASS.longerSides ? grassInfoUp.sideLongTexture : grassInfoUp.sideShortTexture;
		TextureAtlasSprite sideSpriteSnow = ForgeConfigHandler.CONNECTEDGRASS.longerSides ? this.longSnowIcon.icon : this.shortSnowIcon.icon;
		OptifineCompatWrapper.renderAs(stateUp, EnumBlockRenderType.MODEL, renderer, true, () -> modelRenderer.render(
				renderer,
				Model.FULLCUBE_OVERLAY,
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				(i,q) -> sideOverlay[i],
				(m, i, q) -> isSnowed ? sideSpriteSnow : sideSprite,
				(rv, m, i, q, i2, v) -> {
					rv.rotateUV(uvRot[i]);
					if(!isSnowed && grassInfoUp.overrideColor == null) {
						rv.multiplyColor(blockColorUp);
					}
				}));
		return true;
	}
}