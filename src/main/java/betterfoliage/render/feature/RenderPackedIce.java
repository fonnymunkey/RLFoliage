package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.model.Model;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class RenderPackedIce extends RenderingHandler {
	public static final RenderPackedIce RENDER_PACKED_ICE = new RenderPackedIce();
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet packedIceIcon = getIconSet(BetterFoliage.MODID, "blocks/better_packedice_%d");
	private final ModelSet packedIceModel = getModelSet(64, (model, i) -> model.addAll(
			Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, -0.5 - MathUtil.random(ForgeConfigHandler.PACKEDICE.heightMin, ForgeConfigHandler.PACKEDICE.heightMax), -0.5)
				 .setAoShader(ShaderUtil.faceOrientedAuto(EnumFacing.DOWN, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
				 .setFlatShader(ShaderUtil.faceOrientedAuto(EnumFacing.DOWN, ShaderUtil.CORNER_FLAT))
				 .toCross(EnumFacing.UP, q -> q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.PACKEDICE.hOffset)))));
	
	private RenderPackedIce() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s packed ice textures", this.packedIceIcon.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.PACKEDICE.enabled &&
				renderCutout &&
				ForgeConfigHandler.PACKEDICE.population > 0 &&
				ForgeConfigHandler.BLOCKS.packediceClassesMatcher.matchesClass(ctx.getBlock()) &&
				(ForgeConfigHandler.PACKEDICE.population >= 64 || ForgeConfigHandler.PACKEDICE.population > this.noise.get(ctx.getPos()));
	}
	
	@Override
	public boolean render(BlockContext ctx, Supplier<Boolean> renderBase, Supplier<BufferBuilder> worldRenderer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderBase.get();
		if(!renderCutout) return rendered;
		
		if(ctx.getState(0, -1, 0).isOpaqueCube()) return rendered;
		
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(EnumFacing.DOWN);
		
		int[] rand = ctx.getSemiRandomArray(2);
		BufferBuilder renderer = worldRenderer.get();
		modelRenderer.render(
				renderer,
				packedIceModel.get(rand[0]),
				MathUtil.IDENTITY,
				ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> packedIceIcon.get(rand[i & 1]),
				ShaderUtil.NO_POST);
		return true;
	}
}