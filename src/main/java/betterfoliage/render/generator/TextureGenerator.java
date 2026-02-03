package betterfoliage.render.generator;

import betterfoliage.render.math.Pair;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public abstract class TextureGenerator extends ParameterBasedGenerator {
	public enum ResourceType {
		COLOR,
		METADATA,
		NORMAL,
		SPECULAR
	}
	
	protected TextureGenerator(String domain) {
		super(domain);
	}
	
	public ResourceLocation generatedResource(String iconName, @Nullable Pair<String, Object> extraParam) {
		ResourceLocation textureLocation = RenderUtil.textureLocation(iconName);
		Map<String,String> params = new LinkedHashMap<>();
		params.put("dom", textureLocation.getNamespace());
		params.put("path", textureLocation.getPath());
		if(extraParam != null) {
			params.put(extraParam.l, extraParam.r.toString());
		}
		return new ResourceLocation(this.domain, new ParameterList(params, "generate").toString());
	}
	
	@Override
	public boolean resourceExists(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return false;
		return RenderUtil.getResource(target.r) != null;
	}
	
	@Nullable
	public InputStream getInputStream(ParameterList params) {
		Pair<ResourceType,ResourceLocation> target = targetResource(params);
		if(target == null) return null;
		if(target.l == ResourceType.METADATA) {
			IResource res = RenderUtil.getResource(target.r);
			return res == null ? null : res.getInputStream();
		}
		BufferedImage img = this.generate(params);
		if(img == null) return null;
		try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
			ImageIO.write(img, "PNG", stream);
			return new ByteArrayInputStream(stream.toByteArray());
		}
		catch(Exception ignored) { }
		return null;
	}
	
	@Nullable
	public abstract BufferedImage generate(ParameterList params);
	
	@Nullable
	public static IResource getMultisizeTexture(int maxSize, Function<Integer,ResourceLocation> maskPath) {
		List<Integer> sizes = new ArrayList<>();
		while(maxSize > 2) {
			sizes.add(maxSize);
			maxSize /= 2;
		}
		for(int size : sizes) {
			IResource res = RenderUtil.getResource(maskPath.apply(size));
			if(res != null) return res;
		}
		return null;
	}
	
	@Nullable
	public static Pair<ResourceType,ResourceLocation> targetResource(ParameterList params) {
		if(!params.contains("dom") || !params.contains("path")) return null;
		ResourceLocation baseTexture = new ResourceLocation(params.get("dom"), params.get("path"));
		String value = params.getValue();
		if(value == null) return null;
		else value = value.toLowerCase();
		switch(value) {
			case "generate.png": return new Pair<>(ResourceType.COLOR, new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath() + ".png"));
			case "generate.png.mcmeta": return new Pair<>(ResourceType.METADATA, new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath() + ".png.mcmeta"));
			case "generate_n.png": return new Pair<>(ResourceType.NORMAL, new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath() + "_n.png"));
			case "generate_s.png": return new Pair<>(ResourceType.SPECULAR, new ResourceLocation(baseTexture.getNamespace(), baseTexture.getPath() + "_s.png"));
			default: return null;
		}
	}
}