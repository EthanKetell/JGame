package demoPong;

import jgame.Entity;
import jgame.Images;
import jgame.Sprite;

public class Score extends Entity {
	
	/** Whether the score is the left one. If not, it's probably on the right */
	boolean onLeft;
	
	/** Sprite containing the digits 0-9 */
	Sprite digits = Images.getSprite("pong_digits", 2, 5);
	
	/** The paddle whose score is displayed */
	Paddle host;
	
	public Score(Paddle host) {
		this.onLeft = host.onLeft;
		this.host = host;
	}
	
	@Override
	public void setup() {
		this.sprite = digits.getSprite(0);
		this.x = (onLeft?-1:1)*world.getSize().getWidth()/4;
		this.y = 40-world.getSize().height/2.0;
	}

	@Override
	public void update() {
		this.sprite = digits.getSprite(host.score);
	}

}
