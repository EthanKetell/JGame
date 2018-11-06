package demo2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jgame.*;

public class NumberGame extends Game {
	
	private static BufferedImage background;
	private Grid grid;
	
	static {
		background = new BufferedImage(500,500,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) background.getGraphics();
		g.setColor(new Color(0xB9ADA1));
		g.fillRect(0, 0, 500, 500);
		g.setColor(new Color(0xCAC1B5));
		for(int x = 20; x < 500; x += 120) {
			for(int y = 20; y < 500; y += 120) {
				g.fillRect(x, y, 100, 100);
			}
		}
		g.dispose();
	}

	public static void main(String[] args) {
		new NumberGame().start();
	}

	@Override
	protected void setup() {
		this.setWindowSize(new Dimension(500,500));
		world.setBackground(new Color(0x005b10));
		world.setFillSprite(new Sprite(background));
		world.add(grid = new Grid());
		this.addTile();
		this.addTile();
	}

	boolean waitForMove = true;
	@Override
	protected void update() {
		List<Tile> tiles = world.getAllOfType(Tile.class);
		for(Tile t : tiles) {
			if(!t.doneMoving) {
				return;
			}
		}
		
		if(!waitForMove) {
			addTile();
			waitForMove = true;
			return;
		}
		
		Direction direc;
		if(Controller.controlPressed("up")) {
			direc = Direction.NORTH;
		} else if(Controller.controlPressed("down")) {
			direc = Direction.SOUTH;
		} else if(Controller.controlPressed("left")) {
			direc = Direction.WEST;
		} else if(Controller.controlPressed("right")) {
			direc = Direction.EAST;
		} else {
			return;
		}
		
		Comparator<Tile> byPos = new Comparator<Tile>() {
			public int compare(Tile o1, Tile o2) {
				switch(direc) {
				case NORTH:
					return o1.gridY-o2.gridY;
				case SOUTH:
					return o2.gridY-o1.gridY;
				case EAST:
					return o2.gridX-o1.gridX;
				case WEST:
					return o1.gridX-o2.gridX;
				default:
					return 0;
				}
			}
		};
		tiles.sort(byPos);
		for(Tile t : tiles) {
			t.move(direc);
		}
		waitForMove = false;
	}
	
	private void addTile() {
		Point p = grid.getEmptySpot();
		
		if(p != null) {
			Tile newTile = new Tile(grid);
			grid.set(newTile, p.x, p.y);
			newTile.x = newTile.finX;
			newTile.y = newTile.finY;
			
		}
	}

}
