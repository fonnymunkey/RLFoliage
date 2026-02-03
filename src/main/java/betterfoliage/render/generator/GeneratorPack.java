package betterfoliage.render.generator;

import betterfoliage.mixin.FMLClientHandlerAccessor;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneratorPack implements IResourcePack {
	public static final GeneratorPack GENERATOR_PACK = new GeneratorPack("RLFoliage Generated",
																		 ShortGrassGenerator.SHORT_GRASS_GENERATOR,
																		 ConnectedGrassGenerator.CONNECTED_GRASS_GENERATOR,
																		 LeafGenerator.LEAF_GENERATOR,
																		 ReedsGenerator.REEDS_GENERATOR);
	
	private final String name;
	private final List<GeneratorBase> generators;
	
	public GeneratorPack(String name, List<GeneratorBase> generators) {
		this.name = name;
		this.generators = generators;
	}
	
	public GeneratorPack(String name, GeneratorBase... generators) {
		this(name, Arrays.asList(generators));
	}
	
	public void inject() {
		((FMLClientHandlerAccessor)FMLClientHandler.instance()).betterfoliage$getResourcePackList().add(this);
	}
	
	@Override
	public String getPackName() {
		return this.name;
	}
	
	@Override
	public BufferedImage getPackImage() {
		return null;
	}
	
	@Override
	public Set<String> getResourceDomains() {
		return this.generators.stream().map(g -> g.domain).collect(Collectors.toSet());
	}
	
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T extends IMetadataSection> T getPackMetadata(@Nullable MetadataSerializer serializer, @Nullable String sectionName) throws IOException {
		return "pack".equals(sectionName) ? (T) new PackMetadataSection(new TextComponentString("Generated resources"), 1) : null;
	}
	
	@Override
	public boolean resourceExists(@Nullable ResourceLocation location) {
		if(location == null) return false;
		for(GeneratorBase generator : this.generators) {
			if(generator.domain.equals(location.getNamespace()) && generator.resourceExists(location)) return true;
		}
		return false;
	}
	
	@Override
	public InputStream getInputStream(@Nullable ResourceLocation location) {
		if(location == null) return null;
		for(GeneratorBase generator : this.generators) {
			if(generator.domain.equals(location.getNamespace()) && generator.resourceExists(location)) {
				InputStream stream = generator.getInputStream(location);
				if(stream != null) return stream;
			}
		}
		return null;
	}
}