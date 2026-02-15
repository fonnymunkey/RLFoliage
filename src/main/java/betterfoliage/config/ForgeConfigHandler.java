package betterfoliage.config;

import betterfoliage.BetterFoliage;
import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.mixin.BiomeDictionaryTypeAccessor;
import fermiumbooter.annotations.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.*;

@Config(modid = BetterFoliage.MODID)
public class ForgeConfigHandler {
	
	@Config.Name("Algae")
	@Config.LangKey("betterfoliage.algae")
	public static final Algae ALGAE = new Algae();
	
	@Config.Name("Block Types")
	@Config.LangKey("betterfoliage.blocks")
	public static final Blocks BLOCKS = new Blocks();
	
	@Config.Name("Cactus")
	@Config.LangKey("betterfoliage.cactus")
	public static final Cactus CACTUS = new Cactus();
	
	@Config.Name("Connected Grass")
	@Config.LangKey("betterfoliage.connectedGrass")
	public static final ConnectedGrass CONNECTEDGRASS = new ConnectedGrass();
	
	@Config.Name("Coral")
	@Config.LangKey("betterfoliage.coral")
	public static final Coral CORAL = new Coral();
	
	@Config.Name("Falling Leaves")
	@Config.LangKey("betterfoliage.fallingLeaves")
	public static final FallingLeaves FALLINGLEAVES = new FallingLeaves();
	
	@Config.Name("Global")
	@Config.LangKey("betterfoliage.global")
	public static final Global GLOBAL = new Global();
	
	@Config.Name("Leaves")
	@Config.LangKey("betterfoliage.leaves")
	public static final Leaves LEAVES = new Leaves();
	
	@Config.Name("Lilypad")
	@Config.LangKey("betterfoliage.lilypad")
	public static final Lilypad LILYPAD = new Lilypad();
	
	@Config.Name("Netherrack")
	@Config.LangKey("betterfoliage.netherrack")
	public static final Netherrack NETHERRACK = new Netherrack();
	
	@Config.Name("Packed Ice")
	@Config.LangKey("betterfoliage.packedIce")
	public static final PackedIce PACKEDICE = new PackedIce();
	
	@Config.Name("Reed")
	@Config.LangKey("betterfoliage.reed")
	public static final Reed REED = new Reed();
	
	@Config.Name("Rising Soul")
	@Config.LangKey("betterfoliage.risingSoul")
	public static final RisingSoul RISINGSOUL = new RisingSoul();
	
	@Config.Name("Shaders")
	@Config.LangKey("betterfoliage.shaders")
	public static final Shaders SHADERS = new Shaders();
	
	@Config.Name("Short Grass")
	@Config.LangKey("betterfoliage.shortGrass")
	public static final ShortGrass SHORTGRASS = new ShortGrass();
	
	public static class Algae {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.25D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.1D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 1.0D;
		
		@Config.Name("Height Min")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMin")
		public double heightMin = 0.5D;
		
		@Config.Name("Height Max")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMax")
		public double heightMax = 1.0D;
		
		@Config.Name("Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.population")
		public int population = 48;
		
		@Config.Name("Shader Wind")
		@Config.LangKey("betterfoliage.shaderWind")
		public boolean shaderWind = true;
		
		@Config.Name("Biomes")
		@Config.LangKey("betterfoliage.algae.biomes")
		public String[] biomes = new String[] {"river", "ocean"};
		
		@Config.Ignore
		private transient final BitSet biomeIds = new BitSet();
		
		public boolean isBiomeValid(int id) {
			return this.biomeIds.get(id);
		}
		
		public void refreshConfig() {
			this.biomeIds.clear();
			filterBiomesByNameOrId(this.biomes, this.biomeIds);
		}
	}
	
	public static class Blocks {
		
		@Config.Name("Leaves Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.leavesClassesWhitelist")
		public String[] leavesClassesWhitelist = new String[] {
				"net.minecraft.block.BlockLeaves",
				"biomesoplenty.common.block.BlockBOPLeaves",
				"com.gildedgames.aether.common.blocks.natural.BlockAetherLeaves",
				"shadows.plants2.block.BlockEnumLeaves",
				"shadows.plants2.block.BlockEnumNetherLeaves",
				"snownee.cuisine.blocks.BlockModLeaves",
				"snownee.cuisine.blocks.BlockShearedLeaves"
		};
		
