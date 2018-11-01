package demo;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import jgame.Entity;

public class Bullet extends Entity {
	
	double xSpeed, ySpeed;
	int damage, lifespan;
	
	public Bullet(Player source, double speed, int damage) {
		this.rotation = source.rotation;
		this.rotationMode = RotationMode.NONE;
		this.xSpeed = source.xSpeed + speed*Math.cos(this.getRadians());
		this.ySpeed = source.ySpeed + speed*Math.sin(this.getRadians());
		this.damage = damage;
		this.lifespan = 100;
	}

	@Override
	public void setup() {
		this.shape = new Ellipse2D.Double(-2, -2, 5, 5);
		this.color = Color.WHITE;
		this.fill = false;
		this.lineWidth = 2;
	}

	@Override
	public void update() {
		x += xSpeed;
		y += ySpeed;
		lifespan -= 1;
		if(lifespan <= 0) {
			world.remove(this);
		}
	}

}
