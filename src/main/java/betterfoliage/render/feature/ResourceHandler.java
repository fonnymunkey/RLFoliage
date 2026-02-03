package betterfoliage.render.feature;

import betterfoliage.BetterFoliage;
import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Int3;
import betterfoliage.render.model.Model;
import betterfoliage.render.util.RenderUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public abstract class ResourceHandler {
	private final List<Object> resources = new ArrayList<>();
	
	protected ResourceHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void afterPreStitch() { }
	
	public void afterPostStitch() { }
	
	public IconHolder getIconStatic(String domain, String path) {
		IconHolder iconStatic = new IconHolder(domain, path);
		this.resources.add(iconStatic);
		return iconStatic;
	}
	
	public IconHolder getIconStatic(ResourceLocation loc) {
		return this.getIconStatic(loc.getNamespace(), loc.getPath());
	}
	
	public IconSet getIconSet(String domain, String namePattern) {
		IconSet iconSet = new IconSet(domain, namePattern);
		this.resources.add(iconSet);
		return iconSet;
	}
	
	public IconSet getIconSet(ResourceLocation loc) {
		return this.getIconSet(loc.getNamespace(), loc.getPath());
	}
	
	public ModelHolder getModelHolder(Consumer<Model> init) {
		ModelHolder modelHolder = new ModelHolder(init);
		this.resources.add(modelHolder);
		return modelHolder;
	}
	
	public ModelSet getModelSet(int size, BiConsumer<Model,Integer> init) {
		ModelSet modelSet = new ModelSet(size, init);
		this.resources.add(modelSet);
		return modelSet;
	}
	
	public VectorSet getVectorSet(int size, IntFunction<Double3> init) {
		VectorSet vectorSet = new VectorSet(size, init);
		this.resources.add(vectorSet);
		return vectorSet;
	}
	
	public SimplexNoise getSimplexNoise() {
		SimplexNoise simplexNoise = new SimplexNoise();
		this.resources.add(simplexNoise);
		return simplexNoise;
	}
	
	@SubscribeEvent
	public void onPreStitch(TextureStitchEvent.Pre event) {
		this.resources.forEach(resource -> {
			if(resource instanceof IStitchListener) ((IStitchListener)resource).onPreStitch(event.getMap());
		});
		this.afterPreStitch();
	}
	
	@SubscribeEvent
	public void onPostStitch(TextureStitchEvent.Post event) {
		this.resources.forEach(resource -> {
			if(resource instanceof IStitchListener) ((IStitchListener)resource).onPostStitch(event.getMap());
		});
		this.afterPostStitch();
	}
	
	@SubscribeEvent(priority = EventPriority.LOW)
	public void handleConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(BetterFoliage.MODID)) {
			this.resources.forEach(resource -> {
				if(resource instanceof IConfigChangeListener) ((IConfigChangeListener)resource).onConfigChange();
			});
		}
	}
	
	@SubscribeEvent
	public void handleWorldLoad(WorldEvent.Load event) {
		this.resources.forEach(resource -> {
			if(resource instanceof IWorldLoadListener) ((IWorldLoadListener)resource).onWorldLoad(event.getWorld());
		});
	}
	
	public static class IconHolder implements IStitchListener {
		private final ResourceLocation iconRes;
		public TextureAtlasSprite icon = null;
		
		public IconHolder(String domain, String name) {
			this.iconRes = new ResourceLocation(domain, name);
		}
		
		@Override
		public void onPreStitch(TextureMap atlas) {
			atlas.registerSprite(this.iconRes);
		}
		
		@Override
		public void onPostStitch(TextureMap atlas) {
			TextureAtlasSprite texture = atlas.getTextureExtry(this.iconRes.toString());
			if(texture == null) texture = atlas.getMissingSprite();
			this.icon = texture;
		}
	}
	
	public static class IconSet implements IStitchListener {
		private final String domain;
		private final String namePattern;
		private final ResourceLocation[] resources = new ResourceLocation[16];
		private final TextureAtlasSprite[] icons = new TextureAtlasSprite[16];
		public int size = 0;
		
		public IconSet(String domain, String namePattern) {
			this.domain = domain;
			this.namePattern = namePattern;
		}
		
		@Override
		public void onPreStitch(TextureMap atlas) {
			this.size = 0;
			for(int i = 0; i < 16; i++) {
				this.icons[i] = null;
				String formatted = String.format(this.namePattern, i);
				if(RenderUtil.getResource(new ResourceLocation(this.domain, String.format("textures/%s.png", formatted))) != null) {
					ResourceLocation res = new ResourceLocation(this.domain, formatted);
					this.resources[this.size++] = res;
					atlas.registerSprite(res);
				}
			}
		}
		
		@Override
		public void onPostStitch(TextureMap atlas) {
			for(int i = 0; i < this.size; i++) {
				TextureAtlasSprite texture = atlas.getTextureExtry(this.resources[i].toString());
				if(texture == null) texture = atlas.getMissingSprite();
				this.icons[i] = texture;
			}
		}
		
		public TextureAtlasSprite get(int i) {
			return this.size == 0 ? null : this.icons[i%this.size];
		}
	}
	
	public static class ModelHolder implements IConfigChangeListener {
		private final Consumer<Model> init;
		public Model model;
		
		public ModelHolder(Consumer<Model> init) {
			this.init = init;
			this.model = new Model();
			init.accept(this.model);
		}
		
		@Override
		public void onConfigChange() {
			this.model = new Model();
			this.init.accept(this.model);
		}
	}
	
	public static class ModelSet implements IConfigChangeListener {
		private final int size;
		private final BiConsumer<Model,Integer> init;
		private final Model[] models;
		
		public ModelSet(int size, BiConsumer<Model,Integer> init) {
			this.size = size;
			this.init = init;
			this.models = new Model[size];
			for(int i = 0; i < size; i++) {
				Model model = new Model();
				init.accept(model, i);
				this.models[i] = model;
			}
		}
		
		@Override
		public void onConfigChange() {
			for(int i = 0; i < this.size; i++) {
				Model model = new Model();
				this.init.accept(model, i);
				this.models[i] = model;
			}
		}
		
		public Model get(int i) {
			return this.size == 0 ? null : this.models[i%this.size];
		}
	}
	
	public static class VectorSet implements IConfigChangeListener {
		private final int size;
		private final IntFunction<Double3> init;
		private final Double3[] vectors;
		
		public VectorSet(int size, IntFunction<Double3> init) {
			this.size = size;
			this.init = init;
			this.vectors = new Double3[size];
			for(int i = 0; i < size; i++) {
				this.vectors[i] = init.apply(i);
			}
		}
		
		@Override
		public void onConfigChange() {
			for(int i = 0; i < this.size; i++) {
				this.vectors[i] = this.init.apply(i);
			}
		}
		
		public Double3 get(int i) {
			return this.size == 0 ? null : this.vectors[i%this.size];
		}
	}
	
	public static class SimplexNoise implements IWorldLoadListener {
		private NoiseGeneratorSimplex noise;
		
		public void onWorldLoad(World world) {
			this.noise = new NoiseGeneratorSimplex(new Random(world.getSeed()));
		}
		
		public int get(int x, int z) {
			return MathHelper.floor((this.noise.getValue(x, z) + 1.0D) * 32.0D);
		}
		
		public int get(Int3 pos) {
			return this.get(pos.x, pos.z);
		}
		
		public int get(BlockPos pos) {
			return this.get(pos.getX(), pos.getZ());
		}
	}
	
	public interface IStitchListener {
		void onPreStitch(TextureMap atlas);
		void onPostStitch(TextureMap atlas);
	}
	
	public interface IConfigChangeListener {
		void onConfigChange();
	}
	
	public interface IWorldLoadListener {
		void onWorldLoad(World world);
	}
}