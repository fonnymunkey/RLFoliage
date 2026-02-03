package betterfoliage.render.math;

import betterfoliage.render.util.MathUtil;
import net.minecraft.util.EnumFacing;

public class Double3 {
	public double x;
	public double y;
	public double z;
	
	public Double3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Double3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Double3(EnumFacing facing) {
		this(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
	}
	
	public Double3 plus(Double3 other) {
		return new Double3(this.x + other.x, this.y + other.y, this.z + other.z);
	}
	
	public Double3 unaryMinus() {
		return new Double3(-this.x, -this.y, -this.z);
	}
	
	public Double3 minus(Double3 other) {
		return new Double3(this.x - other.x, this.y - other.y, this.z - other.z);
	}
	
	public Double3 times(Double scale) {
		return new Double3(this.x * scale, this.y * scale, this.z * scale);
	}
	
	public Double3 times(Double3 other) {
		return new Double3(this.x * other.x, this.y * other.y, this.z * other.z);
	}
	
	public Double3 rotate(Rotation rot) {
		return new Double3(rot.rotatedComponent(EnumFacing.EAST, this.x, this.y, this.z),
						   rot.rotatedComponent(EnumFacing.UP, this.x, this.y, this.z),
						   rot.rotatedComponent(EnumFacing.SOUTH, this.x, this.y, this.z));
	}
	
	public Double3 rotateMut(Rotation rot) {
		return setTo(rot.rotatedComponent(EnumFacing.EAST, this.x, this.y, this.z),
					 rot.rotatedComponent(EnumFacing.UP, this.x, this.y, this.z),
					 rot.rotatedComponent(EnumFacing.SOUTH, this.x, this.y, this.z));
	}
	
	public Double3 setTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Double3 setTo(Double3 other) {
		return this.setTo(other.x, other.y, other.z);
	}
	
	public Double3 add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Double3 add(Double3 other) {
		return this.add(other.x, other.y, other.z);
	}
	
	public Double3 sub(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Double3 sub(Double3 other) {
		return this.sub(other.x, other.y, other.z);
	}
	
	public Double3 mul(double scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}
	
	public Double3 mul(Double3 other) {
		this.x *= other.x;
		this.y *= other.y;
		this.z *= other.z;
		return this;
	}
	
	public double dot(Double3 other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}
	
	public Double3 cross(Double3 other) {
		double x = this.x;
		double y = this.y;
		double z = this.z;
		this.x = y * other.z - z * other.y;
		this.y = z * other.x - x * other.z;
		this.z = x * other.y - y * other.x;
		return this;
	}
	
	public Double3 normalize() {
		double inv = 1.0 / this.length();
		return this.mul(inv);
	}
	
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}
	
	public EnumFacing nearestCardinal() {
		return MathUtil.nearestAngle(this, MathUtil.FORGEDIRS_LIST, MathUtil::vec);
	}
}