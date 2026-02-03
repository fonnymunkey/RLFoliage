package betterfoliage.mixin.forestry;

import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.arboriculture.genetics.TreeDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TreeDefinition.class)
public interface TreeDefinitionAccessor {
	@Accessor(value = "species", remap = false)
	IAlleleTreeSpecies betterfoliage$getTreeSpecies();
}