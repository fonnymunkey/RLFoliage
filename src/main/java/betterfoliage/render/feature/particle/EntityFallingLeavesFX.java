package betterfoliage.render.feature.particle;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.model.HSB;
import betterfoliage.render.registry.LeafParticleRegistry;
import betterfoliage.render.registry.LeafRegistry;
import betterfoliage.render.util.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class EntityFallingLeavesFX extends AbstractEntityFX {
	private static final float biomeBrightnessMultiplier = 0.5F;
	
	private final boolean isMirrored;
	private int particleRot;
	private boolean rotPositive = true;
	private boolean wasCollided = false;
	
	public EntityFallingLeavesFX(World worldIn, BlockPos pos, IBlockState state) {
		super(worldIn, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
		
		this.isMirrored = (this.rand.nextInt() & 1) == 1;
		this.particleRot = this.rand.nextInt(64);
		
		this.particleMaxAge = MathHelper.floor(MathUtil.random(0.6, 1.0) * ForgeConfigHandler.FALLINGLEAVES.lifetime * 20.0);
		this.motionY = -ForgeConfigHandler.FALLINGLEAVES.speed;
		this.particleScale = (float)ForgeConfigHandler.FALLINGLEAVES.size * 0.1F;
		
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
		if(this.rand.nextFloat() > 0.95F) this.rotPositive = !this.rotPositive;
		if(this.particleAge > this.particleMaxAge - 20 && ForgeConfigHandler.FALLINGLEAVES.fadeOut) this.particleAlpha = 0.05F * (this.particleMaxAge - this.particleAge);
		
		if(this.onGround || this.wasCollided) {
			this.velocity.setTo(0.0, 0.0, 0.0);
			if(!this.wasCollided) {
				this.particleAge = ForgeConfigHandler.FALLINGLEAVES.fadeOut ? Math.max(this.particleAge, this.particleMaxAge - 20) : this.particleMaxAge;
				this.wasCollided = true;
			}
		}
		else {
			this.velocity.setTo(COS[particleRot], 0.0, SIN[particleRot])
						 .mul(ForgeConfigHandler.FALLINGLEAVES.perturb)
						 .add(LeafWindTracker.LEAF_WIND_TRACKER.current)
						 .add(0.0, -1.0, 0.0)
						 .mul(ForgeConfigHandler.FALLINGLEAVES.speed);
			this.particleRot = (this.particleRot + (rotPositive ? 1 : -1)) & 63;
		}
	}
	
	@Override
	public void render(BufferBuilder worldRenderer, float partialTickTime) {
		if(ForgeConfigHandler.FALLINGLEAVES.opacityHack) GL11.glDepthMask(true);
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