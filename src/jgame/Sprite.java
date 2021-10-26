package jgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;

public class Sprite implements Iterable<Sprite> {
	
	private final BufferedImage image;
	private Shape hitbox;
	protected final int rows, cols;
	private final Sprite[] frames;
	
	/**
	 * Creates a {@linkplain Sprite} with the given image
	 * @param image The {@linkplain BufferedImage} for this sprite
	 */
	public Sprite(BufferedImage image) {
		this(image,1,1);
	}
	
	/**
	 * Creates a {@linkplain Sprite} with the given image, divided into the specified number
	 * of rows and columns for getting sub-images with {@linkplain Sprite#getImage(int)}
	 * @param image The {@linkplain BufferedImage} for this sprite
	 * @param rows The number of horizontal divisions
	 * @param cols The number of vertical divisions
	 */
	public Sprite(BufferedImage image, int rows, int cols) {
		if(rows < 1 || cols < 1) {
			throw new IllegalArgumentException("Sprite must have at least one row and one column");
		}
		this.image = image;
		this.rows = rows;
		this.cols = cols;
		this.frames = new Sprite[rows*cols];
		if(frames.length > 1) {
			BufferedImage[] frames = Images.divideSpriteSheet(image, rows, cols);
			for(int i = 0; i < frames.length; i++) {
				this.frames[i] = new Sprite(frames[i]);
			}
		} else {
			frames[0] = this;
		}
	}
	
	/**
	 * Returns the {@linkplain BufferedImage} for the sprite.
	 * @return the BufferedImage for the sprite
	 */
	public BufferedImage getImage() {
		return getImage(0);
	}
	
	/**
	 * Returns the sub-image of this {@linkplain Sprite Sprite's} {@linkplain BufferedImage}. Indices are
	 * indexed with zero at the top left corner of the image, progressing left to right, then top to bottom.
	 * Size and number available are determined by the constructor {@linkplain Sprite#Sprite(BufferedImage, int, int)}
	 * @param index The index of the sub-image to get
	 * @return The sub-image at the specified index
	 */
	public BufferedImage getImage(int index) {
		return frames[index].image;
	}
	
	/**
	 * Returns the {@linkplain Sprite} which is a division of this Sprite at the specified {@code index}. Indices
	 * are indexed with zero at the top left corner of the image, progressing left to right, then top to bottom.
	 * Size and number available are determined by the constructor {@linkplain Sprite#Sprite(BufferedImage, int, int)}
	 * @param index The index of the Sprite to retrieve
	 * @return The specified Sprite
	 */
	public Sprite getSprite(int index) {
		return frames[index];
	}
	
	/**
	 * Returns the hitbox for this {@linkplain Sprite}. If it has not been set using {@linkplain Sprite#setHitbox(Shape)},
	 * returns a {@linkplain Rectangle} describing the size of this sprite's image.
	 * @return The {@linkplain Shape} representing this sprite's hitbox
	 */
	public Shape getShape() {
		if(hitbox == null) {
			return new Rectangle(-image.getWidth()/2, -image.getHeight()/2, image.getWidth(), image.getHeight());
		} else {
			return this.hitbox;
		}
	}
	
	/**
	 * Sets the hitbox for this {@linkplain Sprite}
	 * @param shape The shape to use as a hitbox
	 */
	public void setHitbox(Shape shape) {
		this.hitbox = shape;
	}
	
	/**
	 * Returns a {@linkplain Dimension} describing the size of the {@linkplain Sprite}
	 * @return The Dimension of the sprite
	 */
	public Dimension getSize() {
		BufferedImage image = this.getImage();
		return new Dimension(image.getWidth(), image.getHeight());
	}

	@Override
	public Iterator<Sprite> iterator() {
		return Arrays.asList(frames).iterator();
	}
	
}
