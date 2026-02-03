package betterfoliage.render.generator;

import betterfoliage.BetterFoliage;
import betterfoliage.render.math.Pair;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class LeafGenerator extends TextureGenerator {
	public static final LeafGenerator LEAF_GENERATOR = new LeafGenerator();
	
	private LeafGenerator() {
		super("bf_gen_leaves");
	}
	
	@Nullable
	@Override
	public BufferedImage generate(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return null;
		String leafType = params.get("type");
		if(leafType == null) leafType = "default";
		
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
		
		IResource maskRes = getLeafMask(leafType, size * 2);
		if(maskRes == null) maskRes = getLeafMask("default", size * 2);
		BufferedImage maskTexture = null;
		double maskScale = 0;
		if(maskRes != null) {
			maskTexture = RenderUtil.loadImage(maskRes);
			if(maskTexture != null) {
				maskScale = (double)maskTexture.getWidth() / (double)(size * 2);
			}
		}
		
		BufferedImage leafTexture = new BufferedImage(size * 2, size * 2 * frames, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = leafTexture.createGraphics();
		
		for(int i = 0; i < frames; i++) {
			BufferedImage baseFrame = baseTexture.getSubimage(0, size * i, size, size);
			BufferedImage leafFrame = new BufferedImage(size * 2, size * 2, BufferedImage.TYPE_4BYTE_ABGR);
			
			Graphics2D leafGraphics = leafFrame.createGraphics();
			leafGraphics.drawImage(baseFrame, 0, 0, null);
			leafGraphics.drawImage(baseFrame, 0, size, null);
			leafGraphics.drawImage(baseFrame, size, 0, null);
			leafGraphics.drawImage(baseFrame, size, size, null);
			
			if(maskTexture != null && target.l == ResourceType.COLOR) {
				for(int x = 0; x < size * 2; x++) {
					for(int y = 0; y < size * 2; y++) {
						long basePixel = (long)leafFrame.getRGB(x, y) & 0xFFFFFFFFL;
						long maskPixel = (long)maskTexture.getRGB((int)(x * maskScale), (int)(y * maskScale)) & 0xFF000000L | 0xFFFFFFL;
						leafFrame.setRGB(x, y, (int)(basePixel & maskPixel));
					}
				}
			}
			
			graphics.drawImage(leafFrame, 0, size * i * 2, null);
		}
		
		return leafTexture;
	}
	
	private static IResource getLeafMask(String type, int maxSize) {
		return getMultisizeTexture(maxSize, s -> new ResourceLocation(BetterFoliage.MODID, String.format("textures/blocks/leafmask_%s_%s.png", s, type)));
	}
}