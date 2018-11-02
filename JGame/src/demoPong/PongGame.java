package demoPong;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import jgame.*;

public class PongGame extends Game {
	
	static Paddle playerLeft, playerRight;
	
	public static void main(String[] args) {
		new PongGame().start();
//		Images.manage();
	}
	
	@Override
	protected void setup() {
		setupKeyBinds();
		world.setSize(new Dimension(640,480));
		world.setFillPaint(Color.BLACK);
		world.setBackground(Color.DARK_GRAY);
		world.setZoomType(World.ZoomType.LETTERBOX);
		
		world.add(playerLeft = new Paddle(true));
		world.add(playerRight = new Paddle(false));
		world.add(new Score(playerLeft));
		world.add(new Score(playerRight));
		world.add(new Ball());
	}

	@Override
	protected void update() {
		// TODO Auto-generated method stub

	}
	
	private void setupKeyBinds() {
		Controller.clearKeyBindings();
		// Left paddle controls
		Controller.addKeyBind("L_UP",	KeyEvent.VK_W);
		Controller.addKeyBind("L_DOWN",	KeyEvent.VK_S);
		// Right paddle controls
		Controller.addKeyBind("R_UP",	KeyEvent.VK_UP);
		Controller.addKeyBind("R_DOWN",	KeyEvent.VK_DOWN);
	}

}
