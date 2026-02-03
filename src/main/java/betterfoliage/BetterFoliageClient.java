package betterfoliage;

import betterfoliage.compat.ForestryCompatWrapper;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.render.feature.*;
import betterfoliage.render.feature.particle.LeafWindTracker;
import betterfoliage.render.feature.particle.RisingSoulResources;
import betterfoliage.render.generator.GeneratorPack;
import betterfoliage.render.registry.*;

public abstract class BetterFoliageClient {
	
	public static final GeneratorPack GENERATOR_PACK = GeneratorPack.GENERATOR_PACK;
	
	public static final ModelRenderRegistryRoot<CactusRegistry.CactusInfo> CACTUS_REGISTRY = CactusRegistry.CACTUS_REGISTRY;
	public static final ModelRenderRegistryRoot<GrassRegistry.GrassInfo> GRASS_REGISTRY = GrassRegistry.GRASS_REGISTRY;
	public static final ModelRenderRegistryRoot<LeafRegistry.LeafInfo> LEAF_REGISTRY = LeafRegistry.LEAF_REGISTRY;
	
	public static final CactusRegistry.StandardCactusRegistry STANDARD_CACTUS_REGISTRY = CactusRegistry.StandardCactusRegistry.STANDARD_CACTUS_REGISTRY;
	public static final GrassRegistry.StandardGrassRegistry STANDARD_GRASS_REGISTRY = GrassRegistry.StandardGrassRegistry.STANDARD_GRASS_REGISTRY;
	public static final LeafRegistry.StandardLeafRegistry STANDARD_LEAF_REGISTRY = LeafRegistry.StandardLeafRegistry.STANDARD_LEAF_REGISTRY;
	
	public static final RenderAlgae RENDER_ALGAE = RenderAlgae.RENDER_ALGAE;
	public static final RenderCactus RENDER_CACTUS = RenderCactus.RENDER_CACTUS;
	public static final RenderConnectedGrass RENDER_CONNECTED_GRASS = RenderConnectedGrass.RENDER_CONNECTED_GRASS;
	public static final RenderCoral RENDER_CORAL = RenderCoral.RENDER_CORAL;
	public static final RenderGrass RENDER_GRASS = RenderGrass.RENDER_GRASS;
	public static final RenderLeaves RENDER_LEAVES = RenderLeaves.RENDER_LEAVES;
	public static final RenderLilypad RENDER_LILYPAD = RenderLilypad.RENDER_LILYPAD;
	public static final RenderMycelium RENDER_MYCELIUM = RenderMycelium.RENDER_MYCELIUM;
	public static final RenderNetherrack RENDER_NETHERRACK = RenderNetherrack.RENDER_NETHERRACK;
	public static final RenderPackedIce RENDER_PACKED_ICE = RenderPackedIce.RENDER_PACKED_ICE;
	public static final RenderReeds RENDER_REEDS = RenderReeds.RENDER_REEDS;
	
	public static final LeafParticleRegistry LEAF_PARTICLE_REGISTRY = LeafParticleRegistry.LEAF_PARTICLE_REGISTRY;
	public static final LeafWindTracker LEAF_WIND_TRACKER = LeafWindTracker.LEAF_WIND_TRACKER;
	public static final RisingSoulResources RISING_SOUL_RESOURCES = RisingSoulResources.RISING_SOUL_RESOURCES;
	
	public static void init() {
		OptifineCompatWrapper.init();
		ForestryCompatWrapper.init();
	}
}