		@Config.Name("Leaves Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.leavesClassesBlacklist")
		public String[] leavesClassesBlacklist = new String[] {};
		
		@Config.Name("Leaves Models")
		@Config.LangKey("betterfoliage.blocks.leavesModels")
		public String[] leavesModels = new String[] {
				"minecraft:block/leaves,all",
				"minecraft:block/cube_all,all",
				"biomesoplenty:block/leaves_overlay,under"
		};
		
		@Config.Name("Grass Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.grassClassesWhitelist")
		public String[] grassClassesWhitelist = new String[] {
				"net.minecraft.block.BlockGrass",
				"biomesoplenty.common.block.BlockBOPGrass",
				"tconstruct.blocks.slime.SlimeGrass",
				"enhancedbiomes.blocks.BlockGrassEB",
				"com.bioxx.tfc.Blocks.Terrain.BlockGrass",
				"com.shinoow.abyssalcraft.common.blocks.BlockDreadGrass",
				"com.shinoow.abyssalcraft.common.blocks.BlockDarklandsgrass"
		};
		
		@Config.Name("Grass Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.grassClassesBlacklist")
		public String[] grassClassesBlacklist = new String[] {};
		
		@Config.Name("Grass Models")
		@Config.LangKey("betterfoliage.blocks.grassModels")
		public String[] grassModels = new String[] {
				"block/grass,top,bottom",
				"block/cube_bottom_top,top,bottom",
				"block/soil/grass_master,top,bottom"
		};
		
		@Config.Name("Mycelium Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.myceliumWhitelist")
		public String[] myceliumClassesWhitelist = new String[] {
				"net.minecraft.block.BlockMycelium",
				"nex.block.BlockMycelium"
		};
		
		@Config.Name("Mycelium Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.myceliumBlacklist")
		public String[] myceliumClassesBlacklist = new String[] {};
		
		@Config.Name("Dirt Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.dirtWhitelist")
		public String[] dirtClassesWhitelist = new String[] {
				"net.minecraft.block.BlockDirt",
				"biomesoplenty.common.block.BlockBOPDirt",
				"enhancedbiomes.blocks.BlockSoilEB",
				"com.bioxx.tfc.Blocks.Terrain.BlockDirt",
				"net.aetherteam.aether.blocks.natural.BlockAetherDirt",
				"com.gildedgames.aether.common.blocks.natural.BlockAetherDirt",
				"slimeknights.tconstruct.world.block.BlockSlimeDirt"
		};
		
		@Config.Name("Dirt Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.dirtBlacklist")
		public String[] dirtClassesBlacklist = new String[] {};
		
		@Config.Name("Crops Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.cropsWhitelist")
		public String[] cropsClassesWhitelist = new String[] {
				"net.minecraft.block.BlockTallGrass",
				"net.minecraft.block.BlockCrops",
				"biomesoplenty.common.block.BlockBOPFlower",
				"biomesoplenty.common.block.BlockBOPTurnip",
				"biomesoplenty.common.block.BlockBOPPlant",
				"tconstruct.blocks.slime.SlimeTallGrass",
				"plantmegapack.block.PMPBlockBerrybush",
				"plantmegapack.block.PMPBlockCrops",
				"plantmegapack.block.PMPBlockDesert",
				"plantmegapack.block.PMPBlockFern",
				"plantmegapack.block.PMPBlockFlowerMulti",
				"plantmegapack.block.PMPBlockFlowerSingle",
				"plantmegapack.block.PMPBlockForest",
				"plantmegapack.block.PMPBlockGrass",
				"plantmegapack.block.PMPBlockJungle",
				"plantmegapack.block.PMPBlockMountain",
				"plantmegapack.block.PMPBlockSavanna",
				"plantmegapack.block.PMPBlockShrub",
				"plantmegapack.block.PMPBlockWetlands",
				"com.pam.harvestcraft.BlockPamCrop",
				"com.pam.harvestcraft.BlockPamDesertGarden",
				"com.pam.harvestcraft.BlockPamNormalGarden",
				"com.pam.harvestcraft.BlockPamWaterGarden",
				"shadows.plants2.block.BlockEnumCrop",
				"snownee.cuisine.blocks.BlockCuisineCrops"
		};
		
		@Config.Name("Crops Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.cropsBlacklist")
		public String[] cropsClassesBlacklist = new String[] {
				"net.minecraft.block.BlockReed",
				"net.minecraft.block.BlockDoublePlant",
				"net.minecraft.block.BlockCarrot",
				"net.minecraft.block.BlockPotato",
				"snownee.cuisine.blocks.BlockDoubleCrops"
		};
		
