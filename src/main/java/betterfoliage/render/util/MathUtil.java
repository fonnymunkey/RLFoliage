package betterfoliage.render.util;

import betterfoliage.render.math.*;
import betterfoliage.render.math.FaceCorners;
import betterfoliage.render.math.Rotation;
import net.minecraft.util.EnumFacing;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MathUtil {
	public static final EnumFacing[] FORGEDIRS = EnumFacing.values();
	public static final List<EnumFacing> FORGEDIRS_LIST = Arrays.asList(FORGEDIRS);
	public static final EnumFacing[] FORGEDIRS_HORIZONTAL = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };
	public static final Int3[] FORGEDIRS_OFFSETS = new Int3[6];
	public static final List<Pair<EnumFacing,EnumFacing>> BOX_EDGES = FORGEDIRS_LIST.stream().flatMap(
			face1 -> FORGEDIRS_LIST.stream().filter(
					face2 -> face2.getAxis().ordinal() > face1.getAxis().ordinal())
								   .map(face2 -> new Pair<>(face1, face2))).collect(Collectors.toList());
	public static final List<FaceCorners> FACE_CORNERS = FORGEDIRS_LIST.stream().map(facing -> {
		switch(facing) {
			case DOWN: return new FaceCorners(EnumFacing.SOUTH, EnumFacing.WEST);
			case UP: return new FaceCorners(EnumFacing.SOUTH, EnumFacing.EAST);
			case NORTH: return new FaceCorners(EnumFacing.WEST, EnumFacing.UP);
			case SOUTH: return new FaceCorners(EnumFacing.UP, EnumFacing.WEST);
			case WEST: return new FaceCorners(EnumFacing.SOUTH, EnumFacing.UP);
			case EAST: return new FaceCorners(EnumFacing.SOUTH, EnumFacing.DOWN);
			default: throw new IllegalStateException("Failed to generate FACE_CORNERS");
		}}).collect(Collectors.toList());
	public static final Rotation IDENTITY = new Rotation(FORGEDIRS, FORGEDIRS);
	public static final Rotation[] ROT90 = new Rotation[6];
	public static final Rotation[] ROTATION_FROM_UP;
	
	public static final int[][] ROTATION_MATRIX = new int[][] {
			{ 0, 1, 4, 5, 3, 2, 6 },
			{ 0, 1, 5, 4, 2, 3, 6 },
			{ 5, 4, 2, 3, 0, 1, 6 },
			{ 4, 5, 2, 3, 1, 0, 6 },
			{ 2, 3, 1, 0, 4, 5, 6 },
			{ 3, 2, 0, 1, 4, 5, 6 }};
	
	static {
		for(int i = 0; i < 6; i++) {
			FORGEDIRS_OFFSETS[i] = new Int3(FORGEDIRS[i]);
			ROT90[i] = new Rotation(getRotations(FORGEDIRS[i].getOpposite()), getRotations(FORGEDIRS[i]));
		}
		ROTATION_FROM_UP = new Rotation[] {
				ROT90[EnumFacing.EAST.ordinal()].times(2),
				IDENTITY,
				ROT90[EnumFacing.WEST.ordinal()],
				ROT90[EnumFacing.EAST.ordinal()],
				ROT90[EnumFacing.SOUTH.ordinal()],
				ROT90[EnumFacing.NORTH.ordinal()] };
	}
	
	public static EnumFacing[] getRotations(EnumFacing facing) {
		EnumFacing[] rotations = new EnumFacing[6];
		for(int i = 0; i < 6; i++) {
			rotations[i] = EnumFacing.values()[ROTATION_MATRIX[facing.ordinal()][i]];
		}
		return rotations;
	}
	
	public static EnumFacing rotate(EnumFacing facing, Rotation rot) {
		return rot.forward[facing.ordinal()];
	}
	
	public static EnumFacing face(Pair<EnumFacing.Axis,EnumFacing.AxisDirection> pair) {
		return face(pair.l, pair.r);
	}
	
	public static EnumFacing face(EnumFacing.Axis l, EnumFacing.AxisDirection r) {
		if(l == EnumFacing.Axis.X) return r == EnumFacing.AxisDirection.POSITIVE ? EnumFacing.EAST : EnumFacing.WEST;
		if(l == EnumFacing.Axis.Y) return r == EnumFacing.AxisDirection.POSITIVE ? EnumFacing.UP : EnumFacing.DOWN;
		if(l == EnumFacing.Axis.Z && r == EnumFacing.AxisDirection.POSITIVE) return EnumFacing.SOUTH;
		return EnumFacing.NORTH;
	}
	
	public static float aoMultiplier(EnumFacing facing) {
		switch(facing) {
			case UP : return 1.0F;
			case DOWN : return 0.5F;
			case NORTH :
			case SOUTH :
				return 0.8F;
			default : return 0.6F;
		}
	}
	
	public static Int3 offset(EnumFacing facing) {
		return FORGEDIRS_OFFSETS[facing.ordinal()];
	}
	
	public static Rotation rot(EnumFacing facing) {
		return ROT90[facing.ordinal()];
	}
	
	public static Double3 times(EnumFacing facing, double scale) {
		return new Double3((double)facing.getDirectionVec().getX() * scale, (double)facing.getDirectionVec().getY() * scale, (double)facing.getDirectionVec().getZ() * scale);
	}
	
	public static Double3 vec(EnumFacing facing) {
		return new Double3(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ());
	}
	
	public static Double3 weight(Double3 v1, Double weight1, Double3 v2, Double weight2) {
		return new Double3(v1.x * weight1 + v2.x * weight2,
						   v1.y * weight1 + v2.y * weight2,
						   v1.z * weight1 + v2.z * weight2);
	}
	
	public static <T> T nearestPosition(Double3 vertex, Iterable<T> objs, Function<T,Double3> objPos) {
		T minObj = null;
		double minLength = Double.POSITIVE_INFINITY;
		for(T obj : objs) {
			double length = objPos.apply(obj).sub(vertex).length();
			if(length < minLength) {
				minLength = length;
				minObj = obj;
			}
		}
		if(minObj == null) throw new IllegalStateException("Failed to find nearest position");
		return minObj;
	}
	
	public static <T> T nearestAngle(Double3 vector, Iterable<T> objs, Function<T,Double3> objAngle) {
		T maxObj = null;
		double maxDot = Double.NEGATIVE_INFINITY;
		for(T obj : objs) {
			double dot = objAngle.apply(obj).dot(vector);
			if(dot > maxDot) {
				maxDot = dot;
				maxObj = obj;
			}
		}
		if(maxObj == null) throw new IllegalStateException("Failed to find nearest angle");
		return maxObj;
	}
	
	public static double random(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public static Double3 xzDisk(int i) {
		double j = 2.0D * Math.PI * (double)i / 64.0D;
		return new Double3(Math.cos(j), 0.0, Math.sin(j));
	}
	
	public static int getSemiRandom(int x, int y, int z, int seed) {
		int value = (x * x + y * y + z * z + x * y + y * z + z * x + (seed * seed)) & 63;
		return (3 * x * value + 5 * y * value + 7 * z * value + (11 * seed)) & 63;
	}
}