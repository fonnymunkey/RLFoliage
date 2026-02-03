package betterfoliage.render.generator;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.InputStream;

public abstract class ParameterBasedGenerator extends GeneratorBase {
	
	protected ParameterBasedGenerator(String domain) {
		super(domain);
	}
	
	abstract boolean resourceExists(ParameterList params);
	@Nullable
	abstract InputStream getInputStream(ParameterList params);
	
	@Override
	public boolean resourceExists(@Nullable ResourceLocation location) {
		String input = location == null ? "" : location.getPath();
		return this.resourceExists(ParameterList.fromString(input));
	}
	
	@Override
	public InputStream getInputStream(@Nullable ResourceLocation location) {
		String input = location == null ? "" : location.getPath();
		return this.getInputStream(ParameterList.fromString(input));
	}
}