		@Config.Name("Sand Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.sandWhitelist")
		public String[] sandClassesWhitelist = new String[] {
				"net.minecraft.block.BlockSand",
				"com.bioxx.tfc.Blocks.Terrain.BlockSand"
		};
		
		@Config.Name("Sand Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.sandBlacklist")
		public String[] sandClassesBlacklist = new String[] {};
		
		@Config.Name("Lilypad Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.lilypadWhitelist")
		public String[] lilypadClassesWhitelist = new String[] {
				"net.minecraft.block.BlockLilyPad",
				"biomesoplenty.common.block.BlockBOPLilypad",
				"com.bioxx.tfc.Blocks.Vanilla.BlockCustomLilyPad"
		};
		
		@Config.Name("Lilypad Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.lilypadBlacklist")
		public String[] lilypadClassesBlacklist = new String[] {};
		
		@Config.Name("Cactus Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.cactusWhitelist")
		public String[] cactusClassesWhitelist = new String[] {
				"net.minecraft.block.BlockCactus",
				"com.bioxx.tfc.Blocks.Vanilla.BlockCustomCactus"
		};
		
		@Config.Name("Cactus Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.cactusBlacklist")
		public String[] cactusClassesBlacklist = new String[] {};
		
		@Config.Name("Cactus Models")
		@Config.LangKey("betterfoliage.blocks.cactusModels")
		public String[] cactusModels = new String[] {
				"minecraft:block/cactus,top,bottom,side"
		};
		
		@Config.Name("Netherrack Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.netherrackWhitelist")
		public String[] netherrackClassesWhitelist = new String[] {
				"net.minecraft.block.BlockNetherrack",
				"nex.block.BlockNetherrack"
		};
		
		@Config.Name("Netherrack Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.netherrackBlacklist")
		public String[] netherrackClassesBlacklist = new String[] {};
		
		@Config.Name("PackedIce Classes Whitelist")
		@Config.LangKey("betterfoliage.blocks.packedIceWhitelist")
		public String[] packedIceClassesWhitelist = new String[] {
				"net.minecraft.block.BlockPackedIce"
		};
		
