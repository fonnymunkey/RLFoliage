package betterfoliage.render.shader;

import betterfoliage.render.util.MathUtil;
import betterfoliage.render.math.Rotation;
import betterfoliage.render.model.AoData;
import betterfoliage.render.RenderVertex;
import betterfoliage.render.ModelRenderer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class CornerTri implements Shader {
	private final EnumFacing face;
	private final EnumFacing dir1;
	private final EnumFacing dir2;
	private final BiFunction<AoData,AoData,AoData> func;
	
	public CornerTri(EnumFacing face, EnumFacing dir1, EnumFacing dir2, BiFunction<AoData,AoData,AoData> func) {
		this.face = face;
		this.dir1 = dir1;
		this.dir2 = dir2;
		this.func = func;
	}
	
	@Override
	public void shade(ModelRenderer context, RenderVertex vertex) {
		AoData acc = accumulate(context.aoShading(this.face, this.dir1, this.dir2), context.aoShading(this.dir1, this.face, this.dir2), this.func);
		acc = accumulate(acc, context.aoShading(this.dir2, this.face, this.dir1), this.func);
		vertex.shade(acc != null ? acc : AoData.BLACK);
	}
	
	@Override
	public Shader rotate(Rotation rot) {
		return new CornerTri(MathUtil.rotate(this.face, rot), MathUtil.rotate(this.dir1, rot), MathUtil.rotate(this.dir2, rot), this.func);
	}
	
	@Nullable
	private static AoData accumulate(@Nullable AoData v1, @Nullable AoData v2, BiFunction<AoData,AoData,AoData> func) {
		boolean v1ok = v1 != null && v1.valid;
		boolean v2ok = v2 != null && v2.valid;
		if(v1ok && v2ok) return func.apply(v1, v2);
		if(v1ok) return v1;
		if(v2ok) return v2;
		return null;
	}
}