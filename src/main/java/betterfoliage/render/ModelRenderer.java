package betterfoliage.render;

import betterfoliage.compat.OptifineCompatWrapper;
import betterfoliage.render.math.*;
import betterfoliage.render.model.*;
import betterfoliage.render.shader.Shader;
import betterfoliage.render.util.MathUtil;
import betterfoliage.render.util.ShaderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelRenderer {
	public static final ThreadLocal<ModelRenderer> MODEL_RENDERER = ThreadLocal.withInitial(ModelRenderer::new);
	public final BlockContext BLOCK_CONTEXT;
	public final RenderVertex RENDER_VERTEX;
	
	private final AoFaceData[] aoFaces;
	public Rotation rotation;
	public boolean aoEnabled;
	
	private ModelRenderer() {
		this.BLOCK_CONTEXT = new BlockContext();
		this.RENDER_VERTEX = new RenderVertex();
		this.aoFaces = new AoFaceData[6];
		for(int i = 0; i < 6; i++) {
			this.aoFaces[i] = new AoFaceData(MathUtil.FORGEDIRS[i]);
		}
		this.rotation = MathUtil.IDENTITY;
		this.aoEnabled = Minecraft.isAmbientOcclusionEnabled();
	}
	
	public void render(BufferBuilder worldRenderer, Model model, Rotation rot, Double3 trans, boolean forceFlat, ShaderUtil.QuadFilter quadFilter, ShaderUtil.QuadIconResolver icon, ShaderUtil.PostProcessLambda postProcess) {
		this.rotation = rot;
		this.aoEnabled = Minecraft.isAmbientOcclusionEnabled();
		
		ensureSpaceForQuads(worldRenderer, model.quads.size() + 1);
		
		for(int i = 0; i < model.quads.size(); i++) {
			Quad quad = model.quads.get(i);
			if(quadFilter.filter(i, quad)) {
				TextureAtlasSprite drawIcon = icon.resolve(this, i, quad);
				if(drawIcon != null) {
					OptifineCompatWrapper.setQuadSprite(worldRenderer, drawIcon);
					
					for(int j = 0; j < 4; j++) {
						Vertex vert = quad.getVert(j);
						this.RENDER_VERTEX.init(vert).rotate(this.rotation).translate(trans);
						Shader shader = (this.aoEnabled && !forceFlat) ? vert.aoShader : vert.flatShader;
						shader.shade(this, this.RENDER_VERTEX);
						postProcess.process(this.RENDER_VERTEX, this, i, quad, j, vert);
						this.RENDER_VERTEX.setIcon(drawIcon);
						
						worldRenderer.pos(this.RENDER_VERTEX.x, this.RENDER_VERTEX.y, this.RENDER_VERTEX.z)
									 .color(this.RENDER_VERTEX.red, this.RENDER_VERTEX.green, this.RENDER_VERTEX.blue, 1.0F)
									 .tex(this.RENDER_VERTEX.u, this.RENDER_VERTEX.v)
									 .lightmap(this.RENDER_VERTEX.brightness >> 16 & 65535, this.RENDER_VERTEX.brightness & 65535)
									 .endVertex();
					}
				}
			}
		}
	}
	
	public void updateShading() {
		if(!this.aoEnabled) return;
		for(EnumFacing facing : MathUtil.FORGEDIRS) {
			this.aoFaces[facing.ordinal()].update(OptifineCompatWrapper.getDiffusedMult(facing));
		}
	}
	
	public void updateShading(EnumFacing facing) {
		if(!this.aoEnabled) return;
		this.aoFaces[facing.ordinal()].update(OptifineCompatWrapper.getDiffusedMult(facing));
	}
	
	public void updateShading(boolean[] faces) {
		if(!this.aoEnabled) return;
		for(EnumFacing facing : MathUtil.FORGEDIRS) {
			if(faces[facing.ordinal()]) {
				this.aoFaces[facing.ordinal()].update(OptifineCompatWrapper.getDiffusedMult(facing));
			}
		}
	}
	
	public AoData aoShading(EnumFacing face, EnumFacing corner1, EnumFacing corner2) {
		return this.aoFaces[MathUtil.rotate(face, this.rotation).ordinal()].get(MathUtil.rotate(corner1, this.rotation), MathUtil.rotate(corner2, this.rotation));
	}
	
	public BlockContext.BlockData blockData(Int3 offset) {
		return this.BLOCK_CONTEXT.getBlockData(offset.rotate(this.rotation));
	}
	
	private static void ensureSpaceForQuads(BufferBuilder worldRenderer, int num) {
		worldRenderer.rawIntBuffer.position(worldRenderer.getBufferSize());
		worldRenderer.growBuffer(num * worldRenderer.getVertexFormat().getSize());
	}
}