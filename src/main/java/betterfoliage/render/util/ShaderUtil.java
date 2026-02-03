package betterfoliage.render.util;

import betterfoliage.render.ModelRenderer;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.math.Pair;
import betterfoliage.render.model.Quad;
import betterfoliage.render.model.Vertex;
import betterfoliage.render.model.AoData;
import betterfoliage.render.shader.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public abstract class ShaderUtil {
	@FunctionalInterface
	public interface ShaderFactory {
		Shader create(Quad quad, Vertex vertex);
	}
	@FunctionalInterface
	public interface CornerShaderFactory {
		Shader create(EnumFacing face, EnumFacing dir1, EnumFacing dir2);
	}
	@FunctionalInterface
	public interface QuadFilter {
		boolean filter(int i, Quad quad);
	}
	@FunctionalInterface
	public interface QuadIconResolver {
		@Nullable
		TextureAtlasSprite resolve(ModelRenderer renderer, int i, Quad quad);
	}
	@FunctionalInterface
	public interface PostProcessLambda {
		void process(RenderVertex renderVertex, ModelRenderer renderer, int i, Quad quad, int j, Vertex vertex);
	}
	
	public static final Shader NO_SHADER = new NoShader();
	public static final CornerShaderFactory CORNER_FLAT = (face, dir1, dir2) -> new FaceFlat(face);
	public static final CornerShaderFactory CORNER_AO_MAX_GREEN = cornerAoTri((s1, s2) -> s1.green > s2.green ? s1 : s2);
	public static final QuadFilter FILTER_TRUE = (a, b) -> true;
	public static final PostProcessLambda NO_POST = (a,b,c,d,e,f) -> {};
	public static final PostProcessLambda WHITE_WASH = (a,b,c,d,e,f) -> a.setGrey(1.4F);
	public static final PostProcessLambda GREY_WASH = (a,b,c,d,e,f) -> a.setGrey(1.0F);
	
	public static ShaderFactory faceOrientedAuto(@Nullable EnumFacing overrideFace, CornerShaderFactory corner) {
		return (quad, vertex) -> {
			EnumFacing quadFace =
					overrideFace == null ?
					quad.normal().nearestCardinal() :
					overrideFace;
			Pair<EnumFacing,EnumFacing> nearestCorner =
					MathUtil.nearestPosition(
							vertex.xyz,
							MathUtil.FACE_CORNERS.get(quadFace.ordinal()).asList(),
							n -> MathUtil.vec(quadFace).add(MathUtil.vec(n.l)).add(MathUtil.vec(n.r)).mul(0.5));
			return corner.create(quadFace, nearestCorner.l, nearestCorner.r);
		};
	}
	
	public static ShaderFactory edgeOrientedAuto(@Nullable Pair<EnumFacing,EnumFacing> overrideEdge, CornerShaderFactory corner) {
		return (quad, vertex) -> {
			Pair<EnumFacing,EnumFacing> edgeDir =
					overrideEdge == null ?
					MathUtil.nearestAngle(
							quad.normal(),
							MathUtil.BOX_EDGES,
							n -> MathUtil.vec(n.l).add(MathUtil.vec(n.r))) :
					overrideEdge;
			List<EnumFacing> edgeList = Arrays.asList(edgeDir.l, edgeDir.r);
			EnumFacing nearestFace = MathUtil.nearestPosition(vertex.xyz, edgeList, MathUtil::vec);
			Pair<EnumFacing,EnumFacing> nearestCorner = MathUtil.nearestPosition(
					vertex.xyz,
					MathUtil.FACE_CORNERS.get(nearestFace.ordinal()).asList(),
					n -> MathUtil.vec(nearestFace).add(MathUtil.vec(n.l).add(MathUtil.vec(n.r).mul(0.5))));
			return corner.create(nearestFace, nearestCorner.l, nearestCorner.r);
		};
	}
	
	public static CornerShaderFactory cornerAo(EnumFacing.Axis fallbackAxis) {
		return (face, dir1, dir2) -> {
			EnumFacing fallbackDir = face.getAxis() == fallbackAxis ? face :
									 dir1.getAxis() == fallbackAxis ? dir1 :
									 dir2.getAxis() == fallbackAxis ? dir2 :
									 null;
			if(fallbackDir == null) throw new IllegalStateException("Failed to find matching fallbackDir");
			return new CornerSingleFallback(face, dir1, dir2, fallbackDir);
		};
	}
	
	public static CornerShaderFactory cornerAoTri(BiFunction<AoData,AoData,AoData> func) {
		return (face, dir1, dir2) -> new CornerTri(face, dir1, dir2, func);
	}
}