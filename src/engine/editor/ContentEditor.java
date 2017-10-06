package engine.editor;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.PopupMenu;
import java.awt.TextField;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import engine.io.Input;
import engine.io.SimpleInput;
import engine.util.EntityRegistry;
import engine.util.LevelUtility;
import engine.util.Rect2D;
import engine.util.TileRegistry;
import engine.visuals.SpriteSheet;
import engine.visuals.gui.GUIPicker;
import engine.visuals.viewport.Camera;
import engine.visuals.viewport.Viewport;
import engine.world.Level;
import engine.world.World;
import engine.world.entity.Entity;
import engine.world.entity.EntityFactory;
import engine.world.tile.Tile;
import game.Game;

public class ContentEditor {
	public static final int WIDTH = 640 * 2;
	public static final int HEIGHT = 480 * 2;
	public static final int SCALE = 2;
	public static final String TITLE = "Content Editor";
	
	public static final int TYPE_EDIT_NONE = 0;
	public static final int TYPE_EDIT_TILES = 1;
	public static final int TYPE_EDIT_ENTITIES = 2;
	
	private int editType = TYPE_EDIT_NONE;
	private String editName = "";
	
	private boolean openedWorld;
	private boolean editable; //is there an editable world?
	private boolean canPutEntity; //can put new entity, used to prevent the mouse-event adding duplicate entities
	private EditorWorld world;
	
	private boolean running = false;
	public Viewport viewport;
	public Camera camera;
	public Input input;
	
	//Parent Frame
	private JFrame frame = new JFrame(TITLE);

	//Menu stuff
	private JMenuBar menubar = new JMenuBar();
	private JMenu filemenu = new JMenu("File");
	private JMenuItem newFile = new JMenuItem("New");
	private JMenuItem openFile = new JMenuItem("Open");
	private JMenuItem saveFile = new JMenuItem("Save");
	
	//Panels
	private JPanel navigation = new JPanel();
	private JPanel tilePanel = new JPanel();
	private JPanel entityPanel = new JPanel();
	
	private Choice tileChoice = new Choice();
	private Choice entityChoice = new Choice();
	
	private Button selectTile = new Button("Select");
	private Button selectEntity = new Button("Select");
	
	public ContentEditor() {
		running = true;
		
		camera = new Camera();
		
		viewport = new Viewport(WIDTH, HEIGHT, SCALE, TITLE, camera, frame);
		
		input = new SimpleInput(viewport);
		
		world = new EditorWorld();
		
		init();
		
		filemenu.add(newFile);
		filemenu.add(openFile);
		filemenu.add(saveFile);
		menubar.add(filemenu);
		frame.setJMenuBar(menubar);
		
		
		navigation.setLayout(new BoxLayout(navigation, BoxLayout.Y_AXIS));
//		tilePanel.setLayout();
//		entityPanel.setLayout();
		navigation.setPreferredSize(new Dimension(240, HEIGHT));
		tilePanel.setPreferredSize(new Dimension(240, HEIGHT / 2));
		entityPanel.setPreferredSize(new Dimension(240, HEIGHT / 2));
		
		populateChoices();
		
		tilePanel.add(tileChoice);
		tilePanel.add(selectTile);
		entityPanel.add(entityChoice);
		entityPanel.add(selectEntity);
		
		JSeparator navigationSeparator = new JSeparator(SwingConstants.HORIZONTAL);
		navigation.add(tilePanel);
		navigation.add(navigationSeparator);
		navigation.add(entityPanel);
		
		frame.add(navigation, BorderLayout.EAST);
		frame.pack();
	}
	
	private void populateChoices() {
		
		for(int i = 0; i < Entity.typenames.length; ++i) {
			entityChoice.add(Entity.typenames[i]);
		}
		
		for(int i = 0; i < Tile.tiles.length; ++i) {
			if(Tile.tiles[i] == null) break;
			
			tileChoice.add(Tile.tiles[i].toString());
		}
	}
	
