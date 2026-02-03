package betterfoliage.render.registry;

import net.minecraft.client.renderer.texture.TextureMap;

public interface ModelRenderKey<T> {
	default void onPreStitch(TextureMap atlas) { }
	T resolveSprites(TextureMap atlas);
}