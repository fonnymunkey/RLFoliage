package betterfoliage.mixin;

import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayDeque;

@Mixin(BlockPos.PooledMutableBlockPos.class)
public abstract class PooledMutableBlockPosMixin {
	
	@Unique
	private static final ThreadLocal<ArrayDeque<BlockPos.PooledMutableBlockPos>> betterfoliage$POOL = ThreadLocal.withInitial(ArrayDeque::new);
	
	@Invoker(value = "<init>")
	static BlockPos.PooledMutableBlockPos betterfoliage$init(int xIn, int yIn, int zIn) {
		throw new AssertionError();
	}
	
	@Shadow
	private boolean released;
	
	/**
	 * @author fonnymunkey
	 * @reason Make PooledMutableBlockPos use ThreadLocal rather than synchronized to remove contention
	 */
	@Overwrite
	public static BlockPos.PooledMutableBlockPos retain(int xIn, int yIn, int zIn) {
		ArrayDeque<BlockPos.PooledMutableBlockPos> pool = betterfoliage$POOL.get();
		if(!pool.isEmpty()) {
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = pool.removeLast();
			if(((PooledMutableBlockPosMixin)(Object)blockpos$pooledmutableblockpos).released) {
				((PooledMutableBlockPosMixin)(Object)blockpos$pooledmutableblockpos).released = false;
				blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
				return blockpos$pooledmutableblockpos;
			}
		}
		return betterfoliage$init(xIn, yIn, zIn);
	}
	
	/**
	 * @author fonnymunkey
	 * @reason Make PooledMutableBlockPos use ThreadLocal rather than synchronized to remove contention
	 */
	@Overwrite
	public void release() {
		ArrayDeque<BlockPos.PooledMutableBlockPos> pool = betterfoliage$POOL.get();
		if(pool.size() < 100) {
			pool.addLast((BlockPos.PooledMutableBlockPos)(Object)this);
		}
		this.released = true;
	}
}