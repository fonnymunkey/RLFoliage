package betterfoliage.render.shader;

import betterfoliage.render.math.Int3;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;
import net.minecraft.util.EnumFacing;

public class FaceFlat implements Shader {
	private final EnumFacing face;
	
	public FaceFlat(EnumFacing face) {
		this.face = face;
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		int color = context.blockData(Int3.ZERO).color;
		vertex.shade(context.blockData(MathUtil.offset(this.face)).packedLight, color);
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return new FaceFlat(MathUtil.rotate(this.face, rot));
	}
}