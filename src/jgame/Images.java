package jgame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;

public class Images {

	private static final Map<String, BufferedImage> images = new HashMap<>();

	/**
	 * Returns the {@linkplain BufferedImage} with the given name
	 * @param file The name of the image to get
	 * @return The image with the given name
	 */
	public static BufferedImage getImage(String file) {
		if(images.containsKey(file)) {
			return images.get(file);
		} else {
			InputStream input = ClassLoader.getSystemResourceAsStream(file);
			if(input != null) {
				try {
					BufferedImage image = ImageIO.read(input);
					images.put(file, image);
				} catch (IOException e) {
					System.err.println("Failed to read file: "+file);
					e.printStackTrace();
				}
			} else {
				System.err.println("Could not find: "+file);
			}
		}
		return images.get(file);
	}
	
	/**
	 * Returns the {@linkplain Sprite} with the given name
	 * @param name The name of the Sprite to get
	 * @return The Sprite with the given name
	 */
	public static Sprite getSprite(String name) {
		return new Sprite(getImage(name));
	}
	
	/**
	 * Returns the {@linkplain Sprite} with the given name, divided for use with {@linkplain Animation Animations}
	 * or {@linkplain Sprite#getImage(int)}
	 * @param name The name of the Sprite to get
	 * @param rows The number of horizontal divisions
	 * @param cols The number of vertical divisions
	 * @return The Sprite with the given name, or {@code null} if the name is not found
	 */
	public static Sprite getSprite(String name, int rows, int cols) {
		return new Sprite(getImage(name), rows, cols);
	}

	/**
	 * Rotates an image counter-clockwise
	 * @param image The {@link BufferedImage} to rotate
	 * @param radians The angle to rotate by
	 * @return A rotated image
	 */
	public static BufferedImage rotateImage(BufferedImage image, double radians) {
		int newWidth = (int)(image.getHeight()*Math.abs(Math.sin(radians))+image.getWidth()*Math.abs(Math.cos(radians)));
		int newHeight = (int)(image.getHeight()*Math.abs(Math.cos(radians))+image.getWidth()*Math.abs(Math.sin(radians)));
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)out.getGraphics();
		AffineTransform transform = new AffineTransform();
		transform.translate((newWidth-image.getWidth())/2.0, (newHeight-image.getHeight())/2.0);
		transform.rotate(radians, image.getWidth()/2.0, image.getHeight()/2.0);
		
		g.setTransform(transform);
		g.drawImage(image,0,0, null);
		g.dispose();
		return out;
	}
	
	/**
	 * Scales an image
	 * @param image The {@link BufferedImage} to scale
	 * @param scale The multiple by which to scale the image
	 * @return A scaled instance of the image
	 */
	public static BufferedImage scaleImage(BufferedImage image, double scale) {
		if(scale == 1) {
			return image;
		}
		int newWidth = (int) Math.ceil(image.getWidth()*scale);
		int newHeight = (int)(Math.ceil(image.getHeight()*scale));
		BufferedImage out = new BufferedImage(newWidth,newHeight,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)out.getGraphics();
		g.scale(scale, scale);
		g.drawImage(image,0,0,null);
		g.dispose();
		return out;
	}
	
	/**
	 * Returns an instance of the given {@link BufferedImage} transformed
	 * by the given {@code degrees}, {@code sx}, and {@code sy}
	 * @param image The image to transform
	 * @param radians The angle to rotate the image by
	 * @param sx The factor to scale the width of the image by
	 * @param sy The factor to scale the height of the image by
	 * @return A copy of {@link BufferedImage image} with the transformations applied
	 */
	public static BufferedImage transformImage(BufferedImage image, double radians, double sx, double sy) {
		int scaleWidth = (int)(image.getWidth() * sx);
		int scaleHeight = (int)(image.getHeight() * sy);
		int newWidth = (int)(scaleHeight*Math.abs(Math.sin(radians))+scaleWidth*Math.abs(Math.cos(radians)));
		int newHeight = (int)(scaleHeight*Math.abs(Math.cos(radians))+scaleWidth*Math.abs(Math.sin(radians)));
		
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		
		AffineTransform transform = new AffineTransform();
		transform.translate((newWidth-scaleWidth)/2.0, (newHeight-scaleHeight)/2.0);
		transform.rotate(radians, scaleWidth/2.0, scaleHeight/2.0);
		transform.scale(sx, sy);
		
		Graphics2D g2 = (Graphics2D)out.getGraphics();
		g2.setTransform(transform);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
		return out;
	}
	
	/**
	 * Splits a {@linkplain BufferedImage} into an array of sub-images by dividing the
	 * image into a grid with the specified number of rows and columns
	 * @param image The BufferedImage to divide
	 * @param rows The number of horizontal divisions
	 * @param cols The number of vertical divisions
	 * @return An array of sub-images
	 */
	public static BufferedImage[] divideSpriteSheet(BufferedImage image, int rows, int cols) {
		BufferedImage[] out = new BufferedImage[rows*cols];
		int frameWidth = image.getWidth()/cols;
		int frameHeight = image.getHeight()/rows;
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < cols; x++) {
				out[cols*y + x] = image.getSubimage(x*frameWidth, y*frameHeight, frameWidth, frameHeight);
			}
		}
		return out;
	}
}
