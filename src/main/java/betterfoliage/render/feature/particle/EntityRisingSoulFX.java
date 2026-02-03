package betterfoliage.render.feature.particle;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.math.Double3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Deque;
import java.util.LinkedList;

public class EntityRisingSoulFX extends AbstractEntityFX {
	private final Deque<Double3> particleTrail = new LinkedList<>();
	private final int initialPhase;
	
	public EntityRisingSoulFX(World worldIn, BlockPos pos) {
		super(worldIn, (double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5);
		this.motionY = ForgeConfigHandler.RISINGSOUL.speed;
		this.particleGravity = 0.0F;
		this.particleTexture = RisingSoulResources.RISING_SOUL_RESOURCES.headIcons.get(this.rand.nextInt(256));
		this.particleMaxAge = MathHelper.floor((0.6 + 0.4 * this.rand.nextDouble()) * ForgeConfigHandler.RISINGSOUL.lifetime * 20.0);
		this.initialPhase = this.rand.nextInt(64);
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public void update() {
		int phase = (this.initialPhase + this.particleAge) % 64;
		this.velocity.setTo(COS[phase] * ForgeConfigHandler.RISINGSOUL.perturb, ForgeConfigHandler.RISINGSOUL.speed, SIN[phase] * ForgeConfigHandler.RISINGSOUL.perturb);
		
		this.particleTrail.addFirst(new Double3(this.currentPos.x, this.currentPos.y, this.currentPos.z));
		while(this.particleTrail.size() > ForgeConfigHandler.RISINGSOUL.trailLength) {
			this.particleTrail.removeLast();
		}
	}
	
	@Override
	public void render(BufferBuilder worldRenderer, float partialTickTime) {
		float alpha = (float)ForgeConfigHandler.RISINGSOUL.opacity;
		if(this.particleAge > this.particleMaxAge - 40) alpha *= (float)(this.particleMaxAge - this.particleAge) / 40.0F;
		
		this.renderParticleQuad(worldRenderer, partialTickTime, ForgeConfigHandler.RISINGSOUL.headSize * 0.25D, alpha);
		
		double scale = ForgeConfigHandler.RISINGSOUL.trailSize * 0.25D;
		Double3 previous = null;
		int i = 0;
		for(Double3 current : this.particleTrail) {
			if(previous != null) {
				scale *= ForgeConfigHandler.RISINGSOUL.sizeDecay;
				alpha *= (float)ForgeConfigHandler.RISINGSOUL.opacityDecay;
				if(i % ForgeConfigHandler.RISINGSOUL.trailDensity == 0) {
					renderParticleQuad(worldRenderer,
									   partialTickTime,
									   current,
									   previous,
									   scale,
									   RisingSoulResources.RISING_SOUL_RESOURCES.trackIcon.icon,
									   alpha);
				}
			}
			previous = current;
			i++;
		}
	}
}