package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.model.Model;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

public class RenderNetherrack extends RenderingHandler {
	public static final RenderNetherrack RENDER_NETHERRACK = new RenderNetherrack();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet netherrackIcon = getIconSet(BetterFoliage.MODID, "blocks/better_netherrack_%d");
	private final ModelSet netherrackModel = getModelSet(64, (model, i) -> model.addAll(
			Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, -0.5 - MathUtil.random(ForgeConfigHandler.NETHERRACK.heightMin, ForgeConfigHandler.NETHERRACK.heightMax), -0.5)
				 .setAoShader(ShaderUtil.faceOrientedAuto(EnumFacing.DOWN, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
				 .setFlatShader(ShaderUtil.faceOrientedAuto(EnumFacing.DOWN, ShaderUtil.CORNER_FLAT))
				 .toCross(EnumFacing.UP, q -> q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.NETHERRACK.hOffset)))));
	
	private RenderNetherrack() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s netherrack textures", this.netherrackIcon.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.NETHERRACK.enabled &&
				renderCutout &&
				ForgeConfigHandler.NETHERRACK.population > 0 &&
				ForgeConfigHandler.BLOCKS.netherrackClassesMatcher.matchesClass(ctx.getBlock()) &&
				(ForgeConfigHandler.NETHERRACK.population >= 64 || ForgeConfigHandler.NETHERRACK.population > this.noise.get(ctx.getPos()));
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		if(!renderCutout) return rendered;
		
		if(ctx.getState(0, -1, 0).isOpaqueCube()) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(EnumFacing.DOWN);
		
		int[] rand = ctx.getSemiRandomArray(2);
		modelRenderer.render(
				renderer,
				netherrackModel.get(rand[0]),
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> netherrackIcon.get(rand[i & 1]),
				ShaderUtil.NO_POST);
		return true;
	}
}