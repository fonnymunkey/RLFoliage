package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.*;
import betterfoliage.render.model.Model;
import betterfoliage.render.registry.GrassRegistry;
import betterfoliage.render.generator.ShortGrassGenerator;
import betterfoliage.render.shader.FlatOffset;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

import java.util.function.BiConsumer;

public class RenderGrass extends RenderingHandler {
	public static final RenderGrass RENDER_GRASS = new RenderGrass();
	
	private static final int[] uvRot = new int[] { 2, 2, 3, 0, 3, 1 };
	
	public static BiConsumer<Model,Integer> grassTopQuads(double heightMin, double heightMax) {
		return (model, i) -> model.addAll(
				Model.verticalRectangle(-0.5, 0.5, 0.5, -0.5, 0.5, 0.5 + MathUtil.random(heightMin, heightMax))
					 .setAoShader(ShaderUtil.faceOrientedAuto(EnumFacing.UP, ShaderUtil.cornerAo(EnumFacing.Axis.Y)))
					 .setFlatShader(new FlatOffset(new Int3(0, 1, 0)))
					 .toCross(EnumFacing.UP, q -> q.move(MathUtil.xzDisk(i).mul(ForgeConfigHandler.SHORTGRASS.hOffset))));
	}
	
	public final ModelHolder FULL_CUBE = getModelHolder(model -> {
		for(EnumFacing face : MathUtil.FORGEDIRS) {
			model.add(Model.faceQuad(face)
								 .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(face.getAxis())))
								 .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT_DIFFUSE)));
		}
	});
	
	private final SimplexNoise noise = getSimplexNoise();
	private final IconSet normalLongIcons = getIconSet(BetterFoliage.MODID, "blocks/better_grass_long_%d");
	private final IconSet normalShortIcons = getIconSet(BetterFoliage.MODID, "blocks/better_grass_short_%d");
	private final IconSet snowedIcons = getIconSet(BetterFoliage.MODID, "blocks/better_grass_snowed_%d");
	private final IconHolder normalGenIcon = getIconStatic(ShortGrassGenerator.SHORT_GRASS_GENERATOR.generatedResource("minecraft:blocks/tallgrass", new Pair<>("snowed", false)));
	private final IconHolder snowedGenIcon = getIconStatic(ShortGrassGenerator.SHORT_GRASS_GENERATOR.generatedResource("minecraft:blocks/tallgrass", new Pair<>("snowed", true)));
	private final IconHolder snowFullIcon = getIconStatic("minecraft", "blocks/snow");
	private final ModelSet grassModels = getModelSet(64, grassTopQuads(ForgeConfigHandler.SHORTGRASS.heightMin, ForgeConfigHandler.SHORTGRASS.heightMax));
	
	private RenderGrass() {
		super();
		RenderingHandler.RENDERERS.add(this);
	}
	
	@Override
	public void afterPreStitch() {
		log(Level.INFO, String.format("Registered %s long grass textures", this.normalLongIcons.size));
		log(Level.INFO, String.format("Registered %s short grass textures", this.normalShortIcons.size));
		log(Level.INFO, String.format("Registered %s snowed grass textures", this.snowedIcons.size));
	}
	
	@Override
	public boolean isEligible(BlockContext ctx, boolean renderPrimary, boolean renderCutout) {
		boolean renderConnected = ForgeConfigHandler.CONNECTEDGRASS.enabled && renderPrimary;
		boolean renderShortgrass = ForgeConfigHandler.SHORTGRASS.enabled && renderCutout && ForgeConfigHandler.SHORTGRASS.population > 0;
		return (renderConnected || renderShortgrass) && GrassRegistry.GRASS_REGISTRY.get(ctx) != null;
	}
	
	@Override
	public boolean render(BlockContext ctx, BlockRendererDispatcher dispatcher, BufferBuilder renderer, BlockRenderLayer layer, boolean renderPrimary, boolean renderCutout) {
		boolean rendered = false;
		
		GrassRegistry.GrassInfo grassInfo = GrassRegistry.GRASS_REGISTRY.get(ctx);
		if(grassInfo == null) {
			logRenderError(ctx.getState(), ctx.getPos());
			return renderPrimary && renderWorldBlockBase(ctx, dispatcher, renderer, layer);
		}
		int blockColor = OptifineCompatWrapper.getBlockColor(ctx, ctx.getState(), ctx.getPos());
		ModelRenderer modelRenderer = ModelRenderer.MODEL_RENDERER.get();
		IBlockState up = ctx.getState(0, 1, 0);
		boolean isSnowed = RenderUtil.isSnow(up.getMaterial());
		
		if(renderPrimary) {
			boolean connectedGrass = ForgeConfigHandler.CONNECTEDGRASS.enabled && (!isSnowed || ForgeConfigHandler.CONNECTEDGRASS.snowEnabled);
			if(connectedGrass) {
				Block down = ctx.getState(0, -1, 0).getBlock();
				connectedGrass = ForgeConfigHandler.BLOCKS.dirtClassesMatcher.matchesClass(down) || ForgeConfigHandler.BLOCKS.grassClassesMatcher.matchesClass(down);
			}
			
			if(connectedGrass) {
				modelRenderer.updateShading(Int3.ZERO, RenderUtil.ALL_FACES);
				boolean[] isHidden  = new boolean[6];
				for(int i = 0; i < MathUtil.FORGEDIRS.length; i++) {
					isHidden[i] = ctx.getState(MathUtil.offset(MathUtil.FORGEDIRS[i])).isOpaqueCube();
				}
				int[] rand = ctx.getSemiRandomArray(1);
				OptifineCompatWrapper.renderAs(ctx.getState(), EnumBlockRenderType.MODEL, renderer, true, () -> modelRenderer.render(
						renderer,
						FULL_CUBE.model,
						MathUtil.IDENTITY,
						ctx.getCenter(),
						false,
						(i,q) -> !isHidden[i],
						(m, i, q) -> isSnowed ? snowFullIcon.icon : grassInfo.grassTopTexture,
						(rv, m, i, q, i2, v) -> {
							rv.rotateUV(i == 0 || i == 1 ? rand[0] : uvRot[i]);
							if(!isSnowed) {
								if(m.aoEnabled && grassInfo.overrideColor == null) {
									rv.multiplyColor(blockColor);
								}
							}
							else {
								if(!m.aoEnabled) {
									//Redo diffuse color/shading as it was previously set with grass colors rather than snow
									rv.setColor(RenderUtil.colorMult(16777215, OptifineCompatWrapper.getDiffusedMult(MathUtil.FORGEDIRS[i])));
								}
							}
						}));
				rendered = true;
			}
			else {
				rendered = renderWorldBlockBase(ctx, dispatcher, renderer, layer);
				if(renderCutout) modelRenderer.updateShading(Int3.ZERO, RenderUtil.TOP_ONLY);
			}
		}
		
		if(!renderCutout) return rendered;
		
		if(!ForgeConfigHandler.SHORTGRASS.enabled) return rendered;
		if(isSnowed && !ForgeConfigHandler.SHORTGRASS.snowEnabled) return rendered;
		if(!isSnowed && up.getMaterial() != Material.AIR) return rendered;
		if(ForgeConfigHandler.SHORTGRASS.population < 64 && noise.get(ctx.getPos()) >= ForgeConfigHandler.SHORTGRASS.population) return rendered;
		
		IconSet iconSet = isSnowed ? this.snowedIcons : ForgeConfigHandler.SHORTGRASS.longerGrass ? this.normalLongIcons : this.normalShortIcons;
		IconHolder iconGen = isSnowed ? this.snowedGenIcon : this.normalGenIcon;
		
		if(!renderPrimary) modelRenderer.updateShading(Int3.ZERO, RenderUtil.TOP_ONLY);
		
		int[] rand = ctx.getSemiRandomArray(2);
		OptifineCompatWrapper.grass(renderer, ForgeConfigHandler.SHORTGRASS.shaderWind, () -> modelRenderer.render(
				renderer,
				grassModels.get(rand[0]),
				MathUtil.IDENTITY,
				isSnowed ? ctx.getCenter().add(0, 0.0625, 0) : ctx.getCenter(),
				false,
				ShaderUtil.FILTER_TRUE,
				(m, i, q) -> ForgeConfigHandler.SHORTGRASS.useGenerated ? iconGen.icon : iconSet.get(rand[i & 1]),
				(rv, m, i, q, i2, v) -> {
					if(isSnowed) rv.setGrey(1.0F);
					else rv.multiplyColor(grassInfo.overrideColor == null ? blockColor : grassInfo.overrideColor);
				}));
		return true;
	}
}