package betterfoliage.render.shader;

import betterfoliage.render.BlockContext;
import betterfoliage.render.math.Int3;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.AoData;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.util.EnumFacing;

public class CornerSingleFallback implements Shader {
	private final EnumFacing face;
	private final EnumFacing dir1;
	private final EnumFacing dir2;
	private final EnumFacing fallbackDir;
	private final float fallbackDimming;
	private final Int3 offset;
	
	public CornerSingleFallback(EnumFacing face, EnumFacing dir1, EnumFacing dir2, EnumFacing fallbackDir, float fallbackDimming) {
		this.face = face;
		this.dir1 = dir1;
		this.dir2 = dir2;
		this.fallbackDir = fallbackDir;
		this.fallbackDimming = fallbackDimming;
		this.offset = new Int3(fallbackDir);
	}
	
	public CornerSingleFallback(EnumFacing face, EnumFacing dir1, EnumFacing dir2, EnumFacing fallbackDir) {
		this(face, dir1, dir2, fallbackDir, 0.5F);
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		AoData shading = context.aoShading(face, dir1, dir2);
		if(shading.valid) vertex.shade(shading);
		else {
			BlockContext.BlockData data = context.blockData(this.offset);
			vertex.shade(RenderUtil.brMul(data.packedLight, this.fallbackDimming), RenderUtil.colorMult(data.color, this.fallbackDimming));
		}
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return new CornerSingleFallback(
				MathUtil.rotate(this.face, rot),
				MathUtil.rotate(this.dir1, rot),
				MathUtil.rotate(this.dir2, rot),
				MathUtil.rotate(this.fallbackDir, rot),
				this.fallbackDimming);
	}
}