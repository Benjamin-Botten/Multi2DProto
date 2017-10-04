package engine.world.entity;

import java.net.InetAddress;

import com.sun.glass.events.KeyEvent;

import engine.io.Input;
import engine.util.Direction;
import engine.visuals.Sprite;
import engine.visuals.SpriteSheet;
import engine.visuals.gui.GUIInventory;
import engine.visuals.viewport.Camera;
import engine.visuals.viewport.Viewport;
import engine.world.World;
import engine.world.entity.action.Action;
import engine.world.entity.action.ActionQueue;
import engine.world.entity.playerclass.PlayerClass;
import engine.world.item.Item;
import engine.world.item.LifeFlask;
import game.Game;

public class Player extends Entity {

	private PlayerOnline playerOnline;
	private Input input;
	private Camera camera;
	protected PlayerClass playerClass;
	
	private int droppingSlot = GUIInventory.ID_SELECTION_NONE;

	public Player(Input input) {
		super();

		sprite = new Sprite(SpriteSheet.entities, 0, 0, true);

		this.input = input;

		actionProgress = 0;
		
		inventory.add(0, new LifeFlask());
		inventory.add(8, new LifeFlask());
	}

	/**
	 * This constructor is awkward and a code smell from bad networking design
	 * @param username
	 * @param ip
	 * @param port
	 */
	public Player(String username, InetAddress ip, int port) {
		super();

		sprite = new Sprite(SpriteSheet.entities, 0, 0, true);

		playerOnline = new PlayerOnline(username, ip, port);
	}

	public void setCamera(int camX, int camY) {
		if (camera == null)
			return;

		if (camX < 0) {
			camX = 0;
		}
		if (camY < 0) {
			camY = 0;
		}
		camera.set(camX, camY);
	}
	
	public void heal(int amount) {
		setLife(life + amount);
	}

	public void tick() {
		super.tick();
		
		if(dead()) {
			return;
		}
		
		if(target != null) {
			if(target.dead()) {
				target = null;
			}
			if(target instanceof ItemEntity) {
				if(target.canRemove())  {
					target = null;
				}
			}
		}
		
		if(ticks % 16 == 0) {
			heal(25);
		}
		
		sprite.tick();

		setCamera((int) x - Game.HALF_WIDTH, (int) y - Game.HALF_HEIGHT);

		if (actionProgress >= 100) {
			actionProgress = 0;
			currentAction = Action.noAction;
		}
		if (currentAction == Action.noAction) {
			actionProgress = 0;
		}

		if (input != null) {
			if (input.keys[KeyEvent.VK_A]) {
				directionMovement = Direction.DIR_LEFT;
				directionFacing = Direction.DIR_LEFT;
				velX = -speed;
			}
			if (input.keys[KeyEvent.VK_D]) {
				directionMovement = Direction.DIR_RIGHT;
				directionFacing = Direction.DIR_RIGHT;
				velX = speed;
			}
			if (input.keys[KeyEvent.VK_W]) {
				directionMovement = Direction.DIR_UP;
				directionFacing = Direction.DIR_UP;
				velY = -speed;
			}
			if (input.keys[KeyEvent.VK_S]) {
				directionMovement = Direction.DIR_DOWN;
				directionFacing = Direction.DIR_DOWN;
				velY = speed;
			}
			if (input.keys[KeyEvent.VK_D] && input.keys[KeyEvent.VK_W]) {
				directionMovement = Direction.DIR_UP_RIGHT;
				directionFacing = Direction.DIR_UP_RIGHT;
				velX = speed;
				velY = -speed;
			}
			if (input.keys[KeyEvent.VK_D] && input.keys[KeyEvent.VK_S]) {
				directionMovement = Direction.DIR_DOWN_RIGHT;
				directionFacing = Direction.DIR_DOWN_RIGHT;
				velX = speed;
				velY = speed;
			}
			if (input.keys[KeyEvent.VK_A] && input.keys[KeyEvent.VK_W]) {
				directionMovement = Direction.DIR_UP_LEFT;
				directionFacing = Direction.DIR_UP_LEFT;
				velX = -speed;
				velY = -speed;
			}
			if (input.keys[KeyEvent.VK_A] && input.keys[KeyEvent.VK_S]) {
				directionMovement = Direction.DIR_DOWN_LEFT;
				directionFacing = Direction.DIR_DOWN_LEFT;
				velX = -speed;
				velY = speed;
			}
			if (input.keys[KeyEvent.VK_TAB]) {
			}
			if (input.keys[KeyEvent.VK_SPACE]) {
				if (hasTarget) {
					attacking = true;
				}
			}
			if (input.keys[KeyEvent.VK_1]) {
				if (currentAction != Action.weaponAttack) {
					castTime = System.currentTimeMillis();
					currentAction = Action.weaponAttack;
					System.out.println("Pressing 1 > Queuing weapon attack");
				}
			}
			if (input.keys[KeyEvent.VK_2]) {
				if (target != null) {
					if (currentAction != Action.frostbite) {
						castTime = System.currentTimeMillis();
						currentAction = Action.frostbite;
						System.out.println("Pressing 2 > Queuing frostbite attack");
					}
				}
			}
		}

		if (currentAction.getId() == Action.weaponAttack.getId()) {
			updateActionProgress();
		} else if (currentAction.getId() == Action.frostbite.getId()) {
			updateActionProgress();
		} else if (currentAction.getId() == Action.noAction.getId()) {
			actionProgress = 0;
		}
		
		if (target == null) {
			hasTarget = false;
		} else {
			hasTarget = true;
		}
		
		directionFacing = getDirectionToTarget();

		sprite.setDirectionMovement(directionMovement);
		sprite.setDirectionFacing(directionFacing);
	}

