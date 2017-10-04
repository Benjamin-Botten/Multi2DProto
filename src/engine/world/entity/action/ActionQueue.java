package engine.world.entity.action;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A wrapper to restrict multiple action events on the client
 * The queue contains the ID for the performed action on the client side
 * It is sent to the server for handling
 * Any other action logic is separate from the action-queue
 * 
 * @author robot
 *
 */
public class ActionQueue {
	public LinkedList<Action> actions = new LinkedList<Action>();
	
	public void queue(Action action) {
		if(actions.size() > 1) {
			//System.out.println("There's already an action in progress");
			return;
		}
		
		actions.push(action);
	}
	
	public Action dequeue() {
		if(actions.size() <= 0) {
			//System.out.println("Attempting to dequeue action queue > There're no actions left to perform");
			return Action.noAction;
		}
		
		return actions.pop();
	}
	
	/**
	 * Get the action in the queue without dequeuing it
	 * @return Action
	 */
	public Action peek() {
		if(actions.size() != 0) {
			return actions.get(0);
		}
		return Action.noAction;
	}
	
	/**
	 * Check for pending action in queue
	 * @return boolean
	 * true if there is an action queued,
	 * false if no action is in queue
	 */
	public boolean isPending() {
		return actions.size() > 0;
	}
}
