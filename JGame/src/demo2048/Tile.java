package demo2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import jgame.*;

/** The numbered squares that make up the point of this game */
public class Tile extends Entity {

	static Font numberFont = new Font("Verdana", Font.BOLD, 50);					// Stored statically to save memory
	static Shape tileShape = new RoundRectangle2D.Double(-50,-50,100,100,20,20);	// Stored statically to save memory
	
	/** The colors to be used for tiles of different values */
	static Color[] colors = {
			new Color(0xeee4da),
			new Color(0xede0c8),
			new Color(0xf2b179),
			new Color(0xf59563),
			new Color(0xf67c5f),
			new Color(0xf65e3b),
			new Color(0xedcf72),
			new Color(0xedcc61),
			new Color(0xedc850),
			new Color(0xedc53f),
			new Color(0xedc22e),
			new Color(0x3c3a32)
	};

	Grid host;
	Tile mergeTarget;
	int moves, value;
	Point position;
	double dx, dy;

	public Tile(Grid host, int gridX, int gridY) {
		this.host = host;
		this.position = new Point(gridX, gridY);
	}

	@Override
	public void setup() {
		this.x = -host.size.width/2  + 70 + 120*position.x;
		this.y = -host.size.height/2 + 70 + 120*position.y;
		this.shape = tileShape;
		this.value = 0;
		this.color = colors[0];
	}

	@Override
	public void update() {
		if(moves > 0) {
			this.x += dx;
			this.y += dy;
			this.moves--;
		} else if(mergeTarget != null) {
			mergeTarget.embiggen();
			world.remove(this);
		}
	}
	
	/** Increases the value of the tile and increases the score */
	private void embiggen() {
		this.value++;
		this.color = colors[Math.min(value, colors.length-1)];
		this.host.score += Math.pow(2, 1+value);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setFont(numberFont);
		String text = ""+(int)Math.pow(2, 1+value); // Value increases linearly, but 2048 uses powers of 2
		
		//Calculate the x,y point to draw to center the text
		FontMetrics metrics = g.getFontMetrics();
		int x = -metrics.stringWidth(text) / 2;
	    int y = -metrics.getHeight()/2 + metrics.getAscent();
		
	    //If the string wouldn't fit within the tile, shrink it
	    if(x < -40) {
	    	g2.scale(-40.0/x, -40.0/x);
	    }
	    
	    if(value < 2) {	// The first 2 colors require a dark text color to be readable
	    	g2.setColor(new Color(0x776E65));
	    } else {		// All others should use an off-white
	    	g2.setColor(new Color(0xF9F6F2));
	    }
	    
		g2.drawString(text, x, y);
	}

	/**
	 * Changes the location of the tile to the specified location on the tile's host grid
	 * @param newPos Where to move the tile
	 * @return Whether the tile's position was changed
	 */
	public boolean moveTo(Point newPos) {
		if(newPos.equals(position)) {
			return false;
		}
		this.host.remove(position);
		this.host.set(this, newPos);
		this.position = newPos;
		slideToPos();
		return true;
	}
	
	/**
	 * Sets up required variables for smoothly sliding to the correct position of the tile
	 */
	private void slideToPos() {
		int fx = -host.size.width/2  + 70 + 120*position.x;
		int fy = -host.size.height/2 + 70 + 120*position.y;
		this.dx = (fx-x)/Grid.slideFrames;
		this.dy = (fy-y)/Grid.slideFrames;
		this.moves = Grid.slideFrames;
	}
	
	/**
	 * Returns whether a move can be made to an adjacent square
	 * @return Whether this tile can move
	 */
	public boolean canMove() {
		for(Direction d : Direction.cardinals()) {
			Point adjPoint = this.adjacentLocation(d);
			if(host.isValid(adjPoint)) {
				Tile adjacent = host.get(this.adjacentLocation(d));
				if(adjacent == null || this.canMergeWith(adjacent)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Gets the point corresponding to the location one step in the specified direction
	 * @param direction The direction from which to get the adjacent location
	 * @return The point representing the adjacent location
	 */
	public Point adjacentLocation(Direction direction) {
		Point out = (Point) position.clone();
		switch(direction) {
		case NORTH:
			out.y--;
			break;
		case SOUTH:
			out.y++;
			break;
		case EAST:
			out.x++;
			break;
		case WEST:
			out.x--;
			break;
		default:
			return null;
		}
		return out;
	}
	
	/**
	 * Returns whether this tile can merge with the specified tile
	 * @param other The tile to check for merge-ability
	 * @return Whether the tile can be merged with
	 */
	public boolean canMergeWith(Tile other) {
		return this.value == other.value;
	}
	
	/**
	 * Returns the X position of this tile within it's host grid
	 * @return This tile's column
	 */
	public int getX() {
		return position.x;
	}
	
	/**
	 * Returns the Y position of this tile within it's host grid
	 * @return This tile's row
	 */
	public int getY() {
		return position.y;
	}
	
	/**
	 * Tells this tile to merge with another tile, sliding to it's position and increasing it's value
	 * @param other The tile to merge with
	 */
	public void mergeWith(Tile other) {
		this.host.remove(position);
		this.mergeTarget = other;
		this.position = other.position;
		slideToPos();
	}
}
