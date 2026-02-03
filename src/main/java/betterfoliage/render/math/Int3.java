package betterfoliage.render.math;

import net.minecraft.util.EnumFacing;

public class Int3 {
	public static final Int3 ZERO = new Int3(0, 0, 0);
	
	public int x;
	public int y;
	public int z;
	
	public Int3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Int3(EnumFacing facing) {
		this(facing.getDirectionVec().getX(),
			 facing.getDirectionVec().getY(),
			 facing.getDirectionVec().getZ());
	}
	
	public Int3(Pair<Integer,EnumFacing> offset) {
		this(offset.r.getDirectionVec().getX() * offset.l,
			 offset.r.getDirectionVec().getY() * offset.l,
			 offset.r.getDirectionVec().getZ() * offset.l);
	}
	
	public Int3 plus(Int3 other) {
		return new Int3(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Int3 plus(Pair<Integer,EnumFacing> offset) {
		return new Int3(this.x + offset.r.getDirectionVec().getX() * offset.l,
						this.y + offset.r.getDirectionVec().getY() * offset.l,
						this.z + offset.r.getDirectionVec().getZ() * offset.l);
	}
	
	public Int3 unaryMinus() {
		return new Int3(-this.x, -this.y, -this.z);
	}
	
	public Int3 minus(Int3 other) {
		return new Int3(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Int3 times(int scale) {
		return new Int3(this.x * scale, this.y * scale, this.z * scale);
	}
	
	public Int3 times(Int3 other) {
		return new Int3(this.x * other.x, this.y * other.y, this.z * other.z);
	}
	
	public Int3 rotate(Rotation rot) {
		return new Int3(rot.rotatedComponent(EnumFacing.EAST, this.x, this.y, this.z),
						rot.rotatedComponent(EnumFacing.UP, this.x, this.y, this.z),
						rot.rotatedComponent(EnumFacing.SOUTH, this.x, this.y, this.z));
	}
	
	public Int3 setTo(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Int3 setTo(Int3 other) {
		return this.setTo(other.x, other.y, other.z);
	}
	
	public Int3 add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Int3 add(Int3 other) {
		return this.add(other.x, other.y, other.z);
	}
	
	public Int3 sub(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Int3 sub(Int3 other) {
		return this.sub(other.x, other.y, other.z);
	}
	
	public Int3 mul(int scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}
	
	public Int3 mul(Int3 other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
		return this;
	}
}