package betterfoliage.config;

import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ModelTextureList {
	public final ResourceLocation modelLocation;
	public final List<String> textureNames;
	
	public ModelTextureList(ResourceLocation modelLocation, List<String> textureNames) {
		this.modelLocation = modelLocation;
		this.textureNames = textureNames;
	}
}