package betterfoliage.render.util;

import betterfoliage.render.model.HSB;
import betterfoliage.render.math.Pair;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class RenderUtil {
	public static final Function<EnumFacing,Boolean> ALL_FACES = n -> true;
	public static final Function<EnumFacing,Boolean> TOP_ONLY = n -> n == EnumFacing.UP;
	public static final Function<EnumFacing,Boolean> BOTTOM_ONLY = n -> n == EnumFacing.DOWN;
	private static final Integer[] BRIGHTNESS_COMPONENTS = new Integer[]{ 20, 4 };
	
	public static boolean isSnow(Material mat) {
		return mat == Material.SNOW || mat == Material.CRAFTED_SNOW;
	}
	
	public static List<Pair<ModelBlock,ResourceLocation>> modelBlockAndLoc(IModel model) {
		if(model instanceof ModelLoader.VanillaModelWrapper) {
			return Collections.singletonList(new Pair<>(((ModelLoader.VanillaModelWrapper)model).model, ((ModelLoader.VanillaModelWrapper)model).location));
		}
		else if(model instanceof ModelLoader.WeightedRandomModel) {
			List<IModel> models = ((ModelLoader.WeightedRandomModel)model).models;
			if(models != null) {
				return models.stream().flatMap(m -> modelBlockAndLoc(m).stream()).collect(Collectors.toList());
			}
		}
		else if(model instanceof ModelLoader.MultipartModel) {
			Map<Selector,IModel> models = ((ModelLoader.MultipartModel)model).partModels;
			if(models != null) {
				return models.values().stream().flatMap(m -> modelBlockAndLoc(m).stream()).collect(Collectors.toList());
			}
		}
		else {
			IModel val = getIModelFromField(model);
			if(val != null) return modelBlockAndLoc(val);
		}
		return Collections.emptyList();
	}
	
	@Nullable
	private static IModel getIModelFromField(IModel model) {
		if(model == null) return null;
		for(Field field : model.getClass().getDeclaredFields()) {
			if(IModel.class.isAssignableFrom(field.getType())) {
				try {
					field.setAccessible(true);
					return (IModel)field.get(model);
				}
				catch(Exception ignored) {}
			}
		}
		return null;
	}
	
	public static String stripStart(String main, String strip) {
		return main.startsWith(strip) ? main.substring(strip.length()) : main;
	}
	
	public static ResourceLocation stripStart(ResourceLocation main, String strip) {
		return new ResourceLocation(main.getNamespace(), stripStart(main.getPath(), strip));
	}
	
	@Nullable
	public static IResource getResource(ResourceLocation loc) {
		try {
			return Minecraft.getMinecraft().getResourceManager().getResource(loc);
		}
		catch(Exception ignored) {}
		return null;
	}
	
	@Nullable
	public static List<String> getLines(IResource res) {
		List<String> result = new ArrayList<>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
			reader.lines().forEach(result::add);
		}
		catch(Exception ex) {
			return null;
		}
		return result;
	}
	
	@Nullable
	public static BufferedImage loadImage(IResource res) {
		try(InputStream stream = res.getInputStream()) {
			return ImageIO.read(stream);
		}
		catch(Exception ignored) {}
		return null;
	}
	
	public static Integer averageColor(TextureAtlasSprite texture) {
		ResourceLocation locationNoDirs = stripStart(new ResourceLocation(texture.getIconName()), "blocks/");
		ResourceLocation locationWithDirs = new ResourceLocation(locationNoDirs.getNamespace(), String.format("textures/blocks/%s.png", locationNoDirs.getPath()));
		IResource res = getResource(locationWithDirs);
		if(res != null) {
			BufferedImage image = loadImage(res);
			if(image != null) {
				int numOpaque = 0;
				double sumHueX = 0.0D;
				double sumHueY = 0.0D;
				float sumSaturation = 0.0F;
				float sumBrightness = 0.0F;
				for(int x = 0; x < image.getWidth(); x++) {
					for(int y = 0; y < image.getHeight(); y++) {
						int pixel = image.getRGB(x, y);
						int alpha = (pixel >> 24) & 255;
						HSB hsb = HSB.fromColor(pixel);
						if(alpha == 255) {
							numOpaque++;
							sumHueX += Math.cos(((double)hsb.hue - 0.5D) * Math.PI * 2.0D);
							sumHueY += Math.sin(((double)hsb.hue - 0.5D) * Math.PI * 2.0D);
							sumSaturation += hsb.saturation;
							sumBrightness += hsb.brightness;
						}
					}
				}
				float avgHue = (float)(Math.atan2(sumHueY, sumHueX) / (Math.PI * 2.0D) + 0.5D);
				return Color.HSBtoRGB(avgHue, sumSaturation / (float)numOpaque, sumBrightness / (float)numOpaque);
			}
		}
		return null;
	}
	
	public static boolean derivesFrom(ModelBlock model, ResourceLocation loc, ResourceLocation target) {
		if(RenderUtil.stripStart(loc, "models/").equals(target)) return true;
		if(model.parent != null && model.getParentLocation() != null) {
			return derivesFrom(model.parent, model.getParentLocation(), target);
		}
		return false;
	}
	
	public static List<Pair<ModelBlock,ResourceLocation>> distinctByLoc(List<Pair<ModelBlock,ResourceLocation>> list) {
		Map<ResourceLocation,Pair<ModelBlock,ResourceLocation>> distinct = new LinkedHashMap<>();
		for(Pair<ModelBlock,ResourceLocation> pair : list) {
			distinct.put(pair.r, pair);
		}
		return new ArrayList<>(distinct.values());
	}
	
	public static ResourceLocation textureLocation(String iconName) {
		ResourceLocation loc = new ResourceLocation(iconName);
		if(loc.getPath().startsWith("mcpatcher")) return loc;
		return new ResourceLocation(loc.getNamespace(), "textures/" + loc.getPath());
	}
	
	public static int brMul(int i, float f) {
		int weight = (int)(f * 256.0F);
		int result = 0;
		for(int shift : BRIGHTNESS_COMPONENTS) {
			int raw = (i >> shift) & 15;
			int weighted = raw * weight / 256;
			result |= weighted << shift;
		}
		return result;
	}
	
	public static int colorMult(int i, float f) {
		int weight = (int)(f * 256.0F);
		int red = (i >> 16 & 255) * weight / 256;
		int green = (i >> 8 & 255) * weight / 256;
		int blue = (i & 255) * weight / 256;
		return (red << 16) | (green << 8) | blue;
	}
	
	public static int brSum(Float multiplier, int... brightness) {
		int[] sum = new int[BRIGHTNESS_COMPONENTS.length];
		for(int i = 0; i < BRIGHTNESS_COMPONENTS.length; i++) {
			int shift = BRIGHTNESS_COMPONENTS[i];
			for(int br : brightness) {
				int comp = (br >> shift) & 15;
				sum[i] += comp;
			}
		}
		int result = 0;
		for(int i = 0; i < BRIGHTNESS_COMPONENTS.length; i++) {
			int shift = BRIGHTNESS_COMPONENTS[i];
			int comp = multiplier == null ? sum[i] << shift : (int)((float)sum[i] * multiplier) << shift;
			result |= comp;
		}
		return result;
	}
	
	public static int brWeighted(int br1, float weight1, int br2, float weight2) {
		int w1 = (int)(weight1 * 256.0F + 0.5F);
		int w2 = (int)(weight2 * 256.0F + 0.5F);
		int result = 0;
		for(int shift : BRIGHTNESS_COMPONENTS) {
			int comp1 = (br1 >> shift)&15;
			int comp2 = (br2 >> shift)&15;
			int compWeighted = (comp1 * w1 + comp2 * w2) / 256;
			result |= (compWeighted&15) << shift;
		}
		return result;
	}
	
	public static int blendRGB(int rgb1, int rgb2, int weight1, int weight2) {
		int r = (((rgb1 >> 16) & 255) * weight1 + ((rgb2 >> 16) & 255) * weight2) / (weight1 + weight2);
		int g = (((rgb1 >> 8) & 255) * weight1 + ((rgb2 >> 8) & 255) * weight2) / (weight1 + weight2);
		int b = ((rgb1 & 255) * weight1 + (rgb2 & 255) * weight2) / (weight1 + weight2);
		int a = (rgb1 >> 24) & 255;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}