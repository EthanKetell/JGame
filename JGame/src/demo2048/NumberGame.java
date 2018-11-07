package demo2048;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import jgame.*;

public class NumberGame extends Game {
	
	public static final Font font = new Font("Verdana", Font.BOLD, 50);
	public static final int framesToSlide = 10; // How many frames tiles should take per move
	
	Grid board;

	public static void main(String[] args) {
		new NumberGame().start();
	}

	@Override
	protected void setup() {
		this.setWindowSize(new Dimension(600,700));
		world.add(board = new Grid(4,4));
		world.setZoomType(World.ZoomType.LETTERBOX);
		world.camera.y = -50;
		world.setBackground(new Color(0xFAF8EF));
		world.setFillPaint(new Color(0xFAF8EF));
	}

	/**
	 * Tracks which step the game is on
	 * </br>0 = waiting for input from the user
	 * </br>1 = sliding the tiles
	 * </br>2 = adding a new tile
	 * </br>3 = waiting to restart after game over
	 */
	int gameState = 0;
	protected void update() {
		
		// Enable click and drag to move game
		if(Controller.mouseButtonDown(1)) {
			world.camera.x -= Controller.getMouseMovement().x;
			world.camera.y -= Controller.getMouseMovement().y;
		}
		
		// Begin game loop
		if(gameState == 0) { // Waiting for user input
			Direction direction;
			if (Controller.controlPressed("up")) {
				direction = Direction.NORTH;
			} else if (Controller.controlPressed("down")) {
				direction = Direction.SOUTH;
			} else if (Controller.controlPressed("left")) {
				direction = Direction.WEST;
			} else if (Controller.controlPressed("right")) {
				direction = Direction.EAST;
			} else {
				return;
			}
			if(board.slideTiles(direction)) {	// Only move to next step if slide did something
				gameState++;
			}
		} else if(gameState == 1) { // Waiting for tiles to finish moving
			if(!board.isSliding()) {
				gameState++;
			}
		} else if(gameState == 2){ // Adding a new tile
			board.addTile();
			if(!board.hasMove()) {
				world.add(new ScoreOverlay(board));
				gameState = 3;
			} else {
				gameState = 0;
			}
		} else if(gameState == 3) { // Waiting to restart game
			if(Controller.controlPressed("space")) {
				world.removeAll(world.getAllOfType(Entity.class));
				world.add(board = new Grid(board.width,board.height));
				gameState = 0;
			}
		}
	}
}
