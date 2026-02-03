package betterfoliage.mixin;

import net.minecraftforge.common.BiomeDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BiomeDictionary.Type.class)
public interface BiomeDictionaryTypeAccessor {
	
	@Accessor(value = "byName", remap = false)
	static Map<String, BiomeDictionary.Type> betterfoliage$getByName() {
		throw new AssertionError();
	}
}