		@Config.Name("PackedIce Classes Blacklist")
		@Config.LangKey("betterfoliage.blocks.packedIceBlacklist")
		public String[] packedIceClassesBlacklist = new String[] {};
		
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher leavesClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.leavesClassesBlacklist, () -> this.leavesClassesWhitelist);
		@Config.Ignore
		public transient final List<ModelTextureList> leavesModelsTextures = new ArrayList<>();
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher grassClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.grassClassesBlacklist, () -> this.grassClassesWhitelist);
		@Config.Ignore
		public transient final List<ModelTextureList> grassModelsTextures = new ArrayList<>();
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher myceliumClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.myceliumClassesBlacklist, () -> this.myceliumClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher dirtClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.dirtClassesBlacklist, () -> this.dirtClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher cropsClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.cropsClassesBlacklist, () -> this.cropsClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher sandClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.sandClassesBlacklist, () -> this.sandClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher lilypadClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.lilypadClassesBlacklist, () -> this.lilypadClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher cactusClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.cactusClassesBlacklist, () -> this.cactusClassesWhitelist);
		@Config.Ignore
		public transient final List<ModelTextureList> cactusModelsTextures = new ArrayList<>();
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher netherrackClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.netherrackClassesBlacklist, () -> this.netherrackClassesWhitelist);
		@Config.Ignore
		public transient final BlockMatcherRegistry.BlockMatcher packediceClassesMatcher = BlockMatcherRegistry.getBlockMatcher(() -> this.packedIceClassesBlacklist, () -> this.packedIceClassesWhitelist);
		
		public void refreshConfig() {
			BlockMatcherRegistry.refreshMatchers();
			this.leavesModelsTextures.clear();
			this.grassModelsTextures.clear();
			this.cactusModelsTextures.clear();
			parseModelTexturesFromStrings(this.leavesModels, this.leavesModelsTextures);
			parseModelTexturesFromStrings(this.grassModels, this.grassModelsTextures);
			parseModelTexturesFromStrings(this.cactusModels, this.cactusModelsTextures);
		}
		
		private static void parseModelTexturesFromStrings(String[] modelEntries, List<ModelTextureList> targetList) {
			for(String modelEntry : modelEntries) {
				if(modelEntry == null) continue;
				modelEntry = modelEntry.trim();
				if(modelEntry.isEmpty()) continue;
				
				String[] split = modelEntry.split(",");
				ArrayList<String> entries = new ArrayList<>();
				for(String entry : split) {
					if(entry == null) continue;
					entry = entry.trim();
					if(entry.isEmpty()) continue;
					entries.add(entry);
				}
				
				if(entries.size() < 2) {
					BetterFoliage.LOGGER_DETAIL.log(Level.WARN, "Invalid Model Texture Entry: {}", modelEntry);
				}
				else targetList.add(new ModelTextureList(new ResourceLocation(entries.remove(0)), entries));
			}
		}
	}
	
	public static class Cactus {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.5D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.1D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 0.8D;
		
		@Config.Name("Size Variation")
		@Config.RangeDouble(max = 0.5D)
		@Config.LangKey("betterfoliage.cactus.sizeVariation")
		public double sizeVariation = 0.1D;
	}
	
	public static class ConnectedGrass {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Snow Enabled")
		@Config.LangKey("betterfoliage.connectedGrass.snowEnabled")
		public boolean snowEnabled = true;
		
		@Config.Name("Longer Sides")
		@Config.LangKey("betterfoliage.connectedGrass.longerSides")
		public boolean longerSides = true;
	}
	
	public static class Coral {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.2D;
		
		@Config.Name("Vertical Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.vOffset")
		public double vOffset = 0.1D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 0.7D;
		
		@Config.Name("Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.population")
		public int population = 48;
		
		@Config.Name("Crust Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.coral.crustSize")
		public double crustSize = 1.4D;
		
		@Config.Name("Chance")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.coral.chance")
		public int chance = 32;
		
		@Config.Name("Shallow Water")
		@Config.LangKey("betterfoliage.coral.shallowWater")
		public boolean shallowWater = false;
		
		@Config.Name("Biomes")
		@Config.LangKey("betterfoliage.coral.biomes")
		public String[] biomes = new String[] {"river", "ocean", "beach"};
		
		@Config.Ignore
		private transient final BitSet biomeIds = new BitSet();
		
		public boolean isBiomeValid(int id) {
			return this.biomeIds.get(id);
		}
		
		public void refreshConfig() {
			this.biomeIds.clear();
			filterBiomesByNameOrId(this.biomes, this.biomeIds);
		}
	}
	
	public static class FallingLeaves {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Speed")
		@Config.RangeDouble(min = 0.01D, max = 0.15D)
		@Config.LangKey("betterfoliage.fallingLeaves.speed")
		public double speed = 0.05D;
		
		@Config.Name("Wind Strength")
		@Config.RangeDouble(min = 0.1D, max = 2.0D)
		@Config.LangKey("betterfoliage.fallingLeaves.windStrength")
		public double windStrength = 0.5D;
		
		@Config.Name("Storm Strength")
		@Config.RangeDouble(min = 0.1D, max = 2.0D)
		@Config.LangKey("betterfoliage.fallingLeaves.stormStrength")
		public double stormStrength = 2.0D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.25D, max = 1.5D)
		@Config.LangKey("betterfoliage.fallingLeaves.size")
		public double size = 0.75D;
		
		@Config.Name("Chance")
		@Config.RangeDouble(min = 0.001D, max = 1.0D)
		@Config.LangKey("betterfoliage.fallingLeaves.chance")
		public double chance = 0.05D;
		
		@Config.Name("Perturb")
		@Config.RangeDouble(min = 0.01D, max = 1.0D)
		@Config.LangKey("betterfoliage.fallingLeaves.perturb")
		public double perturb = 0.25D;
		
		@Config.Name("Lifetime")
		@Config.RangeDouble(min = 1.0D, max = 15.0D)
		@Config.LangKey("betterfoliage.fallingLeaves.lifetime")
		public double lifetime = 10.0D;
		
		@Config.Name("Opacity Hack")
		@Config.LangKey("betterfoliage.fallingLeaves.opacityHack")
		public boolean opacityHack = false;
		
		@Config.Name("Fade Out")
		@Config.LangKey("betterfoliage.fallingLeaves.fadeOut")
		public boolean fadeOut = true;
	}
	
	@MixinConfig(name = BetterFoliage.MODID)
	public static class Global {
		
		@Config.Name("Enable Mod")
		@Config.LangKey("betterfoliage.global.enabled")
		public boolean enabled = true;
		
		@Config.Name("Log Render Error to Chat")
		@Config.LangKey("betterfoliage.global.renderError")
		public boolean logRenderErrorToChat = false;
		
		@Config.Name("Enable Forestry Compat")
		@Config.LangKey("betterfoliage.global.forestry")
		public boolean enableForestryCompat = true;
		
		@Config.Comment(
				"Adds a patch for MC-114265 created by jonathan2520 at https://bugs-legacy.mojang.com/browse/MC-114265" + "\n" +
				"Fixes improper mipmap generation causing transparent pixels to merge into black pixels, as well as increases performance" + "\n" +
				"Created by and all credit goes to jonathan2520")
		@Config.Name("Mipmap Generation Patch (MC-114265)")
		@Config.RequiresMcRestart
		@MixinConfig.MixinToggle(earlyMixin = "mixins.betterfoliage.mipmap.json", defaultValue = true)
		public boolean mipmapGenPatch = true;
		
		@Config.Comment(
				"Converts PooledMutableBlockPos to use a ThreadLocal pool rather than a synchronized pool to avoid thread contention" + "\n" +
				"This causes a very slight increase in memory usage however should improve render performance" + "\n" +
				"This patch is less needed when using Optifine as it has its own similar patches (But shouldn't conflict)")
		@Config.Name("PooledMutableBlockPos Thread Contention Patch")
		@Config.RequiresMcRestart
		@MixinConfig.MixinToggle(earlyMixin = "mixins.betterfoliage.pooledpos.json", defaultValue = true)
		public boolean pooledPosPatch = true;
		
		@Config.Comment(
				"Adjusts block layer rendering to render cutout textures using mipmaps if enabled (Better visually)" + "\n" +
				"Can be disabled if other mods cause issues rendering cutout mipmaps, if disabled it will prefer non-mipmap cutout rendering")
		@Config.Name("Render Layer Adjustments")
		public boolean renderLayerAdjustments = true;
	}
	
	public static class Leaves {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.2D;
		
		@Config.Name("Vertical Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.vOffset")
		public double vOffset = 0.1D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.75D, max = 2.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 1.4D;
		
		@Config.Name("Shader Wind")
		@Config.LangKey("betterfoliage.shaderWind")
		public boolean shaderWind = true;
		
		@Config.Name("Dense")
		@Config.LangKey("betterfoliage.leaves.dense")
		public boolean dense = false;
		
		@Config.Name("Snow Enabled")
		@Config.LangKey("betterfoliage.leaves.snowEnabled")
		public boolean snowEnabled = true;
		
		@Config.Name("Hide Internal")
		@Config.LangKey("betterfoliage.leaves.hideInternal")
		public boolean hideInternal = true;
	}
	
	public static class Lilypad {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.25D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.1D;
		
		@Config.Name("Shader Wind")
		@Config.LangKey("betterfoliage.shaderWind")
		public boolean shaderWind = true;
		
		@Config.Name("Flower Chance")
		@Config.RangeInt(min = 0, max = 64)
		@Config.LangKey("betterfoliage.lilypad.flowerChance")
		public int flowerChance = 16;
	}
	
	public static class Netherrack {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.2D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 1.0D;
		
		@Config.Name("Height Min")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMin")
		public double heightMin = 0.6D;
		
		@Config.Name("Height Max")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMax")
		public double heightMax = 0.8D;
		
		@Config.Name("Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.population")
		public int population = 64;
	}
	
	public static class PackedIce {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.1D;
		
		@Config.Name("Height Min")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMin")
		public double heightMin = 1.0D;
		
		@Config.Name("Height Max")
		@Config.RangeDouble(min = 0.1D, max = 1.5D)
		@Config.LangKey("betterfoliage.heightMax")
		public double heightMax = 1.5D;
		
		@Config.Name("Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.population")
		public int population = 16;
	}
	
	public static class Reed {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.2D;
		
		@Config.Name("Height Min")
		@Config.RangeDouble(min = 1.5D, max = 3.5D)
		@Config.LangKey("betterfoliage.heightMin")
		public double heightMin = 1.7D;
		
		@Config.Name("Height Max")
		@Config.RangeDouble(min = 1.5D, max = 3.5D)
		@Config.LangKey("betterfoliage.heightMax")
		public double heightMax = 2.2D;
		
		@Config.Name("Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.population")
		public int population = 32;
		
		@Config.Name("Shader Wind")
		@Config.LangKey("betterfoliage.shaderWind")
		public boolean shaderWind = true;
		
		@Config.Name("Biomes")
		@Config.LangKey("betterfoliage.reed.biomes")
		public String[] biomes = getBiomesFromMinTempRain(0.4F, 0.4F);
		
		@Config.Ignore
		private transient final BitSet biomeIds = new BitSet();
		
		public boolean isBiomeValid(int id) {
			return this.biomeIds.get(id);
		}
		
		public void refreshConfig() {
			this.biomeIds.clear();
			filterBiomesByNameOrId(this.biomes, this.biomeIds);
		}
	}
	
	public static class RisingSoul {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Chance")
		@Config.RangeDouble(min = 0.001D, max = 1.0D)
		@Config.LangKey("betterfoliage.risingSoul.chance")
		public double chance = 0.02D;
		
		@Config.Name("Speed")
		@Config.RangeDouble(min = 0.01D, max = 0.15D)
		@Config.LangKey("betterfoliage.risingSoul.speed")
		public double speed = 0.1D;
		
		@Config.Name("Perturb")
		@Config.RangeDouble(min = 0.01D, max = 0.25D)
		@Config.LangKey("betterfoliage.risingSoul.perturb")
		public double perturb = 0.05D;
		
		@Config.Name("Head Size")
		@Config.RangeDouble(min = 0.25D, max = 1.5D)
		@Config.LangKey("betterfoliage.risingSoul.headSize")
		public double headSize = 1.0D;
		
		@Config.Name("Trail Size")
		@Config.RangeDouble(min = 0.25D, max = 1.5D)
		@Config.LangKey("betterfoliage.risingSoul.trailSize")
		public double trailSize = 0.75D;
		
		@Config.Name("Opacity")
		@Config.RangeDouble(min = 0.05D, max = 1.0D)
		@Config.LangKey("betterfoliage.risingSoul.opacity")
		public double opacity = 0.5D;
		
		@Config.Name("Size Decay")
		@Config.RangeDouble(min = 0.5D, max = 1.0D)
		@Config.LangKey("betterfoliage.risingSoul.sizeDecay")
		public double sizeDecay = 0.97D;
		
		@Config.Name("Opacity Decay")
		@Config.RangeDouble(min = 0.5D, max = 1.0D)
		@Config.LangKey("betterfoliage.risingSoul.opacityDecay")
		public double opacityDecay = 0.97D;
		
		@Config.Name("Lifetime")
		@Config.RangeDouble(min = 1.0D, max = 15.0D)
		@Config.LangKey("betterfoliage.risingSoul.lifetime")
		public double lifetime = 4.0D;
		
		@Config.Name("Trail Length")
		@Config.RangeInt(min = 2, max = 128)
		@Config.LangKey("betterfoliage.risingSoul.trailLength")
		public int trailLength = 48;
		
		@Config.Name("Trail Density")
		@Config.RangeInt(min = 1, max = 16)
		@Config.LangKey("betterfoliage.risingSoul.trailDensity")
		public int trailDensity = 3;
	}
	
	public static class Shaders {
		
		@Config.Name("Leaves Block ID")
		@Config.RangeInt(min = 1, max = 65535)
		@Config.LangKey("betterfoliage.shaders.leavesId")
		public int leavesId = (int)OptifineCompatWrapper.LEAVES_DEFAULT_ID;
		
		@Config.Name("Grass Block ID")
		@Config.RangeInt(min = 1, max = 65535)
		@Config.LangKey("betterfoliage.shaders.grassId")
		public int grassId = (int)OptifineCompatWrapper.GRASS_DEFAULT_ID;
	}
	
	public static class ShortGrass {
		
		@Config.Name("Enabled")
		@Config.LangKey("betterfoliage.enabled")
		public boolean enabled = true;
		
		@Config.Name("Horizontal Offset")
		@Config.RangeDouble(max = 0.4D)
		@Config.LangKey("betterfoliage.hOffset")
		public double hOffset = 0.2D;
		
		@Config.Name("Size")
		@Config.RangeDouble(min = 0.5D, max = 1.5D)
		@Config.LangKey("betterfoliage.size")
		public double size = 1.0D;
		
		@Config.Name("Height Min")
		@Config.RangeDouble(min = 0.1D, max = 2.5D)
		@Config.LangKey("betterfoliage.heightMin")
		public double heightMin = 0.6D;
		
		@Config.Name("Height Max")
		@Config.RangeDouble(min = 0.1D, max = 2.5D)
		@Config.LangKey("betterfoliage.heightMax")
		public double heightMax = 0.8D;
		
		@Config.Name("Grass Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.shortGrass.grassPopulation")
		public int population = 64;
		
		@Config.Name("Shader Wind")
		@Config.LangKey("betterfoliage.shaderWind")
		public boolean shaderWind = true;
		
		@Config.Name("Use Generated")
		@Config.LangKey("betterfoliage.shortGrass.useGenerated")
		public boolean useGenerated = false;
		
		@Config.Name("Mycelium Enabled")
		@Config.LangKey("betterfoliage.shortGrass.myceliumEnabled")
		public boolean myceliumEnabled = true;
		
		@Config.Name("Mycelium Population")
		@Config.RangeInt(max = 64)
		@Config.LangKey("betterfoliage.shortGrass.myceliumPopulation")
		public int myceliumPopulation = 64;
		
		@Config.Name("Snow Enabled")
		@Config.LangKey("betterfoliage.shortGrass.snowEnabled")
		public boolean snowEnabled = true;
		
		@Config.Name("Saturation Threshold")
		@Config.LangKey("betterfoliage.shortGrass.saturationThreshold")
		public double saturationThreshold = 0.1D;
		
		@Config.Name("Longer Grass")
		@Config.LangKey("betterfoliage.shortGrass.longerGrass")
		public boolean longerGrass = true;
	}
	
	private static void filterBiomesByNameOrId(String[] entries, BitSet targetSet) {
		if(entries == null || entries.length == 0 || targetSet == null) return;
		
		ArrayList<String> byName = new ArrayList<>();
		for(String name : entries) {
			if(name == null) continue;
			name = name.trim();
			if(name.isEmpty()) continue;
			try {
				int id = Integer.parseInt(name);
				targetSet.set(id);
			}
			catch(Exception ex) {
				byName.add(name);
			}
		}
		
		for(String name : byName) {
			for(Biome biome : getBiomesByType(name)) {
				targetSet.set(Biome.getIdForBiome(biome));
			}
		}
	}
	
	private static Set<Biome> getBiomesByType(String name) {
		BiomeDictionary.Type type = BiomeDictionaryTypeAccessor.betterfoliage$getByName().get(name.toUpperCase());
		if(type == null) {
			BetterFoliage.LOGGER_DETAIL.log(Level.WARN, "Invalid Biome Type Name: {}", name);
			return new HashSet<>();
		}
		return BiomeDictionary.getBiomes(type);
	}
	
	private static String[] getBiomesFromMinTempRain(float minTemp, float minRain) {
		Set<Integer> biomeInts = new LinkedHashSet<>();
		
		Biome.REGISTRY.forEach(biome -> {
			if(minTemp <= biome.getDefaultTemperature()) biomeInts.add(Biome.getIdForBiome(biome));
			else if(minRain <= biome.getRainfall()) biomeInts.add(Biome.getIdForBiome(biome));
		});
		
		return biomeInts.stream().map(String::valueOf).toArray(String[]::new);
	}
	
	public static void refreshConfig() {
		ALGAE.refreshConfig();
		CORAL.refreshConfig();
		REED.refreshConfig();
		BLOCKS.refreshConfig();
	}
	
	@Mod.EventBusSubscriber(modid = BetterFoliage.MODID, value = Side.CLIENT)
	private static class EventHandler {
		
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(BetterFoliage.MODID)) {
				ConfigManager.sync(BetterFoliage.MODID, Config.Type.INSTANCE);
				refreshConfig();
			}
		}
		
		@SubscribeEvent(priority = EventPriority.LOWEST)
		public static void onConfigChangedLowest(ConfigChangedEvent.OnConfigChangedEvent event) {
			if(event.getModID().equals(BetterFoliage.MODID)) {
				FMLClientHandler.instance().refreshResources(VanillaResourceType.TEXTURES, VanillaResourceType.MODELS);
				if(event.isWorldRunning()) Minecraft.getMinecraft().renderGlobal.loadRenderers();
			}
		}
	}
}