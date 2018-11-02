package demoPong;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

import jgame.Direction;
import jgame.Entity;
import jgame.World;

public class Ball extends Entity {
	
	int delay;
	double speed, xSpeed, ySpeed;

	@Override
	public void setup() {
		this.shape = new Rectangle(-5,-5,10,10);
		this.rotationMode = RotationMode.NONE;
		this.color = Color.WHITE;
		reset();
	}

	@Override
	public void update() {
		if(--delay > 0) {
			return;
		}
		this.x += xSpeed;
		this.y += ySpeed;
		
		World.Edge edge = this.collisionWithType(World.Edge.class);
		if(edge != null) {
			if(edge.direction == Direction.NORTH || edge.direction == Direction.SOUTH) {	// Top or bottom: just bounce
				this.ySpeed *= -1;
			} else if(edge.direction == Direction.EAST) {									// Right player score
				PongGame.playerLeft.score++;
				reset();
			} else {																		// Left player score
				PongGame.playerRight.score++;
				reset();
			}
		}
		
		Paddle paddle = this.collisionWithType(Paddle.class);
		if(paddle != null) {
			if(paddle.onLeft == (xSpeed < 0)) {
				xSpeed *= -1;
				speed += 1;
				ySpeed += (y-paddle.y)/10;
				scaleSpeed();
			}
		}
	}
	
	/** Constrains the magnitude of velocity to speed */
	private void scaleSpeed() {
		double theta = Math.atan2(ySpeed, xSpeed);
		xSpeed = speed*Math.cos(theta);
		ySpeed = speed*Math.sin(theta);
	}

	/** Returns the ball to the middle of the screen, resets speed, and waits 100 frames before resuming */
	private void reset() {
		this.delay = 100;
		this.x = 0;
		this.y = 0;
		Random r = new Random();
		this.rotation = 45 + 90*r.nextInt(4);
		this.speed = 5;
		this.xSpeed = 5*Math.cos(this.getRadians());
		this.ySpeed = 5*Math.sin(this.getRadians());
	}
}
