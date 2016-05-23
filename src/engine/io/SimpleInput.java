package engine.io;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SimpleInput extends Input implements MouseListener, KeyListener {

	public int mx, my;
	public boolean[] pressed = new boolean[Short.MAX_VALUE];
	public boolean wasPressed, ml, mr;
	
	public SimpleInput(Canvas canvas) {
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addKeyListener(this);
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		pressed[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		pressed[e.getKeyCode()] = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1) {
			ml = true;
		}
		else if(button == MouseEvent.BUTTON3) {
			mr = true;
		}
		System.out.println("Mouse Pressed @(" + mx + ", " + my + ")");
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		int button = e.getButton();
		if(button == MouseEvent.BUTTON1) {
			ml = false;
		}
		else if(button == MouseEvent.BUTTON3) {
			mr = false;
		}
		System.out.println("Mouse Released @(" + mx + ", " + my + ")");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
}
