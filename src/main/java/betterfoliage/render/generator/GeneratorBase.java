package betterfoliage.render.generator;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.InputStream;

public abstract class GeneratorBase {
	public final String domain;
	
	protected GeneratorBase(String domain) {
		this.domain = domain;
	}
	
	abstract boolean resourceExists(@Nullable ResourceLocation location);
	@Nullable
	abstract InputStream getInputStream(@Nullable ResourceLocation location);
}