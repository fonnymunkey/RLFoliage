package betterfoliage.render.model;

import betterfoliage.render.math.*;
import betterfoliage.render.shader.Shader;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Quad {
	public Vertex v1;
	public Vertex v2;
	public Vertex v3;
	public Vertex v4;
	
	public Quad(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.v4 = v4;
	}
	
	public Vertex getVert(int n) {
		switch(n%4) {
			case 0: return this.v1;
			case 1: return this.v2;
			case 2: return this.v3;
			default: return this.v4;
		}
	}
	
	public Double3 normal() {
		return this.v2.xyz.minus(this.v1.xyz).cross(this.v4.xyz.minus(this.v1.xyz)).normalize();
	}
	
	public Quad transformV(Function<Vertex,Vertex> trans) {
		this.v1 = trans.apply(this.v1);
		this.v2 = trans.apply(this.v2);
		this.v3 = trans.apply(this.v3);
		this.v4 = trans.apply(this.v4);
		return this;
	}
	
	public Quad transformVI(BiFunction<Vertex,Integer,Vertex> trans) {
		this.v1 = trans.apply(this.v1, 0);
		this.v2 = trans.apply(this.v2, 1);
		this.v3 = trans.apply(this.v3, 2);
		this.v4 = trans.apply(this.v4, 3);
		return this;
	}
	
	public Quad move(double x, double y, double z) {
		this.v1.xyz.add(x, y, z);
		this.v2.xyz.add(x, y, z);
		this.v3.xyz.add(x, y, z);
		this.v4.xyz.add(x, y, z);
		return this;
	}
	
	public Quad move(Double3 trans) {
		this.v1.xyz.add(trans);
		this.v2.xyz.add(trans);
		this.v3.xyz.add(trans);
		this.v4.xyz.add(trans);
		return this;
	}
	
	public Quad move(Pair<Double,EnumFacing> trans) {
		return this.move(new Double3(trans.r).mul(trans.l));
	}
	
	public Quad scale(double scale) {
		this.v1.xyz.mul(scale);
		this.v2.xyz.mul(scale);
		this.v3.xyz.mul(scale);
		this.v4.xyz.mul(scale);
		return this;
	}
	
	public Quad scale(Double3 scale) {
		this.v1.xyz.mul(scale);
		this.v2.xyz.mul(scale);
		this.v3.xyz.mul(scale);
		this.v4.xyz.mul(scale);
		return this;
	}
	
	public Quad scaleUV(double scale) {
		this.v1.uv = new UV(this.v1.uv.u * scale, this.v1.uv.v * scale);
		this.v2.uv = new UV(this.v2.uv.u * scale, this.v2.uv.v * scale);
		this.v3.uv = new UV(this.v3.uv.u * scale, this.v3.uv.v * scale);
		this.v4.uv = new UV(this.v4.uv.u * scale, this.v4.uv.v * scale);
		return this;
	}
	
	public Quad rotate(Rotation rot) {
		this.v1.xyz.rotateMut(rot);
		this.v1.aoShader.rotate(rot);
		this.v1.flatShader.rotate(rot);
		this.v2.xyz.rotateMut(rot);
		this.v2.aoShader.rotate(rot);
		this.v2.flatShader.rotate(rot);
		this.v3.xyz.rotateMut(rot);
		this.v3.aoShader.rotate(rot);
		this.v3.flatShader.rotate(rot);
		this.v4.xyz.rotateMut(rot);
		this.v4.aoShader.rotate(rot);
		this.v4.flatShader.rotate(rot);
		return this;
	}
	
	public Quad rotateImmut(Rotation rot) {
		return new Quad(new Vertex(this.v1.xyz.rotate(rot), this.v1.uv, this.v1.aoShader.rotate(rot), this.v1.flatShader.rotate(rot)),
						new Vertex(this.v2.xyz.rotate(rot), this.v2.uv, this.v2.aoShader.rotate(rot), this.v2.flatShader.rotate(rot)),
						new Vertex(this.v3.xyz.rotate(rot), this.v3.uv, this.v3.aoShader.rotate(rot), this.v3.flatShader.rotate(rot)),
						new Vertex(this.v4.xyz.rotate(rot), this.v4.uv, this.v4.aoShader.rotate(rot), this.v4.flatShader.rotate(rot)));
	}
	
	public Quad rotateUV(int n) {
		this.v1.uv = this.v1.uv.rotate(n);
		this.v2.uv = this.v2.uv.rotate(n);
		this.v3.uv = this.v3.uv.rotate(n);
		this.v4.uv = this.v4.uv.rotate(n);
		return this;
	}
	
	public Quad clampUV(double minU, double maxU, double minV, double maxV) {
		this.v1.uv = this.v1.uv.clamp(minU, maxU, minV, maxV);
		this.v2.uv = this.v2.uv.clamp(minU, maxU, minV, maxV);
		this.v3.uv = this.v3.uv.clamp(minU, maxU, minV, maxV);
		this.v4.uv = this.v4.uv.clamp(minU, maxU, minV, maxV);
		return this;
	}
	
	public Quad mirrorUV(boolean mirrorU, boolean mirrorV) {
		this.v1.uv = this.v1.uv.mirror(mirrorU, mirrorV);
		this.v2.uv = this.v2.uv.mirror(mirrorU, mirrorV);
		this.v3.uv = this.v3.uv.mirror(mirrorU, mirrorV);
		this.v4.uv = this.v4.uv.mirror(mirrorU, mirrorV);
		return this;
	}
	
	public Quad setAoShader(ShaderUtil.ShaderFactory factory) {
		this.v1.aoShader = factory.create(this, this.v1);
		this.v2.aoShader = factory.create(this, this.v2);
		this.v3.aoShader = factory.create(this, this.v3);
		this.v4.aoShader = factory.create(this, this.v4);
		return this;
	}
	
	public Quad setAoShader(Shader shader) {
		this.v1.aoShader = shader;
		this.v2.aoShader = shader;
		this.v3.aoShader = shader;
		this.v4.aoShader = shader;
		return this;
	}
	
	public Quad setFlatShader(ShaderUtil.ShaderFactory factory) {
		this.v1.flatShader = factory.create(this, this.v1);
		this.v2.flatShader = factory.create(this, this.v2);
		this.v3.flatShader = factory.create(this, this.v3);
		this.v4.flatShader = factory.create(this, this.v4);
		return this;
	}
	
	public Quad setFlatShader(Shader shader) {
		this.v1.flatShader = shader;
		this.v2.flatShader = shader;
		this.v3.flatShader = shader;
		this.v4.flatShader = shader;
		return this;
	}
	
	public Quad flipped() {
		Vertex v1t = this.v1;
		Vertex v2t = this.v2;
		Vertex v3t = this.v3;
		Vertex v4t = this.v4;
		this.v1 = v4t;
		this.v2 = v3t;
		this.v3 = v2t;
		this.v4 = v1t;
		return this;
	}
	
	public Quad cycleVertices(int n) {
		switch(n%4) {
			case 1: {
				Vertex v1t = this.v1;
				Vertex v2t = this.v2;
				Vertex v3t = this.v3;
				Vertex v4t = this.v4;
				this.v1 = v2t;
				this.v2 = v3t;
				this.v3 = v4t;
				this.v4 = v1t;
				return this;
			}
			case 2: {
				Vertex v1t = this.v1;
				Vertex v2t = this.v2;
				Vertex v3t = this.v3;
				Vertex v4t = this.v4;
				this.v1 = v3t;
				this.v2 = v4t;
				this.v3 = v1t;
				this.v4 = v2t;
				return this;
			}
			case 3: {
				Vertex v1t = this.v1;
				Vertex v2t = this.v2;
				Vertex v3t = this.v3;
				Vertex v4t = this.v4;
				this.v1 = v4t;
				this.v2 = v1t;
				this.v3 = v2t;
				this.v4 = v3t;
				return this;
			}
			default: return this;
		}
	}
	
	public List<Quad> toCross(EnumFacing rotAxis, Function<Quad, Quad> trans) {
		List<Quad> list = new ArrayList<>();
		for(int i = 0; i < 4; i++ ) {
			list.add(trans.apply(this.rotateImmut(MathUtil.ROT90[rotAxis.ordinal()].times(i)).mirrorUV(i > 1, false)));
		}
		return list;
	}
	
	public List<Quad> toCross(EnumFacing rotAxis) {
		return this.toCross(rotAxis, Function.identity());
	}
}