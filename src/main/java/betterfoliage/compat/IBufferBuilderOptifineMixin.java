package betterfoliage.compat;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.shaders.SVertexBuilder;

public interface IBufferBuilderOptifineMixin {
	SVertexBuilder betterfoliage$getSVertexBuilder();
	void betterfoliage$setQuadSprite(TextureAtlasSprite sprite);
}