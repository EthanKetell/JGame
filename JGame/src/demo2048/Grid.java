package demo2048;

import java.util.List;
import java.util.Random;

import jgame.Entity;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

public class Grid extends Entity {
	
	private Tile[] tiles = new Tile[16];
	
	public void set(Tile tile, int x, int y) {
		if(!isValid(x,y)) {
			throw new IndexOutOfBoundsException("Point ("+x+", "+y+") is out of range!");
		}
		tiles[x + 4*y] = tile;
		if(tile != null) {
			tile.gridX = x;
			tile.gridY = y;
			tile.finX = -180 + 120*x;
			tile.finY = -180 + 120*y;
			world.add(tile, tile.x, tile.y);
		}
	}
	
	public boolean isValid(int x, int y) {
		return x >= 0 && x < 4 && y >= 0 && y < 4;
	}
	
	public Tile get(int x, int y) {
		if(!isValid(x,y)) {
			throw new IndexOutOfBoundsException("Point ("+x+", "+y+") is out of range!");
		}
		return tiles[x + 4*y];
	}
	
	public Point getEmptySpot() {
		List<Integer> empty = new ArrayList<Integer>();
		for(int i = 0; i < tiles.length; i++) {
			if(tiles[i] == null) {
				empty.add(i);
			}
		}
		if(empty.size() > 0) {
			Random r = new Random();
			int index = empty.get(r.nextInt(empty.size()));
			return new Point(index%4, index/4);
		} else {
			return null;
		}
	}

	@Override
	public void setup() {
		this.color = new Color(0,0,0,0);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
