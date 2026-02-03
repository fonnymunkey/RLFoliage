package betterfoliage.render.feature.particle;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.math.Double3;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class LeafWindTracker {
	public static final LeafWindTracker LEAF_WIND_TRACKER = new LeafWindTracker();
	
	public Random random = new Random();
	public Double3 target = new Double3(0, 0, 0);
	public Double3 current = new Double3(0, 0, 0);
	public long nextChange = 0;
	
	private LeafWindTracker() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void changeWind(World world) {
		this.nextChange = world.getWorldInfo().getWorldTime() + 120 + this.random.nextInt(80);
		double direction = Math.PI * 2.0 * this.random.nextDouble();
		double speed = Math.abs(this.random.nextGaussian()) * ForgeConfigHandler.FALLINGLEAVES.windStrength;
		if(world.isRaining()) speed += Math.abs(this.random.nextGaussian()) * ForgeConfigHandler.FALLINGLEAVES.stormStrength;
		this.target.setTo(Math.cos(direction) * speed, 0.0, Math.sin(direction) * speed);
	}
	
	@SubscribeEvent
	public void handleWorldTick(TickEvent.ClientTickEvent event) {
		if(event.phase != TickEvent.Phase.START) return;
		World world = Minecraft.getMinecraft().world;
		if(world == null) return;
		
		if(world.getWorldInfo().getWorldTime() >= this.nextChange) this.changeWind(world);
		double changeRate = world.isRaining() ? 0.015 : 0.005;
		this.current.add(Math.min(Math.max(this.target.x - this.current.x, -changeRate), changeRate),
						 0.0,
						 Math.min(Math.max(this.target.z - this.current.z, -changeRate), changeRate));
	}
	
	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		if(event.getWorld().isRemote) this.changeWind(event.getWorld());
	}
}