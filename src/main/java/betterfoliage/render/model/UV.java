package betterfoliage.render.model;

public class UV {
	public static final UV TOP_LEFT = new UV(-0.5, -0.5);
	public static final UV TOP_RIGHT = new UV(0.5, -0.5);
	public static final UV BOTTOM_LEFT = new UV(-0.5, 0.5);
	public static final UV BOTTOM_RIGHT = new UV(0.5, 0.5);
	
	public final double u;
	public final double v;
	
	public UV(double u, double v) {
		this.u = u;
		this.v = v;
	}
	
	public UV rotate() {
		return new UV(this.v, -this.u);
	}
	
	public UV rotate(int n) {
		switch(n%4) {
			case 0: return this;
			case 1: return new UV(this.v, -this.u);
			case 2: return new UV(-this.u, -this.v);
			default: return new UV(-this.v, this.u);
		}
	}
	
	public UV clamp(double minU, double maxU, double minV, double maxV) {
		if(this.u >= minU && this.u <= maxU && this.v >= minV && this.v <= maxV) return this;
		return new UV(Math.min(Math.max(this.u, minU), maxU), Math.min(Math.max(this.v, minV), maxV));
	}
	
	public UV mirror(boolean mirrorU, boolean mirrorV) {
		if(!mirrorU && !mirrorV) return this;
		return new UV(mirrorU ? -this.u : this.u, mirrorV ? -this.v : this.v);
	}
}