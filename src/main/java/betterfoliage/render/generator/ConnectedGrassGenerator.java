package betterfoliage.render.generator;

import betterfoliage.BetterFoliage;
import betterfoliage.render.math.Pair;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ConnectedGrassGenerator extends TextureGenerator {
	public static final ConnectedGrassGenerator CONNECTED_GRASS_GENERATOR = new ConnectedGrassGenerator();
	
	private ConnectedGrassGenerator() {
		super("bf_gen_connected_grass");
	}
	
	@Nullable
	@Override
	public BufferedImage generate(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return null;
		boolean longGrass = params.contains("long") && Boolean.parseBoolean(params.get("long"));
		
		ResourceLocation handDrawnLoc = RenderUtil.stripStart(target.r, "blocks/");
		handDrawnLoc = new ResourceLocation(BetterFoliage.MODID, handDrawnLoc.getNamespace() + "/textures/blocks/" + handDrawnLoc.getPath());
		IResource handRes = RenderUtil.getResource(handDrawnLoc);
		if(handRes != null) {
			BufferedImage handTex = RenderUtil.loadImage(handRes);
			if(handTex != null) return handTex;
		}
		
		IResource res = RenderUtil.getResource(target.r);
		if(res == null) return null;
		BufferedImage baseTexture = RenderUtil.loadImage(res);
		if(baseTexture == null) return null;
		
		int size = baseTexture.getWidth();
		int frames = baseTexture.getHeight() / size;
		
		IResource maskRes = getGrassMask(longGrass ? "long" : "short", size);
		if(maskRes == null) maskRes = getGrassMask("short", size);
		BufferedImage maskTexture = null;
		double maskScale = 0;
		if(maskRes != null) {
			maskTexture = RenderUtil.loadImage(maskRes);
			if(maskTexture != null) {
				maskScale = (double)maskTexture.getWidth() / (double)(size);
			}
		}
		
		BufferedImage result = new BufferedImage(size, size * frames, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = result.createGraphics();
		
		for(int i = 0; i < frames; i++) {
			BufferedImage baseFrame = baseTexture.getSubimage(0, size * i, size, size);
			BufferedImage grassFrame = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
			
			Graphics2D grassGraphics = grassFrame.createGraphics();
			grassGraphics.drawImage(baseFrame, 0, 0, null);
			
			if(maskTexture != null && target.l == ResourceType.COLOR) {
				for(int x = 0; x < size; x++) {
					for(int y = 0; y < size; y++) {
						long basePixel = (long)grassFrame.getRGB(x, y) & 0xFFFFFFFFL;
						long maskPixel = (long)maskTexture.getRGB((int)(x * maskScale), (int)(y * maskScale)) & 0xFF000000L | 0xFFFFFFL;
						grassFrame.setRGB(x, y, (int)(basePixel & maskPixel));
					}
				}
			}
			
			graphics.drawImage(grassFrame, 0, size * i, null);
		}
		return result;
	}
	
	private static IResource getGrassMask(String type, int maxSize) {
		return getMultisizeTexture(maxSize, s -> new ResourceLocation(BetterFoliage.MODID, String.format("textures/blocks/grassmask_%s_%s.png", s, type)));
	}
}