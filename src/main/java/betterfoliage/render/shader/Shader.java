package betterfoliage.render.shader;

import betterfoliage.render.math.Rotation;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;

public interface Shader {
	void shade(ModelRenderer context, RenderVertex vertex);
	Shader rotate(Rotation rot);
}