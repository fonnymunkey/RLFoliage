package betterfoliage.render.generator;

import betterfoliage.render.math.Pair;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ShortGrassGenerator extends TextureGenerator {
	public static final ShortGrassGenerator SHORT_GRASS_GENERATOR = new ShortGrassGenerator();
	
	private ShortGrassGenerator() {
		super("bf_gen_short_grass");
	}
	
	@Nullable
	@Override
	public BufferedImage generate(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return null;
		boolean isSnowed = params.contains("snowed") && Boolean.parseBoolean(params.get("snowed"));
		
		IResource res = RenderUtil.getResource(target.r);
		if(res == null) return null;
		BufferedImage baseTexture = RenderUtil.loadImage(res);
		if(baseTexture == null) return null;
		
		BufferedImage result = new BufferedImage(baseTexture.getWidth(), baseTexture.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = result.createGraphics();
		
		int size = baseTexture.getWidth();
		int frames = baseTexture.getHeight() / size;
		
		for(int i = 0; i < frames; i++) {
			BufferedImage baseFrame = baseTexture.getSubimage(0, size * i, size, size);
			BufferedImage grassFrame = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
			
			grassFrame.createGraphics().drawImage(baseFrame, 0, 3 * size / 8, null);
			graphics.drawImage(grassFrame, 0, size * i, null);
		}
		if(isSnowed && target.l == ResourceType.COLOR) {
			for(int x = 0; x < result.getWidth(); x++) {
				for(int y = 0; y < result.getHeight(); y++) {
					result.setRGB(x, y, RenderUtil.blendRGB(result.getRGB(x, y), 16777215, 2, 3));
				}
			}
		}
		return result;
	}
}