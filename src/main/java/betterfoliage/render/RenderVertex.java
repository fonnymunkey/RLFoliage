package betterfoliage.render;

import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.AoData;
import betterfoliage.render.model.Vertex;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class RenderVertex {
	public double x = 0.0D;
	public double y = 0.0D;
	public double z = 0.0D;
	public double u = 0.0D;
	public double v = 0.0D;
	public int brightness = 0;
	public float red = 0.0F;
	public float green = 0.0F;
	public float blue = 0.0F;
	
	public RenderVertex init(Vertex vertex, Rotation rot, Double3 trans) {
		Double3 res = vertex.xyz.rotate(rot).add(trans);
		this.x = res.x;
		this.y = res.y;
		this.z = res.z;
		return this;
	}
	
	public RenderVertex init(Vertex vertex) {
		this.x = vertex.xyz.x;
		this.y = vertex.xyz.y;
		this.z = vertex.xyz.z;
		this.u = vertex.uv.u;
		this.v = vertex.uv.v;
		return this;
	}
	
	public RenderVertex translate(Double3 trans) {
		this.x += trans.x;
		this.y += trans.y;
		this.z += trans.z;
		return this;
	}
	
	public RenderVertex rotate(Rotation rot) {
		if(rot == MathUtil.IDENTITY) return this;
		double xVal = rot.rotatedComponent(EnumFacing.EAST, this.x, this.y, this.z);
		double yVal = rot.rotatedComponent(EnumFacing.UP, this.x, this.y, this.z);
		double zVal = rot.rotatedComponent(EnumFacing.SOUTH, this.x, this.y, this.z);
		this.x = xVal;
		this.y = yVal;
		this.z = zVal;
		return this;
	}
	
	public RenderVertex rotateUV(int n) {
		switch(n%4) {
			case 1: { double t = this.v; this.v = -this.u; this.u = t; return this; }
			case 2: { this.u = -this.u; this.v = -this.v; return this; }
			case 3: { double t = -this.v; this.v = this.u; this.u = t; return this; }
			default: return this;
		}
	}
	
	public void mirrorUV(boolean mirrorU, boolean mirrorV) {
		if(mirrorU) this.u = -this.u;
		if(mirrorV) this.v = -this.v;
	}
	
	public RenderVertex setIcon(TextureAtlasSprite icon) {
		this.u = (icon.getMaxU() - icon.getMinU()) * (this.u + 0.5) + icon.getMinU();
		this.v = (icon.getMaxV() - icon.getMinV()) * (this.v + 0.5) + icon.getMinV();
		return this;
	}
	
	public void setGrey(float level) {
		float grey = Math.min((this.red + this.green + this.blue) * 0.333F * level, 1.0F);
		this.red = grey;
		this.green = grey;
		this.blue = grey;
	}
	
	public void multiplyColor(int color) {
		this.red *= (float)(color >> 16 & 255) / 255.0F;
		this.green *= (float)(color >> 8 & 255) / 255.0F;
		this.blue *= (float)(color & 255) / 255.0F;
	}
	
	public void setColor(int color) {
		this.red = (float)(color >> 16 & 255) / 255.0F;
		this.green = (float)(color >> 8 & 255) / 255.0F;
		this.blue = (float)(color & 255) / 255.0F;
	}
	
	public void shade(AoData shading) {
		this.brightness = shading.brightness;
		this.red = shading.red;
		this.green = shading.green;
		this.blue = shading.blue;
	}
	
	//weight1 = 0.5F, weight2 = 0.5F
	public void shade(AoData shading1, AoData shading2, float weight1, float weight2) {
		this.red = Math.min(shading1.red * weight1 + shading2.red * weight2, 1.0F);
		this.green = Math.min(shading1.green * weight1 + shading2.green * weight2, 1.0F);
		this.blue = Math.min(shading1.blue * weight1 + shading2.blue * weight2, 1.0F);
		this.brightness = RenderUtil.brWeighted(shading1.brightness, weight1, shading2.brightness, weight2);
	}
	
	public void shade(int brightness, int color) {
		this.brightness = brightness;
		this.setColor(color);
	}
}