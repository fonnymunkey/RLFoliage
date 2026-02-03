package betterfoliage.mixin.forestry;

import forestry.api.arboriculture.EnumLeafType;
import forestry.arboriculture.models.TextureLeaves;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TextureLeaves.class)
public interface TextureLeavesAccessor {
	@Accessor(value = "leafTextures", remap = false)
	static Map<EnumLeafType, TextureLeaves> betterfoliage$getLeafTextures() {
		throw new AssertionError();
	}
	@Accessor(value = "plain", remap = false)
	ResourceLocation betterfoliage$getPlain();
	@Accessor(value = "fancy", remap = false)
	ResourceLocation betterfoliage$getFancy();
	@Accessor(value = "pollinatedPlain", remap = false)
	ResourceLocation betterfoliage$getPollinatedPlain();
	@Accessor(value = "pollinatedFancy", remap = false)
	ResourceLocation betterfoliage$getPollinatedFancy();
}