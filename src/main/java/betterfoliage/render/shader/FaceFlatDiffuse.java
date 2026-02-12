package betterfoliage.render.shader;

import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.render.ModelRenderer;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.math.Int3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.util.EnumFacing;

public class FaceFlatDiffuse implements Shader {
	private final EnumFacing face;
	
	public FaceFlatDiffuse(EnumFacing face) {
		this.face = face;
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		int color = context.blockData(Int3.ZERO).color;
		vertex.shade(context.blockData(MathUtil.offset(this.face)).packedLight, RenderUtil.colorMult(color, OptifineCompatWrapper.getDiffusedMult(this.face)));
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return new FaceFlatDiffuse(MathUtil.rotate(this.face, rot));
	}
}