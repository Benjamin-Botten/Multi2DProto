package engine.io;

import java.awt.Canvas;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

public abstract class Input implements KeyListener, MouseListener {
	public int mx, my;
	public boolean[] keys;
	public boolean wasPressed, ml, mr;
	
	public Input(Canvas canvas) {
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addKeyListener(this);
		
		keys = new boolean[Short.MAX_VALUE * 2 + 1]; //Allocation size 65535 (sizeof(unsigned short))
	}
}
