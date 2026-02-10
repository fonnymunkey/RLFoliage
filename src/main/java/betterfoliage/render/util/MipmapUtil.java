package betterfoliage.render.util;

/**
 * Mipmap pixel averaging fix for https://bugs-legacy.mojang.com/browse/MC-114265
 * Fixes mipmap creating black pixels on cutout textures and improves performance
 * Created by and all credit goes to jonathan2520 from the linked bug report thread
 * Relevant source code:
 * https://bugs-legacy.mojang.com/secure/attachment/134596/SRGBTable.java
 * https://bugs-legacy.mojang.com/secure/attachment/134595/SRGBCalculator.java
 * https://bugs-legacy.mojang.com/secure/attachment/134594/SRGBAverager.java
 */
public abstract class MipmapUtil {
	private static final SRGBTable srgb = new SRGBTable();
	
	public static int average(int c0, int c1, int c2, int c3) {
		if((((c0 | c1 | c2 | c3) ^ (c0 & c1 & c2 & c3)) & 0xff000000) == 0) {
			float r = srgb.decode(c0 & 0xff)
					+ srgb.decode(c1 & 0xff)
					+ srgb.decode(c2 & 0xff)
					+ srgb.decode(c3 & 0xff);
			float g = srgb.decode(c0 >> 8 & 0xff)
					+ srgb.decode(c1 >> 8 & 0xff)
					+ srgb.decode(c2 >> 8 & 0xff)
					+ srgb.decode(c3 >> 8 & 0xff);
			float b = srgb.decode(c0 >> 16 & 0xff)
					+ srgb.decode(c1 >> 16 & 0xff)
					+ srgb.decode(c2 >> 16 & 0xff)
					+ srgb.decode(c3 >> 16 & 0xff);
			
			return srgb.encode(0.25F * r)
					| srgb.encode(0.25F * g) << 8
					| srgb.encode(0.25F * b) << 16
					| c0 & 0xff000000;
		}
		else {
			float a0 = c0 >>> 24;
			float a1 = c1 >>> 24;
			float a2 = c2 >>> 24;
			float a3 = c3 >>> 24;
			
			float r = a0 * srgb.decode(c0 & 0xff)
					+ a1 * srgb.decode(c1 & 0xff)
					+ a2 * srgb.decode(c2 & 0xff)
					+ a3 * srgb.decode(c3 & 0xff);
			float g = a0 * srgb.decode(c0 >> 8 & 0xff)
					+ a1 * srgb.decode(c1 >> 8 & 0xff)
					+ a2 * srgb.decode(c2 >> 8 & 0xff)
					+ a3 * srgb.decode(c3 >> 8 & 0xff);
			float b = a0 * srgb.decode(c0 >> 16 & 0xff)
					+ a1 * srgb.decode(c1 >> 16 & 0xff)
					+ a2 * srgb.decode(c2 >> 16 & 0xff)
					+ a3 * srgb.decode(c3 >> 16 & 0xff);
			float a = a0 + a1 + a2 + a3;
			
			return srgb.encode(r / a)
					| srgb.encode(g / a) << 8
					| srgb.encode(b / a) << 16
					| (int)(0.25F * a + 0.5F) << 24;
		}
	}
	
	protected static class SRGBTable {
		private final float scale = 3295.5F;
		private final float[] to_float = new float[256];
		private final float[] threshold = new float[256];
		private final byte[] to_int = new byte[(int)scale + 1];
		
		protected SRGBTable() {
			SRGBCalculator calc = new SRGBCalculator();
			for(int i = 0; i < 255; ++i) {
				to_float[i] = (float)calc.decode(i / 255.0);
				double dthresh = calc.decode((i + 0.5) / 255.0);
				float fthresh = (float)dthresh;
				if(fthresh >= dthresh) fthresh = Math.nextAfter(fthresh, -1);
				threshold[i] = fthresh;
			}
			to_float[255] = 1;
			threshold[255] = Float.POSITIVE_INFINITY;
			int offset = 0;
			for(int i = 0; i < 255; ++i) {
				int up_to = (int)(threshold[i] * scale);
				build_to_int_table(offset, up_to, (byte)i);
				offset = up_to + 1;
			}
			build_to_int_table(offset, (int)scale, (byte)255);
		}
		
		private void build_to_int_table(int offset, int up_to, byte value) {
			if(offset > up_to) throw new IllegalArgumentException("scale is too small");
			while(offset <= up_to) {
				to_int[offset++] = value;
			}
		}
		
		protected float decode(int x) {
			return to_float[x];
		}
		
		protected int encode(float x) {
			int index = to_int[(int)(x * scale)] & 0xff;
			if(x > threshold[index]) ++index;
			return index;
		}
		
		protected static class SRGBCalculator {
			private final double decode_threshold;
			private final double decode_slope;
			private final double decode_multiplier;
			private final double decode_addend;
			private final double decode_exponent;
			
			protected SRGBCalculator() {
				double gamma = 2.4D;
				double alpha = 0.055;
				decode_multiplier = 1.0 / (alpha + 1.0);
				decode_addend = decode_multiplier * alpha;
				decode_exponent = gamma;
				decode_threshold = alpha / (gamma - 1.0);
				decode_slope = Math.pow(gamma * decode_threshold * decode_multiplier, gamma) / decode_threshold;
			}
			
			protected double decode(double x) {
				if(x < decode_threshold) return decode_slope * x;
				else return Math.pow(x * decode_multiplier + decode_addend, decode_exponent);
			}
		}
	}
}