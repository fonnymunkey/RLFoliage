package betterfoliage.compat;

import betterfoliage.BetterFoliage;
import betterfoliage.mixin.forestry.TextureLeavesAccessor;
import betterfoliage.mixin.forestry.TreeDefinitionAccessor;
import betterfoliage.render.feature.RenderingHandler;
import betterfoliage.render.registry.LeafRegistry;
import betterfoliage.render.registry.ModelRenderKey;
import betterfoliage.render.registry.ModelRenderRegistry;
import forestry.api.arboriculture.EnumLeafType;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.ILeafSpriteProvider;
import forestry.arboriculture.blocks.PropertyTreeType;
import forestry.arboriculture.genetics.TreeDefinition;
import forestry.arboriculture.models.TextureLeaves;
import forestry.arboriculture.tiles.TileLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ForestryHandler {
	
	public static void init() {
		RenderingHandler.log(Level.INFO, "RLFoliage Forestry support is enabled");
		LeafRegistry.LEAF_REGISTRY.addRegistry(ForestryLeafRegistry.FORESTRY_LEAF_REGISTRY);
	}
	
	public static class ForestryLeafRegistry implements ModelRenderRegistry<LeafRegistry.LeafInfo> {
		public static final ForestryLeafRegistry FORESTRY_LEAF_REGISTRY = new ForestryLeafRegistry();
		
		private final Logger logger = BetterFoliage.LOGGER_DETAIL;
		private final Map<ResourceLocation,ModelRenderKey<LeafRegistry.LeafInfo>> textureToKey = new HashMap<>();
		private Map<ResourceLocation,LeafRegistry.LeafInfo> textureToValue = new HashMap<>();
		
		private ForestryLeafRegistry() {
			super();
		}
		
		@Override
		public LeafRegistry.LeafInfo get(IBlockState state, IBlockAccess world, BlockPos pos) {
			for(Map.Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
				if(entry.getKey() instanceof PropertyTreeType && entry.getValue() instanceof TreeDefinition) {
					TreeDefinition treeDef = (TreeDefinition)entry.getValue();
					IAlleleTreeSpecies species = ((TreeDefinitionAccessor)treeDef).betterfoliage$getTreeSpecies();
					ILeafSpriteProvider spriteProvider = species.getLeafSpriteProvider();
					ResourceLocation textureLoc = spriteProvider.getSprite(false, Minecraft.isFancyGraphicsEnabled());
					return this.textureToValue.get(textureLoc);
				}
			}
			
			TileEntity tile = getTileEntitySafe(world, pos);
			if(tile == null) return null;
			if(!(tile instanceof TileLeaves)) return null;
			
			ResourceLocation textureLoc = ((TileLeaves)tile).getLeaveSprite(Minecraft.isFancyGraphicsEnabled());
			if(textureLoc == null) return null;
			return this.textureToValue.get(textureLoc);
		}
		
		@SubscribeEvent
		public void handlePreStitch(TextureStitchEvent.Pre event) {
			this.textureToValue = new HashMap<>();
			
			Map<EnumLeafType,TextureLeaves> allLeaves = TextureLeavesAccessor.betterfoliage$getLeafTextures();
			for(Map.Entry<EnumLeafType,TextureLeaves> entry : allLeaves.entrySet()) {
				this.logger.log(Level.DEBUG, "ForestryLeavesSupport: base leaf type {}", entry.getKey().toString());
				ResourceLocation plain = ((TextureLeavesAccessor)entry.getValue()).betterfoliage$getPlain();
				ResourceLocation fancy = ((TextureLeavesAccessor)entry.getValue()).betterfoliage$getFancy();
				ResourceLocation pollinatedPlain = ((TextureLeavesAccessor)entry.getValue()).betterfoliage$getPollinatedPlain();
				ResourceLocation pollinatedFancy = ((TextureLeavesAccessor)entry.getValue()).betterfoliage$getPollinatedFancy();
				
				LeafRegistry.StandardLeafKey plainKey = new LeafRegistry.StandardLeafKey(this.logger, plain.toString());
				LeafRegistry.StandardLeafKey fancyKey = new LeafRegistry.StandardLeafKey(this.logger, fancy.toString());
				LeafRegistry.StandardLeafKey pollinatedPlainKey = new LeafRegistry.StandardLeafKey(this.logger, pollinatedPlain.toString());
				LeafRegistry.StandardLeafKey pollinatedFancyKey = new LeafRegistry.StandardLeafKey(this.logger, pollinatedFancy.toString());
				
				plainKey.onPreStitch(event.getMap());
				fancyKey.onPreStitch(event.getMap());
				pollinatedPlainKey.onPreStitch(event.getMap());
				pollinatedFancyKey.onPreStitch(event.getMap());
				
				this.textureToKey.put(plain, plainKey);
				this.textureToKey.put(fancy, fancyKey);
				this.textureToKey.put(pollinatedPlain, pollinatedPlainKey);
				this.textureToKey.put(pollinatedFancy, pollinatedFancyKey);
			}
		}
		
		@SubscribeEvent(priority = EventPriority.LOW)
		public void handlePostStitch(TextureStitchEvent.Post event) {
			this.textureToValue = this.textureToKey.entrySet().stream().collect(
					Collectors.toMap(Map.Entry::getKey, entry ->
							entry.getValue().resolveSprites(event.getMap())));
			this.textureToKey.clear();
		}
		
		private static TileEntity getTileEntitySafe(IBlockAccess access, BlockPos pos) {
			try {
				return OptifineCompatWrapper.isBlockLoadedSafe(access, pos) ? access.getTileEntity(pos) : null;
			}
			catch(Exception ignored) {
				return null;
			}
		}
	}
}