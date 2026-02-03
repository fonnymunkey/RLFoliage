package betterfoliage.mixin;

import betterfoliage.compat.IGameSettingsOptifineMixin;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameSettings.class)
public abstract class GameSettingsOptifineMixin implements IGameSettingsOptifineMixin {
	
	//Merge into field added by OF
	boolean ofCustomColors;
	
	@Override
	public boolean betterfoliage$getOFCustomColors() {
		return this.ofCustomColors;
	}
}