package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.generator.ReedsGenerator;
import betterfoliage.render.math.Int3;
import betterfoliage.render.model.Model;
import betterfoliage.render.shader.FlatOffsetNoColor;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

public class RenderReeds extends RenderingHandler {
	public static final RenderReeds RENDER_REEDS = new RenderReeds();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet reedIcons = getIconSet(ReedsGenerator.REEDS_GENERATOR.generatedResource(BetterFoliage.MODID + ":blocks/better_reed_%d", null));
	private final ModelSet reedModels = getModelSet(64, (model, i) -> {
		double height = MathUtil.random(ForgeConfigHandler.REED.heightMin, ForgeConfigHandler.REED.heightMax);
		double waterline = 0.875;
		double vCutLine = 0.5 - waterline / height;
		model.addAll(Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.5, 0.5 + waterline)
						  .setFlatShader(new FlatOffsetNoColor(new Int3(0, 1, 0)))
						  .clampUV(-0.25, 0.25, vCutLine, 0.5)
						  .toCross(EnumFacing.UP, q ->
								  q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.REED.hOffset)))
					);
		model.addAll(Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.5 + waterline, 0.5 + height)
						  .setFlatShader(new FlatOffsetNoColor(new Int3(0, 2, 0)))
						  .clampUV(-0.25, 0.25, -0.5, vCutLine)
						  .toCross(EnumFacing.UP, q ->
								  q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.REED.hOffset)))
					);
	});
	
	private RenderReeds() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s reed textures", this.reedIcons.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.REED.enabled &&
				renderCutout &&
				ForgeConfigHandler.REED.population > 0 &&
				ForgeConfigHandler.BLOCKS.dirtClassesMatcher.matchesClass(ctx.getBlock()) &&
				ForgeConfigHandler.REED.isBiomeValid(ctx.getBiomeId()) &&
				ctx.getState(0, 2, 0).getMaterial() == Material.AIR &&
				ctx.getState(0, 1, 0).getMaterial() == Material.WATER &&
				(ForgeConfigHandler.REED.population >= 64 || ForgeConfigHandler.REED.population > this.noise.get(ctx.getPos()));
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		if(!renderCutout) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		
		int iconVar = ctx.getRandom(1);
		OptifineCompatWrapper.grass(renderer, ForgeConfigHandler.REED.shaderWind, () -> modelRenderer.render(
				renderer,
				reedModels.get(ctx.getRandom(0)),
				MathUtil.IDENTITY,
				ctx.getCenter(),
				true,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> reedIcons.get(iconVar),
				ShaderUtil.NO_POST));
		return true;
	}
}