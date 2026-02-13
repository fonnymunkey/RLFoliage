package betterfoliage.compat;

import betterfoliage.render.feature.RenderingHandler;
import meldexun.nothirium.mc.renderer.chunk.SectionRenderCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.logging.log4j.Level;

public abstract class NothiriumHandler {
	
	public static void init() {
		RenderingHandler.log(Level.INFO, "RLFoliage Nothirium support is enabled");
	}
	
	public static boolean isCacheNothirium(IBlockAccess access) {
		return access instanceof SectionRenderCache;
	}
	
	public static boolean isBlockLoadedSafe(IBlockAccess access, BlockPos pos) {
		return ((SectionRenderCache)access).getWorld().isBlockLoaded(pos, false);
	}
}