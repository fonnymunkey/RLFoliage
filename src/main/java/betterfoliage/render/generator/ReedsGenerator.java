package betterfoliage.render.generator;

import betterfoliage.render.math.Pair;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ReedsGenerator extends TextureGenerator {
	public static final ReedsGenerator REEDS_GENERATOR = new ReedsGenerator("bf_gen_reeds", 1, 2);
	
	private final int aspectWidth;
	private final int aspectHeight;
	
	private ReedsGenerator(String domain, int aspectWidth, int aspectHeight) {
		super(domain);
		this.aspectWidth = aspectWidth;
		this.aspectHeight = aspectHeight;
	}
	
	@Nullable
	@Override
	public BufferedImage generate(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return null;
		IResource res = RenderUtil.getResource(target.r);
		if(res == null) return null;
		BufferedImage baseTexture = RenderUtil.loadImage(res);
		if(baseTexture == null) return null;
		
		int frameWidth = baseTexture.getWidth();
		int frameHeight = baseTexture.getWidth() * this.aspectHeight / this.aspectWidth;
		int frames = baseTexture.getHeight() / frameHeight;
		int size = Math.max(frameWidth, frameHeight);
		
		BufferedImage resultTexture = new BufferedImage(size, size * frames, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = resultTexture.createGraphics();
		
		for(int i = 0; i < frames; i++) {
			BufferedImage baseFrame = baseTexture.getSubimage(0, size * i, frameWidth, frameHeight);
			BufferedImage resultFrame = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
			
			resultFrame.createGraphics().drawImage(baseFrame, (size - frameWidth) / 2, (size - frameHeight) / 2, null);
			graphics.drawImage(resultFrame, 0, size * i, null);
		}
		return resultTexture;
	}
}