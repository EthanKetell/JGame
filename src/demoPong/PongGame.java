package demoPong;

import jgame.Controller;
import jgame.Game;
import jgame.Images;
import jgame.World;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PongGame extends Game {
	
	static Paddle playerLeft, playerRight;
	
	public static void main(String[] args) {
		new PongGame().start();
	}
	
	@Override
	protected void setup() {
		setupKeyBinds();
		world.setSize(new Dimension(640,480));
		world.setFillPaint(Color.BLACK);
		world.setBackground(Color.DARK_GRAY);
		world.setZoomType(World.ZoomType.STRETCH);
		
		world.add(playerLeft = new Paddle(true));
		world.add(playerRight = new Paddle(false));
		world.add(new Score(playerLeft));
		world.add(new Score(playerRight));
		world.add(new Ball());
		playerLeft.score = playerRight.score = 9;
	}

	@Override
	protected void update() {
		if(playerLeft.score > 9 || playerRight.score > 9) {
			world.setFillSprite(Images.getSprite("pong_win_"+((playerLeft.score > 9)?"left":"right")));
			world.pause(true);
			playerLeft.score = 0;
			playerRight.score = 0;
		}
		if(!world.isRunning() && Controller.controlPressed("restart")) {
			world.setFillSprite(null);
			world.play();
		}
	}
	
	private void setupKeyBinds() {
		Controller.clearKeyBindings();
		// Left paddle controls
		Controller.addKeyBind("L_UP",	KeyEvent.VK_W);
		Controller.addKeyBind("L_DOWN",	KeyEvent.VK_S);
		// Right paddle controls
		Controller.addKeyBind("R_UP",	KeyEvent.VK_UP);
		Controller.addKeyBind("R_DOWN",	KeyEvent.VK_DOWN);
		// Restart after game over
		Controller.addKeyBind("restart", KeyEvent.VK_SPACE);
	}

}
