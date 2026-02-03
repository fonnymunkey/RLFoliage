package betterfoliage;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1400)
public class BetterFoliagePlugin implements IFMLLoadingPlugin {
	
	public BetterFoliagePlugin() {
		MixinBootstrap.init();
		MixinExtrasBootstrap.init();
		FermiumRegistryAPI.enqueueMixin(false, "mixins.betterfoliage.vanilla.json");
		FermiumRegistryAPI.enqueueMixin(false, "mixins.betterfoliage.optifine.json", () -> FermiumRegistryAPI.isModPresent("optifine"));
		FermiumRegistryAPI.enqueueMixin(false, "mixins.betterfoliage.nonoptifine.json", () -> !FermiumRegistryAPI.isModPresent("optifine"));
		FermiumRegistryAPI.enqueueMixin(false, "mixins.betterfoliage.vintagefix.json", () -> FermiumRegistryAPI.isModPresent("vintagefix"));
		FermiumRegistryAPI.enqueueMixin(true, "mixins.betterfoliage.forestry.json", () -> FermiumRegistryAPI.isModPresent("forestry"));
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}