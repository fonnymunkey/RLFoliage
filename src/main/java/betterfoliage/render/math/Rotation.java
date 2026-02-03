package betterfoliage.render.math;

import betterfoliage.render.util.MathUtil;
import net.minecraft.util.EnumFacing;

public class Rotation {
	public final EnumFacing[] forward;
	public final EnumFacing[] reverse;
	
	public Rotation(EnumFacing[] forward, EnumFacing[] reverse) {
		this.forward = forward;
		this.reverse = reverse;
	}
	
	public Rotation plus(Rotation other) {
		EnumFacing[] forwardNew = new EnumFacing[6];
		EnumFacing[] reverseNew = new EnumFacing[6];
		for(int i = 0; i < 6; i++) {
			forwardNew[i] = this.forward[other.forward[i].ordinal()];
			reverseNew[i] = other.reverse[this.reverse[i].ordinal()];
		}
		return new Rotation(forwardNew, reverseNew);
	}
	
	public Rotation unaryMinus() {
		return new Rotation(this.reverse, this.forward);
	}
	
	public Rotation times(int n) {
		switch(n%4) {
			case 1: return this;
			case 2: return this.plus(this);
			case 3: return this.unaryMinus();
			default: return MathUtil.IDENTITY;
		}
	}
	
	public int rotatedComponent(EnumFacing dir, int x, int y, int z) {
		switch(this.reverse[dir.ordinal()]) {
			case EAST: return x;
			case WEST: return -x;
			case UP: return y;
			case DOWN: return -y;
			case SOUTH: return z;
			case NORTH: return -z;
			default: return 0;
		}
	}
	
	public double rotatedComponent(EnumFacing dir, double x, double y, double z) {
		switch(this.reverse[dir.ordinal()]) {
			case EAST: return x;
			case WEST: return -x;
			case UP: return y;
			case DOWN: return -y;
			case SOUTH: return z;
			case NORTH: return -z;
			default: return 0.0;
		}
	}
}