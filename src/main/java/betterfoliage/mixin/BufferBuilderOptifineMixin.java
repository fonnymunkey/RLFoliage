package betterfoliage.mixin;

import betterfoliage.compat.IBufferBuilderOptifineMixin;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.shaders.SVertexBuilder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderOptifineMixin implements IBufferBuilderOptifineMixin {
	
	//Merge into fields added by OF
	SVertexBuilder sVertexBuilder;
	TextureAtlasSprite quadSprite;
	
	@Override
	public SVertexBuilder betterfoliage$getSVertexBuilder() {
		return this.sVertexBuilder;
	}
	
	@Override
	public void betterfoliage$setQuadSprite(TextureAtlasSprite sprite) {
		this.quadSprite = sprite;
	}
}