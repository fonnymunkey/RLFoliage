package betterfoliage.render.math;

import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceCorners {
	public final Pair<EnumFacing, EnumFacing> topLeft;
	public final Pair<EnumFacing, EnumFacing> topRight;
	public final Pair<EnumFacing, EnumFacing> bottomLeft;
	public final Pair<EnumFacing, EnumFacing> bottomRight;
	
	public FaceCorners(EnumFacing top, EnumFacing left) {
		this.topLeft = new Pair<>(top, left);
		this.topRight = new Pair<>(top, left.getOpposite());
		this.bottomLeft = new Pair<>(top.getOpposite(), left);
		this.bottomRight = new Pair<>(top.getOpposite(), left.getOpposite());
	}
	
	@SuppressWarnings("unchecked")
	public Pair<EnumFacing,EnumFacing>[] asArray() {
		return (Pair<EnumFacing, EnumFacing>[])new Pair[] { topLeft, topRight, bottomLeft, bottomRight };
	}
	
	public List<Pair<EnumFacing,EnumFacing>> asList() {
		return new ArrayList<>(Arrays.asList(topLeft, topRight, bottomLeft, bottomRight));
	}
}