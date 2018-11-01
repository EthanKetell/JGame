package demo;
import java.awt.Dimension;

import jgame.Game;
import jgame.Images;
import jgame.World;

public class AsteroidGame extends Game {

	public static void main(String[] args) {
		Game.debugMode = true;
		new AsteroidGame().start();
//		Images.manage();
	}

	@Override
	protected void setup() {
		world.setSize(new Dimension(640, 480));
		world.setZoomType(World.ZoomType.LETTERBOX);
		world.setBackgroundSprite(Images.getSprite("stars"));
		world.add(new Player());
	}

	@Override
	protected void update() {
		
	}
	
}
