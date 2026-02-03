package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Int3;
import betterfoliage.render.model.Model;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

public class RenderCoral extends RenderingHandler {
	public static final RenderCoral RENDER_CORAL = new RenderCoral();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet coralIcons = getIconSet(BetterFoliage.MODID, "blocks/better_coral_%d");
	private final IconSet crustIcons = getIconSet(BetterFoliage.MODID, "blocks/better_crust_%d");
	private final ModelSet coralModels = getModelSet(64, (model, i) -> {
		model.addAll(
				Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.0, 1.0)
					 .scale(ForgeConfigHandler.CORAL.size)
					 .move(0, 0.5, 0)
					 .toCross(EnumFacing.UP, q -> q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.CORAL.hOffset))));
		double separation = MathUtil.random(0.01, ForgeConfigHandler.CORAL.vOffset);
		model.add(
				Model.horizontalRectangle(-0.5, -0.5, 0.5, 0.5, 0.0)
					 .scale(ForgeConfigHandler.CORAL.crustSize)
					 .move(0.0, 0.5 + separation, 0.0));
		model.transformQ(q -> q
				.setAoShader(ShaderUtil.faceOrientedAuto(EnumFacing.UP, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
				.setFlatShader(ShaderUtil.faceOrientedAuto(EnumFacing.UP, ShaderUtil.CORNER_FLAT)));
	});
	
	private RenderCoral() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s coral textures", this.coralIcons.size));
		log(Level.INFO, String.format("Registered %s coral crust textures", this.crustIcons.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.CORAL.enabled &&
				renderCutout &&
				ForgeConfigHandler.CORAL.population > 0 &&
				(ForgeConfigHandler.CORAL.population >= 64 || (ForgeConfigHandler.CORAL.population > this.noise.get(ctx.getPos()))) &&
				ctx.getState(0, 1, 0).getMaterial() == Material.WATER &&
				(ForgeConfigHandler.CORAL.shallowWater || ctx.getState(0, 2, 0).getMaterial() == Material.WATER) &&
				ForgeConfigHandler.BLOCKS.sandClassesMatcher.matchesClass(ctx.getBlock()) &&
				ForgeConfigHandler.CORAL.isBiomeValid(ctx.getBiomeId());
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		if(!renderCutout) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(Int3.ZERO, RenderUtil.ALL_FACES);
		
		for(int i = 0; i < MathUtil.FORGEDIRS.length; i++) {
			if(ctx.getRandom(i) < ForgeConfigHandler.CORAL.chance) {
				if(!ctx.getState(MathUtil.FORGEDIRS_OFFSETS[i]).isOpaqueCube()) {
					int variation = ctx.getRandom(6);
					
					modelRenderer.render(
							renderer,
							coralModels.get(variation),
							MathUtil.ROTATION_FROM_UP[i],
							ctx.getCenter(),
							false,
							ShaderUtil.FILTER_TRUE,
							(m, i2, q) -> i2 == 4 ? crustIcons.get(variation + 1) : coralIcons.get(variation + 1 + (i2 & 1)),
							ShaderUtil.NO_POST);
					rendered = true;
				}
			}
		}
		return rendered;
	}
}