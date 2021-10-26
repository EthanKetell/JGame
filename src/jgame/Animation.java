package jgame;

import java.awt.image.BufferedImage;

public class Animation extends Sprite {
	
	private final BufferedImage[] frames;
	private int index;
	private final long delay;
	private long lastUpdate;

	
	/**
	 * Creates an {@linkplain Animation} object with the given frames, and the specified framerate. The Animation will
	 * cycle through the images of the array, in order, returning the appropriate frame based on the system time.
	 * @param sheet An array containing the frames to be played by the animation
	 * @param fps The desired framerate for the Animation to play
	 * @see Images#divideSpriteSheet(BufferedImage,int,int)
	 */
	public Animation(Sprite sheet, double fps) {
		this(sheet, fps, 0, sheet.rows*sheet.cols);
	}
	
	/**
	 * Creates an {@linkplain Animation} object from the given {@linkplain Sprite}, within the range of {@code start}, inclusive,
	 * to {@code end}, exclusive, and the specified framerate. The Animation will cycle through the sub-images of the Sprite, in order,
	 * returning the appropriate frame based on the system time.
	 * @param sheet The sprite from which to retrieve frames
	 * @param fps The desired framerate for the Animation to play
	 * @param start the initial index of the Animation, inclusive
	 * @param end the final index of Animation, exclusive
	 */
	public Animation(Sprite sheet, double fps, int start, int end) {
		super(sheet.getImage(0));
		this.frames = new BufferedImage[end-start];
		for(int i = 0; i < frames.length; i++) {
			frames[i] = sheet.getImage(start+i);
		}
		this.delay = Math.round(1000/fps);
		this.index = 0;
		this.lastUpdate = System.currentTimeMillis();
	}
	
	/**
	 * Returns the {@linkplain BufferedImage} for the current frame of the {@linkplain Animation}, which is
	 * calculated using the framerate specified during creation of the Animation and the current 
	 * {@linkplain System#currentTimeMillis() system time}.
	 * @return The current frame of the animation
	 */
	public BufferedImage getImage() {
		long now = System.currentTimeMillis();
		if(now - lastUpdate > delay) {
			index = (index + 1) % frames.length;
			lastUpdate = now;
		}
		return frames[index];
	}
	
	/**
	 * Returns the specified frame from the {@linkplain Animation}
	 * @param frame the index of the frame to retrieve
	 * @return the {@linkplain BufferedImage} for the frame
	 */
	public BufferedImage getImage(int frame) {
		return frames[frame];
	}
	
}
