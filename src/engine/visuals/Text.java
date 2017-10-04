package engine.visuals;

public class Text {
	public static final int ALIGNMENT_LEFT = 0;
	public static final int ALIGNMENT_CENTRE = 1;

	public static final int DEFAULT_SIZE = 1;
	public static final int DEFAULT_COLOR = 0xffffffff;
	public static final int DEFAULT_ALIGNMENT = ALIGNMENT_LEFT;
	
	public static final String charset = "" + //
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" + //
			"1234567890"; //

	
	
	private int x, y;
	private int size = DEFAULT_SIZE; //scale of actual text, not size of the text-string
	private int color = DEFAULT_COLOR;
	private int alignment = DEFAULT_ALIGNMENT;
	private String text;

	private boolean hasBackground;
	private int colorBackground;

	public Text(String text, int x, int y) {
		this.text = text;
		this.x = x;
		this.y = y;
	}
	
	public Text(String text, int x, int y, int size) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	public Text(String text, int x, int y, int size, int alignment, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.size = size;
		this.alignment = alignment;
		this.color = color;
	}
	
	/**
	 * 
	 * @param hasBackground
	 */
	public void setHasBackground(boolean hasBackground) {
		this.hasBackground = hasBackground;
	}
	
	/**
	 * 
	 * @param colorBackground
	 */
	public void setColorBackground(int colorBackground) {
		if(hasBackground) {
			this.colorBackground = colorBackground;
		}
	}
	
	/**
	 * adds a string character by character and handles special chars such as backspaces, returns, etc
	 * @param string
	 */
	public void add(String string) {
		if(string.equals("\b")) {
			if(text.length() > 0) {
				text = text.substring(0, text.length() - 1);
			}
		} else {
			text = text.concat(string);
		}
	}
	
	/** Getters */

	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getAlignment() {
		return alignment;
	}
	
	public String getText() {
		return text;
	}
	
	public int getColorBackground() {
		return colorBackground;
	}
	
	public int getWidth() {
		return text.length() * SpriteSheet.font.ew * size;
	}
	
	public int getHeight() {
		return SpriteSheet.font.eh * size;
	}
	
	
	public String toString() {
		return text;
	}
	
	public int length() {
		return text.length();
	}

	public boolean hasBackground() {
		return hasBackground;
	}
	
	/** Setters */
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setAlignment(int alignment) {
		if(alignment == ALIGNMENT_LEFT || alignment == ALIGNMENT_CENTRE) {
			this.alignment = alignment;
		}
	}
}
