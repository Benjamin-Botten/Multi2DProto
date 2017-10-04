package engine.io;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * 
 * @author robot
 *
 */
public abstract class Input implements KeyListener, MouseListener, MouseMotionListener {
	protected int mx, my;
	protected int mxDrag, myDrag;
	protected int mxTranslated, myTranslated;
	public boolean[] keys;
	protected boolean wasPressed, ml, mr;
	protected boolean mouseMoved;
	protected String typedChar = "";
	protected long timerTyped; //ms
	protected long typedDelay = 2000; //ms
	
	/**
	 * 
	 * @param canvas
	 */
	public Input(Canvas canvas) {
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addKeyListener(this);
		canvas.addMouseMotionListener(this);
		
		keys = new boolean[Short.MAX_VALUE * 2 + 1]; //Allocation size 65535 (sizeof(unsigned short))
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean mouseLeft() {
		return ml;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean mouseRight() {
		return mr;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMouseX() {
		return mx;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMouseY() {
		return my;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMouseXTranslated(int camX) {
		return mx + camX;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMouseYTranslated(int camY) {
		return my + camY;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMouseXDragged() {
		return mxDrag;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getMouseYDragged() {
		return myDrag;
	}
	
	
	/**
	 * 
	 * @param ke
	 * @return
	 */
	public boolean isDown(KeyEvent ke) {
		return keys[ke.getKeyCode()];
	}
	
	public String pollTypedChar() {
		String ret = typedChar;
		typedChar = "";
		return ret;
	}
}
