package betterfoliage.render.feature.particle;

import betterfoliage.BetterFoliage;
import betterfoliage.render.feature.RenderingHandler;
import betterfoliage.render.feature.ResourceHandler;
import org.apache.logging.log4j.Level;

public class RisingSoulResources extends ResourceHandler {
	public static final RisingSoulResources RISING_SOUL_RESOURCES = new RisingSoulResources();
	
	public final IconSet headIcons = getIconSet(BetterFoliage.MODID, "blocks/rising_soul_%d");
	public final IconHolder trackIcon = getIconStatic(BetterFoliage.MODID, "blocks/soul_track");
	
	private RisingSoulResources() {
		super();
	}
	
	@Override
	public void afterPreStitch() {
		RenderingHandler.log(Level.INFO, String.format("Registered %s soul particle textures", this.headIcons.size));
	}
}