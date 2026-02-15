package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class RenderMycelium extends RenderingHandler {
	public static final RenderMycelium RENDER_MYCELIUM = new RenderMycelium();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet mycelNormalIcons = getIconSet(BetterFoliage.MODID, "blocks/better_mycel_%d");
	private final IconSet mycelSnowIcons = getIconSet(BetterFoliage.MODID, "blocks/better_mycel_snowed_%d");
	private final ModelSet mycelModels = getModelSet(64, RenderGrass.grassTopQuads(ForgeConfigHandler.SHORTGRASS.heightMin, ForgeConfigHandler.SHORTGRASS.heightMax));
	
	private RenderMycelium() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s mycelium textures", this.mycelNormalIcons.size));
		log(Level.INFO, String.format("Registered %s snowed mycelium textures", this.mycelSnowIcons.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.SHORTGRASS.myceliumEnabled &&
				renderCutout &&
				ForgeConfigHandler.SHORTGRASS.myceliumPopulation > 0 &&
				ForgeConfigHandler.BLOCKS.myceliumClassesMatcher.matchesClass(ctx.getBlock()) &&
				(ForgeConfigHandler.SHORTGRASS.myceliumPopulation >= 64 || ForgeConfigHandler.SHORTGRASS.myceliumPopulation > this.noise.get(ctx.getPos()));
	}
	
	@Override
	public boolean render(BlockContext ctx, Supplier<Boolean> renderBase, Supplier<BufferBuilder> worldRenderer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderBase.get();
		if(!renderCutout) return rendered;
		
		IBlockState up = ctx.getState(0, 1, 0);
		boolean isSnowed = RenderUtil.isSnow(up.getMaterial());
		if(isSnowed) {
			if(!ForgeConfigHandler.SHORTGRASS.snowEnabled) return rendered;
			else if(up.getMaterial() == Material.CRAFTED_SNOW) return rendered;
		}
		else if(up.getMaterial() != Material.AIR) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(EnumFacing.UP);
		
		IconSet iconSet = isSnowed ? this.mycelSnowIcons : this.mycelNormalIcons;
		int[] rand = ctx.getSemiRandomArray(2);
		BufferBuilder renderer = worldRenderer.get();
		OptifineCompatWrapper.grass(renderer, ForgeConfigHandler.SHORTGRASS.shaderWind, () -> modelRenderer.render(
				renderer,
				mycelModels.get(rand[0]),
				MathUtil.IDENTITY,
				isSnowed ? ctx.getCenter().add(0, 0.0625, 0) : ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> iconSet.get(rand[i & 1]),
				(rv, m, i, q, i2, v) -> {
					if(isSnowed) rv.setGrey(1.0F);
				}));
		return true;
	}
}