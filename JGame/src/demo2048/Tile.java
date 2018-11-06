package demo2048;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import jgame.*;

public class Tile extends Entity {
	
	Color[] colors = {
			new Color(0xeee4da),	// 2
			new Color(0xede0c8),	// 4
			new Color(0xf2b179),	// 8
			new Color(0xf59563),	// 16
			new Color(0xf67c5f),	// 32
			new Color(0xf65e3b),	// 64
			new Color(0xedcf72),	// 128
			new Color(0xedcc61),	// 256
			new Color(0xedc850),	// 512
			new Color(0xedc53f),	// 1024
			new Color(0xedc22e),	// 2048
			new Color(0x3c3a32)		// 4096+
			};
	int gridX, gridY;
	double finX, finY;
	boolean doneMoving, delete;
	Grid host;
	
	int value = 0;

	public Tile(Grid host) {
		this.host = host;
	}
	
	@Override
	public void setup() {
		this.shape = new Rectangle(-50,-50,100,100);
		this.color = new Color(0xeee4da);
	}

	@Override
	public void update() {
		doneMoving = true;
		if(Math.abs(x-finX) < 1) {
			x = finX;
		} else {
			doneMoving = false;
			if(x < finX) {
				x += 5;
			} else if(x > finX) {
				x -= 5;
			}
		}
		if(Math.abs(y-finY) < 1) {
			y = finY;
		} else {
			doneMoving = false;
			if(y < finY) {
				y += 5;
			} else if(y > finY) {
				y -= 5;
			}
		}
		if(delete && doneMoving) {
			world.remove(this);
		}
	}
	
	public boolean canMove() {
		for(Direction d : Direction.cardinals()) {
			if(canMove(d)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canMove(Direction d) {
		Point p = adjacentPoint(d);
		return host.isValid(p.x, p.y) && (host.get(p.x, p.y) == null || host.get(p.x, p.y).value == this.value);
	}
	
	public void move(Direction d) {
		if(canMove(d)) {
			Point p = adjacentPoint(d);
			Tile adj = host.get(p.x, p.y);
			if(adj == null) {
				host.set(null, gridX, gridY);
				host.set(this, p.x, p.y);
			} else {
				if(adj.value == value) {
					adj.merge();
					host.set(null, gridX, gridY);
					this.finX = adj.finX;
					this.finY = adj.finY;
					this.delete = true;
				}
			}
		}
	}
	
	public Point adjacentPoint(Direction direction) {
		int checkX = gridX, checkY = gridY;
		switch(direction) {
		case NORTH:
			checkY -= 1;
			break;
		case SOUTH:
			checkY += 1;
			break;
		case EAST:
			checkX += 1;
			break;
		case WEST:
			checkX -= 1;
			break;
		default:
			return null;
		}
		return new Point(checkX, checkY);
	}
	
	public void merge() {
		value++;
		color = colors[Math.min(11, value)];
	}
}
