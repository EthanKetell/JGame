package demo2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import jgame.*;

/** Simple entity for fading out grid and displaying score after game over */
public class ScoreOverlay extends Entity {
	
	private static final Font restartMessageFont = NumberGame.font.deriveFont(NumberGame.font.getSize()/4f);
	
	Grid host;
	
	public ScoreOverlay(Grid host) {
		this.host = host;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;

		g2.setFont(NumberGame.font);
		FontMetrics metrics = g.getFontMetrics();
	    int y = metrics.getAscent() - metrics.getHeight();
	    g2.setColor(Color.BLACK);
	    
	    String text = "Game Over!";
	    int x = -metrics.stringWidth(text) / 2;
	    g2.drawString(text, x, y);
	    
	    y += metrics.getHeight();
	    text = "Score: "+host.score;
	    x = -metrics.stringWidth(text) / 2;
	    g2.drawString(text, x, y);
	    
	    g2.setFont(restartMessageFont);
	    metrics = g.getFontMetrics();
	    
	    y += 2*metrics.getHeight();
	    text = "Press SPACE to restart";
	    x = -metrics.stringWidth(text) / 2;
	    g2.drawString(text, x, y);
	}

	@Override
	public void setup() {
		this.shape = new RoundRectangle2D.Double(-host.size.width/2, -host.size.height/2, host.size.width, host.size.height, 20, 20);
		this.color = new Color(0x80FAF8EF,true);
	}

	@Override
	public void update() {

	}

}
