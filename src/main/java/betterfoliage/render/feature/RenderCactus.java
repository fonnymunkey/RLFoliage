package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.Model;
import betterfoliage.render.registry.CactusRegistry;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

public class RenderCactus extends RenderingHandler {
	public static final RenderCactus RENDER_CACTUS = new RenderCactus();
	
	private static final double cactusStemRadius = 0.4375;
	private static final Rotation[] cactusArmRotation = new Rotation[] {
			MathUtil.ROT90[EnumFacing.NORTH.ordinal()],
			MathUtil.ROT90[EnumFacing.SOUTH.ordinal()],
			MathUtil.ROT90[EnumFacing.EAST.ordinal()],
			MathUtil.ROT90[EnumFacing.WEST.ordinal()]
	};
	
	private final IconHolder iconCross = getIconStatic(BetterFoliage.MODID, "blocks/better_cactus");
	private final IconSet iconArm = getIconSet(BetterFoliage.MODID, "blocks/better_cactus_arm_%d");
	private final ModelHolder modelStem = getModelHolder(model -> {
		model.add(Model.horizontalRectangle(-cactusStemRadius, -cactusStemRadius, cactusStemRadius, cactusStemRadius, 0.5)
					   .scaleUV(cactusStemRadius * 2.0)
					   .flipped()
					   .move(0, -1, 0)
					   .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
					   .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT)));
		model.add(Model.horizontalRectangle(-cactusStemRadius, -cactusStemRadius, cactusStemRadius, cactusStemRadius, 0.5)
					   .scaleUV(cactusStemRadius * 2.0)
					   .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
					   .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT)));
		model.addAll(Model.verticalRectangle(-0.5, cactusStemRadius, 0.5, cactusStemRadius, -0.5, 0.5)
						  .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
						  .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT))
						  .toCross(EnumFacing.UP));
	});
	private final ModelSet modelCross = getModelSet(64, (model, i) -> {
		model.addAll(Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, -0.5 * 1.41, 0.5 * 1.41)
						  .setAoShader(ShaderUtil.edgeOrientedAuto(null, ShaderUtil.CORNER_AO_MAX_GREEN))
						  .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT))
						  .scale(1.4)
						  .transformV(v -> {
							  Double3 perturb = MathUtil.xzDisk(i).mul(ForgeConfigHandler.CACTUS.sizeVariation);
							  v.xyz.add(v.uv.u < 0.0 ? perturb : perturb.mul(-1.0));
							  return v;
						  })
						  .toCross(EnumFacing.UP));
	});
	private final ModelSet modelArm = getModelSet(64, (model, i) -> {
		model.addAll(Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.0, 1.0)
						  .scale(ForgeConfigHandler.CACTUS.size)
						  .move(0, 0.5, 0)
						  .setAoShader(ShaderUtil.faceOrientedAuto(EnumFacing.UP, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
						  .setFlatShader(ShaderUtil.faceOrientedAuto(EnumFacing.UP, ShaderUtil.CORNER_FLAT))
						  .toCross(EnumFacing.UP, q -> q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.CACTUS.hOffset))));
	});
	
	private RenderCactus() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s cactus arm textures", this.iconArm.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.CACTUS.enabled &&
				CactusRegistry.CACTUS_REGISTRY.get(ctx) != null;
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		
		CactusRegistry.CactusInfo cactusInfo = CactusRegistry.CACTUS_REGISTRY.get(ctx);
		if(cactusInfo == null) {
			logRenderError(ctx.getState(), ctx.getPos());
			return renderPrimary && renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		}
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading();
		
		if(renderPrimary) {
			modelRenderer.render(
					renderer,
					modelStem.model,
					MathUtil.IDENTITY,
					ctx.getCenter(),
					false,
					ShaderUtil.FILTER_TRUE,
					(m, i, q) -> (i == 0 ? cactusInfo.bottomTexture : i == 1 ? cactusInfo.topTexture : cactusInfo.sideResolver.resolve(m, i, q)),
					ShaderUtil.NO_POST);
			rendered = true;
		}
		
		if(!renderCutout) return rendered;
		
		modelRenderer.render(
				renderer,
				modelCross.get(ctx.getRandom(0)),
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> iconCross.icon,
				ShaderUtil.NO_POST);
		modelRenderer.render(
				renderer,
				modelArm.get(ctx.getRandom(1)),
				cactusArmRotation[ctx.getRandom(2)%4],
				ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> iconArm.get(ctx.getRandom(3)),
				ShaderUtil.NO_POST);
		return true;
	}
}