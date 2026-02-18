package betterfoliage.render.feature.particle;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.model.HSB;
import betterfoliage.render.registry.LeafParticleRegistry;
import betterfoliage.render.registry.LeafRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFallingLeavesModernFX extends AbstractEntityFX {
	private static final float biomeBrightnessMultiplier = 0.5F;
	
	private final boolean isMirrored;
	private int particleRot;
	
	private float rotSpeed;
	private float roll;
	private final float spinAcceleration;
	private final double swirlPeriod;
	private static final float angleTo128rad = 128.0F / 360.0F;
	
	public EntityFallingLeavesModernFX(World worldIn, BlockPos pos, IBlockState state) {
		super(worldIn, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
		
		this.isMirrored = this.rand.nextBoolean();
		this.particleRot = 0;
		
		this.rotSpeed = angleTo128rad * (this.rand.nextBoolean() ? 30.0F : -30.0F);
		this.spinAcceleration = angleTo128rad * (this.rand.nextBoolean() ? 5.0F : -5.0F);
		this.particleMaxAge = 300;
		this.particleScale = (8.0F/3.0F) * (this.rand.nextBoolean() ? 0.05F : 0.075F) * (float)ForgeConfigHandler.FALLINGLEAVES.size;
		this.particleGravity = 0.00021F;
		this.motionY = -0.021;
		this.swirlPeriod = angleTo128rad * (1000.0F + this.rand.nextFloat() * 3000.0F);
		
		int blockColor = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, worldIn, pos, 0);
		LeafRegistry.LeafInfo leafInfo = LeafRegistry.LEAF_REGISTRY.get(state, worldIn, pos);
		if(leafInfo == null) {
			this.particleTexture = LeafParticleRegistry.LEAF_PARTICLE_REGISTRY.get("default").get(this.rand.nextInt(1024));
			this.setColor(blockColor);
		}
		else {
			this.particleTexture = leafInfo.particleTextures().get(this.rand.nextInt(1024));
			calculateParticleColor(leafInfo.averageColor, blockColor);
		}
	}
	
	@Override
	public boolean isValid() {
		return this.particleTexture != null;
	}
	
	@Override
	public void update() {
		if(this.onGround || this.particleAge > 1 && (this.motionX == 0.0F || this.motionZ == 0.0F)) {
			this.velocity.setTo(0.0, 0.0, 0.0);
			this.particleAge = this.particleMaxAge;
		}
		else {
			double agePerc = Math.min((float)this.particleAge / 300.0F, 1.0F);
			double d0 = agePerc * COS[(int)(agePerc * this.swirlPeriod) & 127] * 10.0D;
			double d1 = agePerc * SIN[(int)(agePerc * this.swirlPeriod) & 127] * 10.0D;
			this.velocity.mul(1.0 / 0.98).add(0.0025D * d0, -0.00021D, 0.0025D * d1);
			this.rotSpeed += this.spinAcceleration / 20.0F;
			this.roll += this.rotSpeed / 20.0F;
			this.particleRot = (int)this.roll & 127;
		}
	}
	
	@Override
	public void render(BufferBuilder worldRenderer, float partialTickTime) {
		this.renderParticleQuad(worldRenderer, partialTickTime, this.particleRot, this.isMirrored);
	}
	
	private void calculateParticleColor(int textureAvgColor, int blockColor) {
		HSB texture = HSB.fromColor(textureAvgColor);
		HSB block = HSB.fromColor(blockColor);
		
		float weightTex = texture.saturation / (texture.saturation + block.saturation);
		float weightBlock = 1.0F - weightTex;
		
		HSB particle = new HSB(
				weightTex * texture.hue + weightBlock * block.hue,
				weightTex * texture.saturation + weightBlock * block.saturation,
				weightTex * texture.brightness + weightBlock * block.brightness * biomeBrightnessMultiplier);
		this.setColor(particle.asColor());
	}
}