package demo;
import jgame.Controller;
import jgame.Direction;
import jgame.Entity;
import jgame.Images;
import jgame.Sprite;
import jgame.World;

public class Player extends Entity {
	
	Sprite onSprite, offSprite;
	double xSpeed, ySpeed;

	@Override
	public void setup() {
		onSprite = Images.getSprite("ship_on");
		offSprite = Images.getSprite("ship_off");
		onSprite.setHitbox(offSprite.getShape());
		this.sprite = offSprite;
	}

	@Override
	public void update() {
		this.x += xSpeed;
		this.y += ySpeed;
		
		for(World.Edge collision : this.collisionsWithType(World.Edge.class)) {
			if(collision.direction == Direction.EAST || collision.direction == Direction.WEST) {
				x -= xSpeed;
				xSpeed *= -0.8;
			}
			if(collision.direction == Direction.NORTH || collision.direction == Direction.SOUTH) {
				y -= ySpeed;
				ySpeed *= -0.8;
			}
		}
		
		
		if(Math.abs(this.x) > world.getSize().getWidth()/2 + 5) {
			this.x *= -1;
		}
		if(Math.abs(this.y) > world.getSize().getHeight()/2 + 5) {
			this.y *= -1;
		}
		
		if(Controller.controlDown("up")) {
			this.xSpeed += Math.cos(this.getRadians());
			this.ySpeed += Math.sin(this.getRadians());
			this.sprite = onSprite;
		} else {
			this.sprite = offSprite;
		}
		if(Controller.controlDown("left")) {
			this.rotation -= 5;
		}
		if(Controller.controlDown("right")) {
			this.rotation += 5;
		}
		if(Controller.controlPressed("space")) {
			world.add(new Bullet(this,10,1),x,y);
		}
		this.xSpeed *= 0.95;
		this.ySpeed *= 0.95;
	}

}
