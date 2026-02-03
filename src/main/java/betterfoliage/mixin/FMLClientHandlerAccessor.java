package betterfoliage.mixin;

import net.minecraft.client.resources.IResourcePack;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(FMLClientHandler.class)
public interface FMLClientHandlerAccessor {
	
	@Accessor(value = "resourcePackList", remap = false)
	List<IResourcePack> betterfoliage$getResourcePackList();
}