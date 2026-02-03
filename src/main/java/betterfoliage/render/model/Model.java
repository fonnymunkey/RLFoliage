package betterfoliage.render.model;

import betterfoliage.render.math.Double3;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Model {
	public static final Model FULLCUBE = new Model();
	public static final Model FULLCUBE_OVERLAY = new Model();
	
	static {
		for(EnumFacing face : MathUtil.FORGEDIRS) {
			FULLCUBE.add(faceQuad(face)
								 .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(face.getAxis())))
								 .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT)));
			FULLCUBE_OVERLAY.add(faceQuadOverlay(face)
								 .setAoShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.cornerAo(face.getAxis())))
								 .setFlatShader(ShaderUtil.faceOrientedAuto(null, ShaderUtil.CORNER_FLAT)));
		}
	}
	
	public final List<Quad> quads = new ArrayList<>();
	
	public Model() { }
	
	public Model(List<Quad> other) {
		this.quads.addAll(other);
	}
	
	public void add(Quad quad) {
		this.quads.add(quad);
	}
	
	public void addAll(Iterable<Quad> other) {
		for(Quad quad : other) this.quads.add(quad);
	}
	
	public void transformQ(UnaryOperator<Quad> trans) {
		this.quads.replaceAll(trans);
	}
	
	public void transformV(UnaryOperator<Vertex> trans) {
		this.quads.replaceAll(n -> n.transformV(trans));
	}
	
	public static Quad verticalRectangle(double x1, double z1, double x2, double z2, double yBottom, double yTop) {
		return new Quad(new Vertex(new Double3(x1, yBottom, z1), UV.BOTTOM_LEFT),
						new Vertex(new Double3(x2, yBottom, z2), UV.BOTTOM_RIGHT),
						new Vertex(new Double3(x2, yTop, z2), UV.TOP_RIGHT),
						new Vertex(new Double3(x1, yTop, z1), UV.TOP_LEFT));
	}
	
	public static Quad horizontalRectangle(double x1, double z1, double x2, double z2, double y) {
		double xMin = Math.min(x1, x2);
		double xMax = Math.max(x1, x2);
		double zMin = Math.min(z1, z2);
		double zMax = Math.max(z1, z2);
		return new Quad(new Vertex(new Double3(xMin, y, zMin), UV.TOP_LEFT),
						new Vertex(new Double3(xMin, y, zMax), UV.BOTTOM_LEFT),
						new Vertex(new Double3(xMax, y, zMax), UV.BOTTOM_RIGHT),
						new Vertex(new Double3(xMax, y, zMin), UV.TOP_RIGHT));
	}
	
	public static Quad faceQuad(EnumFacing face) {
		Double3 base = MathUtil.vec(face).mul(0.5);
		Double3 top = MathUtil.vec(MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.l).mul(0.5);
		Double3 left = MathUtil.vec(MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.r).mul(0.5);
		return new Quad(new Vertex(base.plus(top).add(left), UV.TOP_LEFT),
						new Vertex(base.minus(top).add(left), UV.BOTTOM_LEFT),
						new Vertex(base.minus(top).sub(left), UV.BOTTOM_RIGHT),
						new Vertex(base.plus(top).sub(left), UV.TOP_RIGHT));
	}
	
	//Not perfect but better to retain shade accuracy with minimal z fighting
	public static Quad faceQuadOverlay(EnumFacing face) {
		Double3 base = MathUtil.vec(face).mul(0.5005);
		Double3 top = MathUtil.vec(MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.l).mul(0.5005);
		Double3 left = MathUtil.vec(MathUtil.FACE_CORNERS.get(face.ordinal()).topLeft.r).mul(0.5005);
		return new Quad(new Vertex(base.plus(top).add(left), UV.TOP_LEFT),
						new Vertex(base.minus(top).add(left), UV.BOTTOM_LEFT),
						new Vertex(base.minus(top).sub(left), UV.BOTTOM_RIGHT),
						new Vertex(base.plus(top).sub(left), UV.TOP_RIGHT));
	}
}