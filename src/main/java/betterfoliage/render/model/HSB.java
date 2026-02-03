package betterfoliage.render.model;

import java.awt.*;

public class HSB {
	public float hue;
	public float saturation;
	public float brightness;
	
	public HSB(float hue, float saturation, float brightness) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
	}
	
	public static HSB fromColor(int color) {
		float[] hsbVals = Color.RGBtoHSB((color >> 16) & 255, (color >> 8) & 255, color & 255, null);
		return new HSB(hsbVals[0], hsbVals[1], hsbVals[2]);
	}
	
	public int asColor() {
		return Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
	}
}