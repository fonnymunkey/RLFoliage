package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Int3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.Model;
import betterfoliage.render.registry.LeafRegistry;
import betterfoliage.render.shader.FlatOffset;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RenderLeaves extends RenderingHandler {
	public static final RenderLeaves RENDER_LEAVES = new RenderLeaves();
	
	private static final Rotation[] NORMAL_LEAVES_ROT = new Rotation[] { MathUtil.IDENTITY };
	private static final Rotation[] DENSE_LEAVES_ROT = new Rotation[] { MathUtil.IDENTITY, MathUtil.ROT90[EnumFacing.EAST.ordinal()], MathUtil.ROT90[EnumFacing.SOUTH.ordinal()] };

	private final ModelHolder leavesModel = getModelHolder(m -> m.addAll(
			Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, -0.5 * 1.41, 0.5 * 1.41)
				 .setAoShader(ShaderUtil.edgeOrientedAuto(null, ShaderUtil.CORNER_AO_MAX_GREEN))
				 .setFlatShader(new FlatOffset(Int3.ZERO))
				 .scale(ForgeConfigHandler.LEAVES.size)
				 .toCross(EnumFacing.UP)));
	private final IconSet snowedIcon = getIconSet(BetterFoliage.MODID, "blocks/better_leaves_snowed_%d");
	private final VectorSet perturbs = getVectorSet(64, i -> {
		double angle = Math.PI * 2.0 * (double)i / 64.0;
		return new Double3(Math.cos(angle) * ForgeConfigHandler.LEAVES.hOffset, 0.0, Math.sin(angle) * ForgeConfigHandler.LEAVES.hOffset)
				.add(0, MathUtil.random(-1.0, 1.0) * ForgeConfigHandler.LEAVES.vOffset, 0);
	});
	
	private RenderLeaves() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		return ForgeConfigHandler.LEAVES.enabled &&
				renderCutout &&
				LeafRegistry.LEAF_REGISTRY.get(ctx) != null &&
				(!ForgeConfigHandler.LEAVES.hideInternal ||
				!ctx.isSurroundedBy(s -> s.isFullCube() || s.getMaterial() == Material.LEAVES));
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		if(renderPrimary) rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		if(!renderCutout) return rendered;
		
		IBlockState state = ctx.getState();
		BlockPos pos = ctx.getPos();
		LeafRegistry.LeafInfo leafInfo = LeafRegistry.LEAF_REGISTRY.get(state, ctx.getWorld(), pos);
		if(leafInfo == null) {
			logRenderError(ctx.getState(), ctx.getPos());
			return rendered;
		}
		int blockColor = OptifineCompatWrapper.getBlockColor(ctx, state, pos);
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		modelRenderer.updateShading(Int3.ZERO, RenderUtil.ALL_FACES);
		IBlockState up = ctx.getState(0, 1, 0);
		boolean isSnowed = RenderUtil.isSnow(up.getMaterial());
		
		OptifineCompatWrapper.leaves(renderer, ForgeConfigHandler.LEAVES.shaderWind, () -> {
			int[] rand = ctx.getSemiRandomArray(2);
			Rotation[] rots = ForgeConfigHandler.LEAVES.dense ? DENSE_LEAVES_ROT : NORMAL_LEAVES_ROT;
			for(Rotation rotation : rots) {
				modelRenderer.render(
						renderer,
						leavesModel.model,
						rotation,
						ctx.getCenter().add(perturbs.get(rand[0])),
						false,
						ShaderUtil.FILTER_TRUE,
						(m, i, q) -> leafInfo.roundLeafTexture,
						(rv, m, i, q, i2, v) ->
								rv.rotateUV(rand[1]).multiplyColor(blockColor));
			}
			if(isSnowed && ForgeConfigHandler.LEAVES.snowEnabled) {
				modelRenderer.render(
						renderer,
						leavesModel.model,
						MathUtil.IDENTITY,
						ctx.getCenter().add(perturbs.get(rand[0])),
						false,
						ShaderUtil.FILTER_TRUE,
						(m, i, q) -> snowedIcon.get(rand[1]),
						ShaderUtil.WHITE_WASH);
			}
		});
		return true;
	}
}