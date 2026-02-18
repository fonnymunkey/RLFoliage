package betterfoliage.mixin;

import betterfoliage.config.ForgeConfigHandler;
import betterfoliage.render.feature.particle.EntityFallingLeavesFX;
import betterfoliage.render.feature.particle.EntityFallingLeavesModernFX;
import betterfoliage.render.feature.particle.EntityRisingSoulFX;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World {
	
	protected WorldClientMixin(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	@Inject(
			method = "showBarrierParticles",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;randomDisplayTick(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/Random;)V", shift = At.Shift.AFTER)
	)
	private void betterfoliage_vanillaWorldClient_showBarrierParticles_randomDisplayTick(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos, CallbackInfo ci, @Local IBlockState state) {
		if(!ForgeConfigHandler.GLOBAL.enabled) return;
		if(ForgeConfigHandler.RISINGSOUL.enabled &&
				state.getBlock() == Blocks.SOUL_SAND &&
				Math.random() < ForgeConfigHandler.RISINGSOUL.chance) {
			pos.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
			boolean render = this.isAirBlock(pos);
			pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
			if(render) new EntityRisingSoulFX(this, pos).addIfValid();
		}
		if(ForgeConfigHandler.FALLINGLEAVES.enabled &&
				Math.random() < ForgeConfigHandler.FALLINGLEAVES.chance &&
				ForgeConfigHandler.BLOCKS.leavesClassesMatcher.matchesClass(state.getBlock())) {
			pos.setPos(pos.getX(), pos.getY() - 1, pos.getZ());
			boolean render = this.isAirBlock(pos);
			pos.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
			if(render) {
				if(ForgeConfigHandler.FALLINGLEAVES.modernized) new EntityFallingLeavesModernFX(this, pos, state).addIfValid();
				else new EntityFallingLeavesFX(this, pos, state).addIfValid();
			}
		}
	}
}