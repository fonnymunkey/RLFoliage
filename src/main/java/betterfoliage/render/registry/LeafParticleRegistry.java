package betterfoliage.render.registry;

import betterfoliage.BetterFoliage;
import betterfoliage.render.feature.ResourceHandler;
import betterfoliage.render.util.LoadModelDataEvent;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class LeafParticleRegistry {
	public static final LeafParticleRegistry LEAF_PARTICLE_REGISTRY = new LeafParticleRegistry();
	
	private static final ResourceLocation MAPPING_LOC = new ResourceLocation(BetterFoliage.MODID, "leaf_texture_mappings.cfg");
	
	public final TextureMatcher typeMappings = new TextureMatcher();
	private final HashMap<String,ResourceHandler.IconSet> particles = new HashMap<>();
	
	private LeafParticleRegistry() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public ResourceHandler.IconSet get(String type) {
		ResourceHandler.IconSet iconSet = this.particles.get(type);
		if(iconSet == null) iconSet = this.particles.get("default");
		return iconSet;
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void handleLoadModelData(LoadModelDataEvent event) {
		this.particles.clear();
		this.typeMappings.loadMappings(MAPPING_LOC);
	}
	
	@SubscribeEvent
	public void handlePreStitch(TextureStitchEvent.Pre event) {
		Set<String> allTypes = this.typeMappings.mappings.stream().map(m -> m.type).collect(Collectors.toCollection(LinkedHashSet::new));
		allTypes.add("default");
		for(String leafType : allTypes) {
			ResourceHandler.IconSet particleSet = new ResourceHandler.IconSet(BetterFoliage.MODID, "blocks/falling_leaf_" + leafType + "_%d");
			particleSet.onPreStitch(event.getMap());
			if(leafType.equals("default") || particleSet.size > 0) this.particles.put(leafType, particleSet);
		}
	}
	
	@SubscribeEvent
	public void handlePostStitch(TextureStitchEvent.Post event) {
		this.particles.values().forEach(particleSet -> particleSet.onPostStitch(event.getMap()));
	}
	
	public static class TextureMatcher {
		private final List<Mapping> mappings = new ArrayList<>();
		
		private TextureMatcher() { }
		
		@Nullable
		public String getType(ResourceLocation resource) {
			return this.mappings.stream()
						 .filter(m -> m.matches(resource))
						 .map(m -> m.type)
						 .findFirst().orElse(null);
		}
		
		@Nullable
		public String getType(String iconName) {
			return this.getType(new ResourceLocation(iconName));
		}
		
		public void loadMappings(ResourceLocation mappingLocation) {
			this.mappings.clear();
			IResource res = RenderUtil.getResource(mappingLocation);
			if(res != null) {
				List<String> lines = RenderUtil.getLines(res);
				if(lines != null) {
					lines.stream().filter(s -> !s.isEmpty() && !s.startsWith("//"))
						 .forEach(line -> {
							 String[] line2 = line.trim().split("=");
							 if(line2.length == 2) {
								 String[] mapping = line2[0].trim().split(":");
								 if(mapping.length == 1) mappings.add(new Mapping(null, mapping[0].trim(), line2[1].trim()));
								 else if(mapping.length == 2) mappings.add(new Mapping(mapping[0].trim(), mapping[1].trim(), line2[1].trim()));
							 }
						 });
				}
			}
		}
		
		public static class Mapping {
			private final String domain;
			private final String path;
			private final String type;
			
			private Mapping(@Nullable String domain, String path, String type) {
				this.domain = domain;
				this.path = path;
				this.type = type;
			}
			
			public boolean matches(ResourceLocation iconLocation) {
				return (this.domain == null || this.domain.equals(iconLocation.getNamespace())) &&
						RenderUtil.stripStart(iconLocation.getPath(), "blocks/").toLowerCase().contains(this.path.toLowerCase());
			}
		}
	}
}