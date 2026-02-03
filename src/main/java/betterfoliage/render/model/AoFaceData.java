package betterfoliage.render.model;

import betterfoliage.mixin.AmbientOcclusionFaceAccessor;
import betterfoliage.render.BlockContext;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.math.Int3;
import betterfoliage.render.util.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.util.EnumFacing;

import java.util.BitSet;

public class AoFaceData {
	private final BlockModelRenderer.AmbientOcclusionFace AOF = AmbientOcclusionFaceAccessor.betterfoliage$callInit(Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer());
	private final AoData topLeft = new AoData();
	private final AoData topRight = new AoData();
	private final AoData bottomLeft = new AoData();
	private final AoData bottomRight = new AoData();
	private final AoData[] ordered;
	private final EnumFacing face;
	private final EnumFacing top;
	private final EnumFacing left;
	
	public AoFaceData(EnumFacing face) {
		switch(face) {
			case DOWN : { ordered = new AoData[] { this.topLeft, this.bottomLeft, this.bottomRight, this.topRight }; break; }
			case UP : { ordered = new AoData[] { this.bottomRight, this.topRight, this.topLeft, this.bottomLeft }; break; }
			case NORTH : { ordered = new AoData[] { this.bottomLeft, this.bottomRight, this.topRight, this.topLeft }; break; }
			case SOUTH : { ordered = new AoData[] { this.topLeft, this.bottomLeft, this.bottomRight, this.topRight }; break; }
			case WEST : { ordered = new AoData[] { this.bottomLeft, this.bottomRight, this.topRight, this.topLeft }; break; }
			default : { ordered = new AoData[] { this.topRight, this.topLeft, this.bottomLeft, this.bottomRight }; break; }
		}
		this.face = face;
		this.top = MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.l;
		this.left = MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.r;
	}
	
	public void update(Int3 offset, float multiplier) {
		BlockContext ctx = ModelRenderer.MODEL_RENDERER.get().BLOCK_CONTEXT;
		IBlockState state = ctx.getState(offset);
		float[] quadBounds = new float[12];
		BitSet flags = new BitSet(3);
		flags.set(0);
		
		this.AOF.updateVertexBrightness(ctx.getWorld(), state, ctx.getPos(offset), this.face, quadBounds, flags);
		for(int i = 0; i < this.ordered.length; i++) {
			this.ordered[i].set(this.AOF.vertexBrightness[i], this.AOF.vertexColorMultiplier[i] * multiplier);
		}
	}
	
	public AoData get(EnumFacing dir1, EnumFacing dir2) {
		boolean isTop = this.top == dir1 || this.top == dir2;
		boolean isLeft = this.left == dir1 || this.left == dir2;
		return isTop ? isLeft ? this.topLeft : this.topRight : isLeft ? this.bottomLeft : this.bottomRight;
	}
}