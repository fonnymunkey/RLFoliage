package betterfoliage.render.model;

public class AoData {
	public static final AoData BLACK = new AoData();
	
	public boolean valid = false;
	public int brightness = 0;
	public float red = 0.0F;
	public float green = 0.0F;
	public float blue = 0.0F;
	
	public void reset() {
		this.valid = false;
	}
	
	public void set(int brightness, float red, float green, float blue) {
		if(this.valid) return;
		this.valid = true;
		this.brightness = brightness;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	//TODO why not also check valid?
	public void set(int brightness, float colorMultiplier) {
		this.valid = true;
		this.brightness = brightness;
		this.red = colorMultiplier;
		this.green = colorMultiplier;
		this.blue = colorMultiplier;
	}
}