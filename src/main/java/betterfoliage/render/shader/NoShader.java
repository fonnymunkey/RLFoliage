package betterfoliage.render.shader;

import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.AoData;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;

public class NoShader implements Shader {
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		vertex.shade(AoData.BLACK);
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return this;
	}
}