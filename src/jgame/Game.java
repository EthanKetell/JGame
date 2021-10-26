package jgame;

import javax.swing.*;
import java.awt.*;

public abstract class Game {
	
	/**
	 * Controls a number of functionalities, such as drawing the collision area for {@linkplain Entity Entities}, and
	 * whether or not {@linkplain Game#debugPrint(Object)} and {@linkplain Game#debugWarn(Object)} print
	 */
	public static boolean debugMode = false;
	
	/**
	 * If {@linkplain Game#debugMode} is {@code true}, prints the specified message to {@code System.out},
	 * otherwise does nothing.
	 * @param o The message to print
	 */
	public static void debugPrint(Object o) {
		if(debugMode) {
			System.out.println(o);
		}
	}
	
	/**
	 * If {@linkplain Game#debugMode} is {@code true}, prints the specified message to {@code System.err},
	 * otherwise does nothing.
	 * @param o The message to print
	 */
	public static void debugWarn(Object o) {
		if(debugMode) {
			System.err.println(o);
		}
	}
	
	/**
	 * The {@linkplain World} for this game, which contains all the game's {@linkplain Entity Entities}, and
	 * manages their updates and drawing.
	 */
	public World world;
	
	private final JFrame frame;
	private Timer gameClock;
	private boolean setupDone;
	
	public Game() {
		world = new World();
		frame = world.getFrame();
	}
	
	/**
	 * Sets the size for the game window.
	 * @param size The desired size for the window
	 */
	public final void setWindowSize(Dimension size) {
		world.frameSize = size;
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	/**
	 * Shows or hides the game window
	 * @param visible The desired visibility of the window
	 */
	public final void setVisible(boolean visible) {
		if(!setupDone) {
			setupDone = true;
			setup();
		}
		frame.setVisible(visible);
	}
	
	/**
	 * Starts the game
	 */
	public final void start() {
		if(gameClock == null) {
			this.setupTimer();
		}
		setVisible(true);
		gameClock.start();
	}
	
	private void setupTimer() {
		gameClock = new Timer(20,event->{
			Controller.refresh();
			this.update();
			world.update();
			world.paint();
		});
	}
	
	/**
	 * Sets the framerate for the game, which controls how often update is called on the game and all it's entities.
	 * If unspecified, defaults to 50 frames per second.
	 * @param fps The desired number of updates per second
	 */
	public final void setFramerate(double fps) {
		if(gameClock == null) {
			this.setupTimer();
		}
		int delay;
		if(fps > 0) {
			delay = (int)(1000/fps);
		} else {
			delay = Integer.MAX_VALUE;
		}
		gameClock.setDelay(delay);
	}
	
	/**
	 * Called only once, when the game first starts. This should be where you initialize the size, framerate,
	 * background color, etc of the game, and add any Entities which should be in the game at the beginning.
	 */
	protected abstract void setup();
	
	/**
	 * This is called repeatedly, with a frequency based on the {@linkplain Game#setFramerate(double) framerate}
	 * of the game. This is where any game logic (like Entity spawning, movement, or collisions) should happen.
	 */
	protected abstract void update();

}