	public void render(Viewport viewport) {
		viewport.render(this);
		
		camera = viewport.getCamera();
		
		int xOffset = 8;
		int yOffset = 8;
		
		viewport.render(getUsername(), (int) x + xOffset * viewport.getScale(), (int) y - yOffset * viewport.getScale());
		
		viewport.renderHealthBar(this, Game.SCALE);

		if (hasTarget) {
			if (target instanceof ItemEntity) {
				ItemEntity itemTarget = (ItemEntity) target;
				viewport.renderGuiElement(5, (int) itemTarget.x, (int) itemTarget.y + 5, itemTarget.w, itemTarget.h);
				viewport.renderGuiElement(6, (int) target.x, (int) target.y - 16, target.w, target.h, ticks, 0.1625f,
						0.25f);
				
				if(target.isWithinRadius(this, 1024)) {
					viewport.render("E To Pickup", (int) target.getCenterX(), (int) target.getCenterY() - target.h, 0xffffffff);
				}
			} else {
				viewport.renderGuiElement(5, (int) target.x + target.w / 8, (int) target.y + 6 * 2, target.w, target.h);
				viewport.renderGuiElement(6, (int) target.x + target.w / (2 * Game.SCALE), (int) target.y - 32,
						target.w, target.h, ticks, 0.1625f, 0.25f);
			}
		}
		
		
		viewport.renderBounds(this);
	}

	public void handle(World world) {
		x += velX;

		if (world.isColliding(this)) {
			Entity interactable = world.getInteractable(this);
			if (interactable != null) {
				System.out.println("Interacting with " + interactable);
				interactable.interact(world, this);
			}
			x -= velX;
		}

		y += velY;

		if (world.isColliding(this)) {
			Entity interactable = world.getInteractable(this);
			if (interactable != null) {
				System.out.println("Interacting with " + interactable);
				interactable.interact(world, this);
			}
			y -= velY;
		}

		velX = 0;
		velY = 0;

		if (input.mouseLeft()) {
			Entity e = world.getEntity(input.getMouseXTranslated(camera.getX()),
					input.getMouseYTranslated(camera.getY()), 1, 1);
			if (e != null) {
				if (e instanceof ItemEntity) {
					target = (ItemEntity) e;
				} else {
					target = e;
				}
				hasTarget = true;
			} else {
				hasTarget = false;
				target = null;
			}
		}

		/** Handle attacking */
		if (hasTarget) {
			if(currentAction == Action.frostbite && actionProgress >= 100) {
				world.addAttackEntity(new FrostbiteEntity(this, target, x, y));
			}
			if (attacking) {
				if (!target.dead()) {
					
				} else {
					 attacking = false;
					 hasTarget = false;
					 target = null;
				}
			}
			
			if(target instanceof ItemEntity) {
				if(input.keys[KeyEvent.VK_E]) {
					target.interact(world, this);
				}
			}
			
		} else {
			attacking = false;
		}
	}
	
	/**
	 * Drops item at given slot in inventory
	 * @param slot
	 */
	public void dropItem(int slot) {
		droppingSlot = slot;
	}

	public boolean solid() {
		return collidable;
	}

	public Camera getCamera() {
		return camera;
	}

	public PlayerOnline getPlayerOnline() {
		return playerOnline;
	}

	public boolean isConnected() {
		return playerOnline.isConnected();
	}

	public String getUsername() {
		return playerOnline.getUsername();
	}

	public void setPlayerOnline(PlayerOnline playerOnline) {
		this.playerOnline = playerOnline;
	}

	public void setConnected(boolean connected) {
		playerOnline.setConnected(connected);
	}

	public void setNetId(int netId) {
		this.netId = netId;
	}
	
	public String getName() {
		return "Player";
	}
}
