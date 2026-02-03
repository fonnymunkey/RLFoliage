package betterfoliage.render;

import betterfoliage.render.math.Double3;
import betterfoliage.render.math.Int3;
import betterfoliage.render.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;

import java.util.function.Predicate;

public class BlockContext {
	private IBlockAccess world = null;
	private BlockPos pos = BlockPos.ORIGIN;
	private IBlockState state;
	private Integer biomeId = null;
	private BlockData blockData = null;
	
	public BlockContext set(IBlockAccess world, BlockPos pos, IBlockState state) {
		this.world = world;
		this.pos = pos;
		this.state = state;
		this.biomeId = null;
		this.blockData = null;
		return this;
	}
	
	public IBlockAccess getWorld() {
		return this.world;
	}
	
	public BlockPos getPos() {
		return this.pos;
	}
	
	public BlockPos getPos(int x, int y, int z) {
		if(x == 0 && y == 0 && z == 0) return this.pos;
		return this.pos.add(x, y, z);
	}
	
	public BlockPos getPos(Int3 offset) {
		return this.getPos(offset.x, offset.y, offset.z);
	}
	
	public Double3 getCenter() {
		return new Double3(this.pos.getX(), this.pos.getY(), this.pos.getZ()).add(0.5, 0.5, 0.5);
	}
	
	public IBlockState getState() {
		return this.state;
	}
	
	public IBlockState getState(int x, int y, int z) {
		if(x == 0 && y == 0 && z == 0) return this.getState();
		return this.world.getBlockState(this.pos.add(x, y, z));
	}
	
	public IBlockState getState(Int3 offset) {
		return this.getState(offset.x, offset.y, offset.z);
	}
	
	public Block getBlock() {
		return this.state.getBlock();
	}
	
	public Block getBlock(int x, int y, int z) {
		if(x == 0 && y == 0 && z == 0) return this.getBlock();
		return this.getState(x, y, z).getBlock();
	}
	
	public Block getBlock(Int3 offset) {
		return this.getBlock(offset.x, offset.y, offset.z);
	}
	
	public BlockData getBlockData() {
		if(this.blockData == null) this.blockData = new BlockData(this.state, this.world, this.pos);
		return this.blockData;
	}
	
	public BlockData getBlockData(int x, int y, int z) {
		if(x == 0 && y == 0 && z == 0) return this.getBlockData();
		BlockPos pos = this.pos.add(x, y, z);
		IBlockState state = this.world.getBlockState(pos);
		return new BlockData(state, this.world, pos);
	}
	
	public BlockData getBlockData(Int3 offset) {
		return this.getBlockData(offset.x, offset.y, offset.z);
	}
	
	public BlockData getBlockData(IBlockState state, BlockPos pos) {
		if(pos.getX() == this.pos.getX() && pos.getY() == this.pos.getY() && pos.getZ() == this.pos.getZ()) return this.getBlockData();
		return new BlockData(state, this.world, pos);
	}
	
	public int getBiomeId() {
		if(this.biomeId == null) this.biomeId = Biome.getIdForBiome(this.world.getBiome(this.pos));
		return this.biomeId;
	}
	
	public boolean isSurroundedBy(Predicate<IBlockState> predicate) {
		for(Int3 offset : MathUtil.FORGEDIRS_OFFSETS) {
			if(!predicate.test(this.getState(offset))) return false;
		}
		return true;
	}
	
	public int getRandom(int seed) {
		return MathUtil.getSemiRandom(this.pos.getX(), this.pos.getY(), this.pos.getZ(), seed);
	}
	
	public int[] getSemiRandomArray(int size) {
		int[] array = new int[size];
		for(int i = 0; i < size; i++) {
			array[i] = this.getRandom(i);
		}
		return array;
	}
	
	public static class BlockData {
		public final IBlockState state;
		public final int color;
		public final int packedLight;
		
		public BlockData(IBlockState state, IBlockAccess world, BlockPos pos) {
			this.state = state;
			this.color = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, pos, 0);
			this.packedLight = state.getPackedLightmapCoords(world, pos);
		}
	}
}