package engine.visuals;

public class Color {
	
	public static final int YELLOW = 0xffffff00;
	public static final int WHITE = 0xffffffff;
	public static final int BLACK = 0xff000000;
	public static final int RED = 0xffff0000;
	public static final int GREEN = 0xff00ff00;
	public static final int BLUE = 0xff0000ff;
	
	public static int getA(int color) {
		return (color >> 24) & 0xff;
	}
	
	public static int getR(int color) {
		return (color >> 16) & 0xff;
	}
	
	public static int getG(int color) {
		return (color >> 8) & 0xff;
	}
	
	public static int getB(int color) {
		return color & 0xff;
	}
	
	public static int getColor(int a, int r, int g, int b) {
		if(a > 255) a = 255;
		if(a < 0) a = 0;
		
		if(r > 255) r = 255;
		if(r < 0) r = 0;
		
		if(g > 255) g = 255;
		if(g < 0) g = 0;
		
		if(b > 255) b = 255;
		if(b < 0) b = 0;
		
		return ((a << 24) | (r << 16) | (g << 8) | b);
	}
	
	public static int getColorMultipliedARGB(int color, float scalar) {
		int a = (int) (getA(color) * (float)scalar);
		int r = (int) (getR(color) * (float)scalar);
		int g = (int) (getG(color) * (float)scalar);
		int b = (int) (getB(color) * (float)scalar);
		
		return getColor(a, r, g, b);
	}

	public static int getColorMultipliedRGB(int color, float scalar) {
		int a = (int) (getA(color));
		int r = (int) (getR(color) * (float)scalar);
		int g = (int) (getG(color) * (float)scalar);
		int b = (int) (getB(color) * (float)scalar);
		
		return getColor(a, r, g, b);
	}
	
	public static int getColorAdditiveARGB(int color, float scalar) {
		int a = getA(color);
		int r = getR(color);
		int g = getG(color);
		int b = getB(color);
		
		a = a + (int) (a * (float) scalar);
		r = r + (int) (r * (float) scalar);
		g = g + (int) (g * (float) scalar);
		b = b + (int) (b * (float) scalar);
		
		return getColor(a, r, g, b);
	}

	public static int getColorAdditiveRGB(int color, float scalar) {
		int r = getR(color);
		int g = getG(color);
		int b = getB(color);
		
		r = r + (int) (r * (float) scalar);
		g = g + (int) (g * (float) scalar);
		b = b + (int) (b * (float) scalar);
		
		return getColor(getA(color), r, g, b);
	}
	
	/** Src is background pixel and color is the new one over it */
	public static int getColorBlendAlpha(int src, int color) {
		int aSrc = getA(src);
		int rSrc = getR(src);
		int gSrc = getG(src);
		int bSrc = getB(src);
		
		int a = (getA(color));
		int r = (getR(color));
		int g = (getG(color));
		int b = (getB(color));
		
		float da = 255 - ((255 - a) * 0.003921f);
		
		r = (int) (r * (float)da);
		g = (int) (g * (float)da);
		b = (int) (b * (float)da);
		
		r = (r + rSrc) >> 0;
		g = (g + gSrc) >> 0;
		b = (b + bSrc) >> 0;
		
		return getColor(a, r, g, b);
	}
		
}
