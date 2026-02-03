package betterfoliage.config;

import betterfoliage.BetterFoliage;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public abstract class BlockMatcherRegistry {
	private static final List<BlockMatcher> allMatchers = new ArrayList<>();
	
	public static BlockMatcher getBlockMatcher(Supplier<String[]> blacklistSupplier, Supplier<String[]> whitelistSupplier) {
		BlockMatcher newMatcher = new BlockMatcher(blacklistSupplier, whitelistSupplier);
		allMatchers.add(newMatcher);
		return newMatcher;
	}
	
	public static void refreshMatchers() {
		BetterFoliage.LOGGER_DETAIL.log(Level.INFO, "Beginning BlockMatchers refresh");
		for(BlockMatcher matcher : allMatchers) {
			matcher.refresh();
		}
		BetterFoliage.LOGGER_DETAIL.log(Level.INFO, "Finished BlockMatchers refresh");
	}
	
	private static void parseClassesFromStrings(String[] names, List<Class<?>> targetList) {
		Set<Class<?>> clazzes = new HashSet<>();
		for(String name : names) {
			if(name == null) continue;
			name = name.trim();
			if(name.isEmpty()) continue;
			try {
				clazzes.add(Class.forName(name));
			}
			catch(Exception ex) {
				BetterFoliage.LOGGER_DETAIL.log(Level.WARN, "Invalid Block Class Name: {}", name);
			}
		}
		targetList.addAll(clazzes);
	}
	
	public static class BlockMatcher {
		private final Supplier<String[]> blacklistSupplier;
		private final Supplier<String[]> whitelistSupplier;
		
		private final List<Class<?>> classesBlacklistParsed = new ArrayList<>();
		private final List<Class<?>> classesWhitelistParsed = new ArrayList<>();
		
		private final HashMap<Block,Boolean> cachedMatches = new HashMap<>();
		
		protected BlockMatcher(Supplier<String[]> blacklistSupplier, Supplier<String[]> whitelistSupplier) {
			this.blacklistSupplier = blacklistSupplier;
			this.whitelistSupplier = whitelistSupplier;
		}
		
		protected void refresh() {
			this.classesBlacklistParsed.clear();
			this.classesWhitelistParsed.clear();
			this.cachedMatches.clear();
			
			parseClassesFromStrings(this.blacklistSupplier.get(), this.classesBlacklistParsed);
			parseClassesFromStrings(this.whitelistSupplier.get(), this.classesWhitelistParsed);
			
			for(Block block : ForgeRegistries.BLOCKS) {
				this.cachedMatches.put(block, this.matchingClassName(block) != null);
			}
		}
		
		public boolean matchesClass(Block block) {
			if(block == null) return false;
			Boolean matches = this.cachedMatches.get(block);
			if(matches == null) return this.matchingClassName(block) != null;
			return matches;
		}
		
		@Nullable
		public String matchingClassName(Block block) {
			Class<?> blockClazz = block.getClass();
			for(Class<?> clazz : this.classesBlacklistParsed) {
				if(clazz.isAssignableFrom(blockClazz)) return null;
			}
			for(Class<?> clazz : this.classesWhitelistParsed) {
				if(clazz.isAssignableFrom(blockClazz)) return clazz.getName();
			}
			return null;
		}
	}
}