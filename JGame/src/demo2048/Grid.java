package demo2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jgame.*;

/** Class for drawing board and managing tiles */
public class Grid extends Entity {
	
	private static Font scoreFont = NumberGame.font.deriveFont(30f);
	
	/**A mini-class to make it easier to step through the lines provided by {@code getLineIndices}*/
	private class LineTracker {
		
		private int[] line;
		private int index;
		
		LineTracker(int[] line) {
			this.line = line;
			this.index = 0;
		}

		boolean hasNext() {
			return index < line.length;
		}
		
		int get() {
			return line[index];
		}
		
		void move() {
			index++;
		}
	}

	int width, height, score, slideFrames;
	Tile[] tiles;
	Dimension size;

	/**
	 * Creates a grid of Tiles with the specified dimensions
	 * @param width how many columns
	 * @param height how many rows
	 */
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setup() {
		this.tiles = new Tile[width * height];
		this.size = new Dimension(20 + 120 * width, 20 + 120 * height);
		this.addTile();
		this.addTile();
	}
	
	@Override
	public void update() {
		if(slideFrames > 0) {
			slideFrames--;
		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(-size.width / 2, -size.height / 2);

		g2.setColor(new Color(0xBBADA0)); // Background color
		g2.fillRoundRect(0, 0, size.width, size.height, 20, 20);
		
		g2.setFont(scoreFont);
		FontMetrics metrics = g2.getFontMetrics();
		String text = "Score: "+score;
		g2.fillRoundRect(0,-55,20+metrics.stringWidth(text),50,20,20);
		g2.setColor(new Color(0xF9F6F2));
		g2.drawString(text, 10, -30-metrics.getHeight()/2 + metrics.getAscent());

		g2.setColor(new Color(0xCDC1B4)); // Empty tile color
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				g2.fillRoundRect(20 + 120 * x, 20 + 120 * y, 100, 100, 20, 20);
			}
		}
	}
	
	/**
	 * Attempts to slide all tiles in the specified direction
	 * @param direction which way to slide the tiles
	 * @return whether any tiles were moved
	 */
	public boolean slideTiles(Direction direction) {
		boolean didMove = false;
		for (int[] line : getLineIndices(direction.opposite())) {
			LineTracker curr = new LineTracker(line);
			curr.move();
			LineTracker prev = new LineTracker(line);
			for(	; curr.hasNext(); curr.move()) {
				Tile tile = tiles[curr.get()];
				if(tile == null) continue;
				Tile last = tiles[prev.get()];
				if(last == null) {
					didMove |= tile.moveTo(new Point(prev.get()%width, prev.get()/width));
				} else {
					if(tile.canMergeWith(last)) {
						didMove = true;
						tile.mergeWith(last);
					} else {
						didMove |= tile.moveTo(last.adjacentLocation(direction.opposite()));
					}
					prev.move();
				}
			}
		}
		if(didMove) {
			this.slideFrames = NumberGame.framesToSlide;
		}
		return didMove;
	}
	
	/** Returns whether there is sliding in progress */
	public boolean isSliding() {
		return slideFrames > 0;
	}
	
	/**
	 * Convenience method; throws an exception if p does not lie on the grid
	 * @param p The point to check
	 */
	private void checkRange(Point p) {
		if (!isValid(p))
			throw new IndexOutOfBoundsException("out of bounds access "+p);
	}
	
	/**
	 * Returns whether the specified point is within the bounds of the grid
	 * </br> i.e. {@code (0 <= x < width) && (0 <= y < height)}
	 * @param p The point to check
	 * @return Whether the point is on the grid
	 */
	public boolean isValid(Point p) {
		return (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height);
	}

	/**
	 * Returns the tile at the given point
	 * @param p The point to get a tile from
	 * @return The tile at p if one is there, else null
	 */
	public Tile get(Point p) {
		checkRange(p);
		return tiles[p.y * width + p.x];
	}

	/**
	 * Places a tile on the grid at a specified point
	 * @param tile The tile to place
	 * @param p The point at which to place the tile
	 */
	public void set(Tile tile, Point p) {
		checkRange(p);
		tiles[p.y * width + p.x] = tile;
	}

	/**
	 * Removes the tile at the specified point
	 * @param p The point from which to remove the tile
	 * @return The removed tile, if one was at the point, else null
	 */
	public Tile remove(Point p) {
		checkRange(p);
		Tile out = tiles[p.y * width + p.x];
		tiles[p.y * width + p.x] = null;
		return out;
	}

	/**
	 * Adds a tile to a random empty location on the grid
	 */
	public void addTile() {
		List<Integer> empty = emptySpots();
		int index = empty.get(new Random().nextInt(empty.size()));
		world.add(tiles[index] = new Tile(this, index % width, index / width));
	}

	/**
	 * Returns a list of all valid points which do not contain a tile
	 * @return A list of points corresponding to empty grid locations
	 */
	private List<Integer> emptySpots() {
		List<Integer> out = new ArrayList<Integer>(width * height);
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null) {
				out.add(i);
			}
		}
		return out;
	}

	/**
	 * Returns a 2D array containing the rows or columns of the grid, with the indices ordered
	 * so that forward iteration moves along the row or column in the specified direction
	 * @param direction The desired direction to iterate the grid
	 * @return An array containing arrays of indices
	 */
	private int[][] getLineIndices(Direction direction) {
		int[][] out;
		if (direction == Direction.NORTH || direction == Direction.SOUTH) {
			out = new int[height][width];
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					out[x][y] = y * width + x;
				}
			}
		} else {
			out = new int[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					out[y][x] = y * width + x;
				}
			}
		}
		if (direction == Direction.NORTH || direction == Direction.WEST) { // In these cases, line direction should be swapped
			for (int[] line : out) {
				int left = 0;
				int right = line.length - 1;
				while (left < right) {
					int temp = line[left];
					line[left] = line[right];
					line[right] = temp;
					left++;
					right--;
				}
			}
		}
		return out;
	}
	
	/**
	 * Returns whether this grid has a move which can be made.
	 * @return Whether there is a valid move to be made
	 */
	public boolean hasMove() {
		for(Tile t : tiles) {
			if(t == null || t.canMove()) {
				return true;
			}
		}
		return false;
	}

}
