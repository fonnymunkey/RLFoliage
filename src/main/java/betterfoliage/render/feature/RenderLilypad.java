package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.Int3;
import betterfoliage.render.model.Model;
import betterfoliage.render.shader.FlatOffsetNoColor;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class RenderLilypad extends RenderingHandler {
	public static final RenderLilypad RENDER_LILYPAD = new RenderLilypad();
	
	private final ModelHolder rootModel = getModelHolder(model -> model.addAll(
			Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, -1.5, -0.5)
				 .setFlatShader(new FlatOffsetNoColor(Int3.ZERO))
				 .toCross(EnumFacing.UP)));
	private final ModelHolder flowerModel = getModelHolder(model -> model.addAll(
			Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.0, 1.0)
				 .scale(0.5)
				 .move(0.0, -0.5, 0.0)
				 .setFlatShader(new FlatOffsetNoColor(Int3.ZERO))
				 .toCross(EnumFacing.UP)));
	private final IconSet rootIcon = getIconSet(BetterFoliage.MODID, "blocks/better_lilypad_roots_%d");
	private final IconSet flowerIcon = getIconSet(BetterFoliage.MODID, "blocks/better_lilypad_flower_%d");
	private final VectorSet perturbs = getVectorSet(64, i -> MathUtil.xzDisk(i).mul(ForgeConfigHandler.LILYPAD.hOffset));
	
	private RenderLilypad() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s lilypad root textures", this.rootIcon.size));
		log(Level.INFO, String.format("Registered %s lilypad flower textures", this.flowerIcon.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.LILYPAD.enabled &&
				renderCutout &&
				ForgeConfigHandler.BLOCKS.lilypadClassesMatcher.matchesClass(ctx.getBlock());
	}
	
	@Override
	public boolean render(BlockContext ctx, Supplier<Boolean> renderBase, Supplier<BufferBuilder> worldRenderer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderBase.get();
		if(!renderCutout) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		
		BufferBuilder renderer = worldRenderer.get();
		int[] rand = ctx.getSemiRandomArray(5);
		OptifineCompatWrapper.grass(renderer, ForgeConfigHandler.LILYPAD.shaderWind, () -> modelRenderer.render(
				renderer,
				rootModel.model,
				MathUtil.IDENTITY,
				ctx.getCenter().add(perturbs.get(rand[2])),
				true,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> rootIcon.get(rand[i & 1]),
				ShaderUtil.NO_POST));
		
		if(rand[3] < ForgeConfigHandler.LILYPAD.flowerChance) {
			modelRenderer.render(
					renderer,
					flowerModel.model,
					MathUtil.IDENTITY,
					ctx.getCenter().add(perturbs.get(rand[4])),
					true,
					ShaderUtil.FILTER_TRUE,
					(m, i, q) -> flowerIcon.get(rand[0]),
					ShaderUtil.NO_POST);
		}
		return true;
	}
}