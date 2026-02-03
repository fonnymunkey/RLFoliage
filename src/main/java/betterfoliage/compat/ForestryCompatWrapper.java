package betterfoliage.compat;

import betterfoliage.config.ForgeConfigHandler;
import net.minecraftforge.fml.common.Loader;

public abstract class ForestryCompatWrapper {
	
	public static void init() {
		if(Loader.isModLoaded("forestry") && ForgeConfigHandler.GLOBAL.enableForestryCompat) {
			ForestryHandler.init();
		}
	}
}