package demoPong;

import java.awt.Color;
import java.awt.Rectangle;

import jgame.Controller;
import jgame.Direction;
import jgame.Entity;
import jgame.World;

public class Paddle extends Entity {
	
	/** Whether the paddle is the left one. If not, it's probably on the right */
	boolean onLeft;
	
	/** For getting controls, either 'L' or 'R' */
	char prefix;
	
	/** How many points this paddle has */
	int score;
	
	public Paddle(boolean onLeft) {
		this.onLeft = onLeft;
		this.prefix = onLeft?'L':'R';
	}

	@Override
	public void setup() {
		this.x = (onLeft?-1:1)*(world.getSize().width/2-20);
		this.y = 0;
		this.shape = new Rectangle(-5,-25,10,50);
		this.color = Color.WHITE;
	}

	@Override
	public void update() {
		if(Controller.controlDown(prefix+"_UP")) {
			this.y -= 5;
		}
		if(Controller.controlDown(prefix+"_DOWN")) {
			this.y += 5;
		}
		World.Edge edgeCollide = this.collisionWithType(World.Edge.class);
		if(edgeCollide != null) {
			int move = (edgeCollide.direction == Direction.NORTH)?1:-1;
			while(this.collidesWith(edgeCollide)) {
				this.y += move;
			}
		}
	}

}
