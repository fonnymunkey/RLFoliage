package betterfoliage.render.feature.particle;

import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Pair;
import betterfoliage.render.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public abstract class AbstractEntityFX extends Particle {
	protected static final double[] SIN = new double[64];
	protected static final double[] COS = new double[64];
	
	static {
		for(int i = 0; i < 64; i++) {
			SIN[i] = Math.sin(Math.PI * 2.0 / 64.0 * (double)i);
			COS[i] = Math.cos(Math.PI * 2.0 / 64.0 * (double)i);
		}
	}
	
	protected final Pair<Double3,Double3> billboardRot = new Pair<>(new Double3(0, 0, 0), new Double3(0, 0, 0));
	protected final Double3 currentPos = new Double3(0, 0, 0);
	protected final Double3 prevPos = new Double3(0, 0, 0);
	protected final Double3 velocity = new Double3(0, 0, 0);
	
	protected AbstractEntityFX(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
		this.currentPos.setTo(this.posX, this.posY, this.posZ);
		this.prevPos.setTo(this.prevPosX, this.prevPosY, this.prevPosZ);
		this.velocity.setTo(this.motionX, this.motionY, this.motionZ);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.currentPos.setTo(this.posX, this.posY, this.posZ);
		this.prevPos.setTo(this.prevPosX, this.prevPosY, this.prevPosZ);
		this.velocity.setTo(this.motionX, this.motionY, this.motionZ);
		this.update();
		this.posX = this.currentPos.x;
		this.posY = this.currentPos.y;
		this.posZ = this.currentPos.z;
		this.motionX = this.velocity.x;
		this.motionY = this.velocity.y;
		this.motionZ = this.velocity.z;
	}
	
	public abstract void render(BufferBuilder worldRenderer, float partialTickTime);
	public abstract void update();
	public abstract boolean isValid();
	
	public void addIfValid() {
		if(this.isValid()) {
			Minecraft.getMinecraft().effectRenderer.addEffect(this);
		}
	}
	
	@Override
	public void renderParticle(BufferBuilder worldRenderer, Entity entity, float partialTickTime, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
		this.billboardRot.l.setTo(rotX + rotXY, rotZ, rotYZ + rotXZ);
		this.billboardRot.r.setTo(rotX - rotXY, -rotZ, rotYZ - rotXZ);
		this.render(worldRenderer, partialTickTime);
	}
	
	public void renderParticleQuad(BufferBuilder worldRenderer, float partialTickTime, int rotation, boolean isMirrored) {
		this.renderParticleQuad(worldRenderer, partialTickTime, this.currentPos, this.prevPos, this.particleScale, rotation, this.particleTexture, isMirrored, this.particleAlpha);
	}
	
	public void renderParticleQuad(BufferBuilder worldRenderer, float partialTickTime, double size, float alpha) {
		this.renderParticleQuad(worldRenderer, partialTickTime, this.currentPos, this.prevPos, size, 0, this.particleTexture, false, alpha);
	}
	
	public void renderParticleQuad(BufferBuilder worldRenderer, float partialTickTime, Double3 currentPos, Double3 prevPos, double size, TextureAtlasSprite icon, float alpha) {
		this.renderParticleQuad(worldRenderer, partialTickTime, currentPos, prevPos, size, 0, icon, false, alpha);
	}
	
	public void renderParticleQuad(BufferBuilder worldRenderer, float partialTickTime, Double3 currentPos, Double3 prevPos, double size, int rotation, TextureAtlasSprite icon, boolean isMirrored, float alpha) {
		double minU = isMirrored ? icon.getMinU() : icon.getMaxU();
		double maxU = isMirrored ? icon.getMaxU() : icon.getMinU();
		double minV = icon.getMinV();
		double maxV = icon.getMaxV();
		
		Double3 center = currentPos.minus(prevPos).mul(partialTickTime).add(prevPos).sub(interpPosX, interpPosY, interpPosZ);
		Double3 v1 = rotation == 0 ? this.billboardRot.l.times(size) : MathUtil.weight(this.billboardRot.l, COS[rotation & 63] * size, this.billboardRot.r, SIN[rotation & 63] * size);
		Double3 v2 = rotation == 0 ? this.billboardRot.r.times(size) : MathUtil.weight(this.billboardRot.l, -SIN[rotation & 63] * size, this.billboardRot.r, COS[rotation & 63] * size);
		
		int renderBrightness = this.getBrightnessForRender(partialTickTime);
		int brLow = (renderBrightness >> 16) & 65535;
		int brHigh = renderBrightness & 65535;
		
		worldRenderer
				.pos(center.x - v1.x, center.y - v1.y, center.z - v1.z)
				.tex(maxU, maxV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
				.lightmap(brLow, brHigh)
				.endVertex();
		worldRenderer
				.pos(center.x - v2.x, center.y - v2.y, center.z - v2.z)
				.tex(maxU, minV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
				.lightmap(brLow, brHigh)
				.endVertex();
		worldRenderer
				.pos(center.x + v1.x, center.y + v1.y, center.z + v1.z)
				.tex(minU, minV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
				.lightmap(brLow, brHigh)
				.endVertex();
		worldRenderer
				.pos(center.x + v2.x, center.y + v2.y, center.z + v2.z)
				.tex(minU, maxV)
				.color(this.particleRed, this.particleGreen, this.particleBlue, alpha)
				.lightmap(brLow, brHigh)
				.endVertex();
	}
	
	@Override
	public int getFXLayer() {
		return 1;
	}
	
	public void setColor(int color) {
		this.particleRed = (float)((color >> 16) & 255) / 255.0F;
		this.particleGreen = (float)((color >> 8) & 255) / 255.0F;
		this.particleBlue = (float)(color & 255) / 255.0F;
	}
}