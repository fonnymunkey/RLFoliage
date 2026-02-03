package betterfoliage.render.shader;

import betterfoliage.render.BlockContext;
import betterfoliage.render.math.Int3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;

public class FlatOffset implements Shader {
	private final Int3 offset;
	
	public FlatOffset(Int3 offset) {
		this.offset = offset;
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		BlockContext.BlockData data = context.blockData(this.offset);
		vertex.brightness = data.packedLight;
		vertex.setColor(data.color);
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return this;
	}
}