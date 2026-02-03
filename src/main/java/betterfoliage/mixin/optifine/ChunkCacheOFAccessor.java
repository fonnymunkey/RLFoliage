package betterfoliage.mixin.optifine;

import net.minecraft.world.ChunkCache;
import net.optifine.override.ChunkCacheOF;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkCacheOF.class)
public interface ChunkCacheOFAccessor {
	@Accessor(value = "chunkCache", remap = false)
	ChunkCache getChunkCache();
}