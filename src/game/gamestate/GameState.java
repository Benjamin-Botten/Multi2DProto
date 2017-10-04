package game.gamestate;

import engine.io.Input;
import engine.visuals.viewport.Viewport;

public class GameState {
	
	protected GameState previousState;
	protected GameState nextState;
	
	public GameState(GameState previousState) {
		if(previousState == null) {
			this.previousState = this;
			this.nextState = this;
			return;
		}
		
		this.previousState = previousState;
		this.nextState = this;
	}

	public void tick(Input input) {
	}
	
	public void render(Viewport viewport) {
	}
	
	public GameState getPrevious() {
		return previousState;
	}
	
	public void setNext(GameState gameState) {
		nextState = gameState;
	}
	
	public GameState update() {
		return nextState;
	}
}
