package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.BlockContext;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class RenderAlgae extends RenderingHandler {
	public static final RenderAlgae RENDER_ALGAE = new RenderAlgae();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet algaeIcons = getIconSet(BetterFoliage.MODID, "blocks/better_algae_%d");
	private final ModelSet algaeModels = getModelSet(64, RenderGrass.grassTopQuads(ForgeConfigHandler.ALGAE.heightMin, ForgeConfigHandler.ALGAE.heightMax));
	
	private RenderAlgae() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s algae textures", this.algaeIcons.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.ALGAE.enabled &&
				renderCutout &&
				ForgeConfigHandler.ALGAE.population > 0 &&
				ForgeConfigHandler.BLOCKS.dirtClassesMatcher.matchesClass(ctx.getBlock()) &&
				ForgeConfigHandler.ALGAE.isBiomeValid(ctx.getBiomeId()) &&
				ctx.getState(0, 2, 0).getMaterial() == Material.WATER &&
				ctx.getState(0, 1, 0).getMaterial() == Material.WATER &&
				(ForgeConfigHandler.ALGAE.population >= 64 || (ForgeConfigHandler.ALGAE.population > this.noise.get(ctx.getPos())));
	}
	
	@Override
	public boolean render(BlockContext ctx, Supplier<Boolean> renderBase, Supplier<BufferBuilder> worldRenderer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderBase.get();
		if(!renderCutout) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(EnumFacing.UP);
		
		BufferBuilder renderer = worldRenderer.get();
		int[] rand = ctx.getSemiRandomArray(3);
		OptifineCompatWrapper.grass(renderer, ForgeConfigHandler.ALGAE.shaderWind, () -> modelRenderer.render(
				renderer,
				this.algaeModels.get(rand[2]),
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> this.algaeIcons.get(rand[i & 1]),
				ShaderUtil.NO_POST));
		return true;
	}
}