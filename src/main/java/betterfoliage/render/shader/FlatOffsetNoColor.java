package betterfoliage.render.shader;

import betterfoliage.render.math.Int3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;

public class FlatOffsetNoColor implements Shader {
	private final Int3 offset;
	
	public FlatOffsetNoColor(Int3 offset) {
		this.offset = offset;
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		vertex.brightness = context.blockData(this.offset).packedLight;
		vertex.red = 1.0F;
		vertex.green = 1.0F;
		vertex.blue = 1.0F;
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return this;
	}
}