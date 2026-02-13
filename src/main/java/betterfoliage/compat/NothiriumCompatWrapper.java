package betterfoliage.compat;

import betterfoliage.render.feature.RenderingHandler;
import fermiumbooter.FermiumRegistryAPI;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.apache.logging.log4j.Level;

public abstract class NothiriumCompatWrapper {
	private static final String Nothirium_MODID = "nothirium";
	private static Boolean nothiriumLoaded = null;
	
	public static void init() {
		if(isNothiriumLoaded()) NothiriumHandler.init();
		else RenderingHandler.log(Level.INFO, "Nothirium support is disabled");
	}
	
	public static boolean isNothiriumLoaded() {
		if(nothiriumLoaded == null) nothiriumLoaded = FermiumRegistryAPI.isModPresent(Nothirium_MODID);
		return nothiriumLoaded;
	}
	
	public static boolean isCacheNothirium(IBlockAccess access) {
		if(isNothiriumLoaded()) return NothiriumHandler.isCacheNothirium(access);
		return false;
	}
	
	public static boolean isBlockLoadedSafe(IBlockAccess access, BlockPos pos) {
		return NothiriumHandler.isBlockLoadedSafe(access, pos);
	}
}