	public void init() {
		//Initialize all the event/actionlisteners to GUI elements
		newFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent action) {
				NumberFormatter formatterInt = new NumberFormatter();
				formatterInt.setValueClass(Integer.class);
				formatterInt.setAllowsInvalid(false);
				formatterInt.setMinimum(0);
				formatterInt.setMaximum(Integer.MAX_VALUE);
				
				JPanel popup = new JPanel();
				final String hintString = "Enter name";
				TextField nameField = new TextField(hintString);
				nameField.setForeground(Color.GRAY);
				nameField.setColumns(30);
				nameField.addFocusListener(new FocusListener() {
					
					@Override
					public void focusGained(FocusEvent arg0) {
						if(nameField.getText().length() == 0) {
							nameField.setText(hintString);
							nameField.setForeground(Color.BLACK);
						}
						else if(nameField.getText().equals(hintString)) {
							nameField.setText("");
							nameField.setForeground(Color.BLACK);
						}
					}

					@Override
					public void focusLost(FocusEvent arg0) {
						if(nameField.getText().length() == 0) {
							nameField.setText(hintString);
							nameField.setForeground(Color.GRAY);
						}
					}
					
				});
				
				JFormattedTextField widthField = new JFormattedTextField(formatterInt);
				JFormattedTextField heightField = new JFormattedTextField(formatterInt);
				widthField.setColumns(10);
				heightField.setColumns(10);
				KeyListener formattedTextKeyListener = new KeyListener() {
					@Override
					public void keyPressed(KeyEvent arg0) {
						if(arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
							if(widthField.getText().length() == 1) {
								widthField.setText("0");
							}
						}
					}
					@Override
					public void keyReleased(KeyEvent arg0) {
					}
					@Override
					public void keyTyped(KeyEvent arg0) {
					}
				};
				
				widthField.addKeyListener(formattedTextKeyListener);
				heightField.addKeyListener(formattedTextKeyListener);
				
				Button createButton = new Button("Create");
				createButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JOptionPane.showMessageDialog(frame, "Create", "Creating level", JOptionPane.INFORMATION_MESSAGE);
						frame.remove(popup);
						frame.pack();
						
						if(LevelUtility.createLevel(nameField.getText(), Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()))) {
							world.setLevel(LevelUtility.get(nameField.getText()));
							openedWorld = true;
						}
					}
					
				});
				
				popup.add(nameField);
				popup.add(widthField);
				popup.add(heightField);
				popup.add(createButton);
				frame.add(popup, BorderLayout.NORTH);
				frame.pack();
			}
			
		});
		
		saveFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showInputDialog(frame, "Save", "Saving Level", JOptionPane.PLAIN_MESSAGE);
				LevelUtility.saveLevel(world.getLevel(), world.getEntities(), world.getTiles(), world.getWidth(), world.getHeight());
			}
			
		});
		
		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String nameLevel = "";
				nameLevel = JOptionPane.showInputDialog(frame, "Load Level", null, JOptionPane.PLAIN_MESSAGE);
				if(LevelUtility.get(nameLevel) != null) {
					world.setLevel(new Level(nameLevel));
					LevelUtility.loadLevel(world);
					openedWorld = true;
				}
			}
			
		});
		
		
		selectTile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editName = tileChoice.getSelectedItem();
				editType = TYPE_EDIT_TILES;
			}
			
		});
		
		selectEntity.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				editName = entityChoice.getSelectedItem();
				editType = TYPE_EDIT_ENTITIES;
			}
			
		});
	}
	
	public void run() {
		while(running) {
			viewport.clear();
			
			//Render if there is a world
			int[] tiles = world.getTiles();
			if(tiles != null) {
				for(int y = 0; y < world.getHeight(); ++y) {
					for(int x = 0; x < world.getWidth(); ++x) {
						viewport.render(Tile.tiles[tiles[x + y * world.getWidth()]], x * Tile.w * Game.SCALE, y * Tile.h * Game.SCALE);
					}
				}
			}
			List<Entity> entities = world.getEntities();
			if(entities != null) {
				for(int i = 0; i < entities.size(); ++i) {
					entities.get(i).render(viewport);
				}
			}
			
			if(input.mouseLeft()) {
				System.out.println("Mouse left is down");
				int mx = input.getMouseXDragged();
				int my = input.getMouseYDragged();
				if(Rect2D.isColliding(mx, my, 1, 1, 0, 0, WIDTH, HEIGHT)) {
					if(editType == TYPE_EDIT_TILES) {
						world.setTile(Tile.tiles[new TileRegistry().getId(editName)], mx, my);
					}
					if(editType == TYPE_EDIT_ENTITIES && canPutEntity) {
						world.addEntity(EntityFactory.create(editName, input.getMouseX(), input.getMouseY()));
						canPutEntity = false;
					}
				}
			}
			else if(!input.mouseLeft()) {
				canPutEntity = true;
			}
			
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			viewport.swap();
		}
	}
	
	public static void main(String[] args) {
		ContentEditor editor = new ContentEditor();
		editor.run();
	}
}
