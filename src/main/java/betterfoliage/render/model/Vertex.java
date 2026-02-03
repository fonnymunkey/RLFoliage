package betterfoliage.render.model;

import betterfoliage.render.math.Double3;
import betterfoliage.render.shader.Shader;
import betterfoliage.render.util.ShaderUtil;

public class Vertex {
	public final Double3 xyz;
	public UV uv;
	public Shader aoShader;
	public Shader flatShader;
	
	//0 0 0 , 0 0 , NoShader, NoShader
	public Vertex(Double3 xyz, UV uv, Shader aoShader, Shader flatShader) {
		this.xyz = xyz;
		this.uv = uv;
		this.aoShader = aoShader;
		this.flatShader = flatShader;
	}
	
	public Vertex(Double3 xyz, UV uv) {
		this(xyz, uv, ShaderUtil.NO_SHADER, ShaderUtil.NO_SHADER);
	}
}