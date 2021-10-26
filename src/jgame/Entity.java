package jgame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Entity {
	
	private static final Shape defaultShape = new Ellipse2D.Double(-10, -10, 20, 20);
		
	/**
	 * A number of options for rotation of an {@linkplain Entity}
	 * @see RotationMode#ROTATE
	 * @see RotationMode#FLIP
	 * @see RotationMode#NONE
	 */
	public enum RotationMode {
		/**
		 * {@linkplain Entity Entities} with this rotation mode will rotate normally, via
		 * {@linkplain AffineTransform#rotate(double)}
		 * @see RotationMode
		 */
		ROTATE,
		/**
		 * {@linkplain Entity Entities} with this rotation mode will be flipped horizontally
		 * to face toward their rotation.
		 * </br></br>
		 * More specifically, the Entity will be mirrored via {@linkplain AffineTransform#scale(double, double)
		 * AffineTransform.scale(-1,1)} when {@code Math.abs(rotation) > 90}
		 * @see RotationMode
		 */
		FLIP,
		/**
		 * {@linkplain Entity Entities} with this rotation mode ignore their rotation when drawing.
		 * @see RotationMode
		 */
		NONE
	}
	
	/**
	 * Controls how the {@linkplain Entity} rotates.
	 * @see Entity.RotationMode
	 */
	public RotationMode rotationMode = RotationMode.ROTATE;
	
	/** 
	 * The horizontal position of the {@linkplain Entity}.
	 * </br>Increasing values move the Entity rightwards.
	 */
	public double x;
	/** 
	 * The vertical position of the {@linkplain Entity}.
	 * </br>Increasing values move the Entity downwards.
	 */
	public double y;
	/** 
	 * The orientation of the {@linkplain Entity}, in degrees.
	 * </br>Increasing values rotate the Entity clockwise.
	 */
	public double rotation;
	
	/**
	 * The {@linkplain Sprite} used to draw the {@linkplain Entity} to the world.
	 */
	public Sprite sprite;
	
	/**
	 * The {@link Shape} of this {@link Entity} which is used for collisions, and, if no {@link
	 * Entity#sprite} is set, drawing to the {@link World}.
	 * </br>This shape should be centered about the point (0,0)
	 */
	public Shape shape = null;
	
	/**
	 * The {@linkplain Color} used to draw this {@linkplain Entity} to the {@linkplain World}
	 */
	protected Color color = Color.MAGENTA;
	
	/**
	 * Whether the {@linkplain Entity Entity's} {@linkplain Shape} should be drawn as an outline, or a solid block
	 */
	protected boolean fill = true;
	
	/**
	 * If {@linkplain Entity#fill fill} is {@code false}, sets the width of the line to draw. If {@code fill}
	 * is {@code true}, has no effect
	 */
	protected int lineWidth = 2;
	
	/**
	 * The {@linkplain World} containing this {@linkplain Entity}
	 */
	protected World world;
	
	/**
	 * This method is called when the {@linkplain World} first starts. Setup should include anything that needs
	 * to be done before the Entity first displays
	 * @see Entity#shape
	 * @see Entity#color
	 * @see Entity#fill
	 * @see Entity#lineWidth
	 * @see Entity#x
	 * @see Entity#y
	 * @see Entity#rotation
	 * @see Entity#rotationMode
	 */
	public abstract void setup();
	
	/**
	 * This method is called every time the {@linkplain World} updates, based on it's specified {@linkplain
	 * World#(double) framerate}. All game logic, like movement or combat, should be implemented here.
	 */
	public abstract void update();
	
	/**
	 * Returns an {@linkplain Entity} of the specified type which collides with this one,
	 * if one exists, else returns {@code null}.
	 * </br></br>
	 * Example usage: If {@code class Enemy extends Entity}, a colliding Enemy could be retrieved with
	 * </br>{@code Enemy e = collisionWithType(Enemy.class)}
	 * @param <T> The class to check for collisions with. Must be a subclass of Entity.
	 * @param type The class to check for a collision with. Must be a subclass of Entity.
	 * @return An Entity which overlaps this one, or {@code null} if there are none.
	 */
	public <T extends Entity> T collisionWithType(Class<T> type){
		for(T e : world.getAllOfType(type)) {
			if(this.collidesWith(e)) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Returns a {@linkplain List} of all {@linkplain Entity Entities} of the specified type which collide
	 *  with this one.
	 * </br></br>
	 * Example usage: If {@code class Enemy extends Entity}, all colliding Enemies could be retrieved with
	 * </br>{@code Enemy e = collisionsWithType(Enemy.class)}
	 * @param <T> The class to check for collisions with. Must be a subclass of Entity.
	 * @param type The class to check for a collision with. Must be a subclass of Entity.
	 * @return An List containing all Entities which overlap this one
	 */
	public <T extends Entity> List<T> collisionsWithType(Class<T> type) {
		return world.getAllOfType(type).stream()
				.filter(e->e.collidesWith(this))
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns whether this {@linkplain Entity} and the specified Entity are colliding, i.e. whether their
	 * {@linkplain Entity#shape shapes} overlap.
	 * @param other The entity to check for collision with
	 * @return Whether this entity and {@code other} are colliding.
	 */
	public boolean collidesWith(Entity other) {
		Area myArea = this.getCollisionArea();
		Area otherArea = other.getCollisionArea();
		if(myArea.getBounds2D().intersects(otherArea.getBounds2D())) {
			myArea.intersect(otherArea);
			return(!myArea.isEmpty());
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the direction, in degrees, toward the specified {@linkplain Entity}
	 * @param other The Entity to get the direction toward
	 * @return The direction toward that Entity
	 */
	public double directionTo(Entity other) {
		return Math.toDegrees(Math.atan2(other.y-y, other.x-x));
	}
	
	/**
	 * Returns the direction, in degrees, toward the specified {@linkplain Point}
	 * @param point The Point to get the direction toward
	 * @return The direction toward that point
	 */
	public double directionTo(Point point) {
		return Math.toDegrees(Math.atan2(point.y-y, point.x-x));
	}
	
	/**
	 * Returns the distance, from center to center, between this {@linkplain Entity} and the specified Entity
	 * @param other The Entity to check against
	 * @return The distance to that Entity
	 */
	public double distanceTo(Entity other) {
		double dx = other.x - x;
		double dy = other.y - y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Returns the distance from the center of this {@linkplain Entity} and the specified {@linkplain Point}
	 * @param point The Point to check against
	 * @return The distance to that Point
	 */
	public double distanceTo(Point point) {
		double dx = point.x - x;
		double dy = point.y - y;
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	/**
	 * Returns the {@linkplain AffineTransform} representing this {@linkplain Entity Entity's} position in the
	 * {@linkplain World}, including its position and rotation, depending on the {@linkplain Entity#rotationMode
	 *  rotation mode} of the entity
	 * @return This Entity's transform
	 */
	protected final AffineTransform getTransform() {
		AffineTransform out = new AffineTransform();
		out.translate(x, y);
		switch(rotationMode) {
		case ROTATE:
			out.rotate(this.getRadians());
			break;
		case FLIP:
			if(Math.abs(rotation) > 90) {
				out.scale(-1, 1);
			}
			break;
		case NONE:
			break;
		}
		return out;
	}
	
	/**
	 * Returns the {@linkplain Area} describing this {@linkplain Entity Entity's} {@linkplain Shape}, transformed to
	 * the correct position and orientation of the Entity
	 * @return This Entity's Area
	 */
	public final Area getCollisionArea() {
		return new Area(this.getTransform().createTransformedShape(this.getShape()));
	}
	
	public Shape getShape() {
		if(shape != null) {
			return shape;
		} else if(sprite != null) {
			return sprite.getShape();
		} else {
			return defaultShape;
		}
	}
	
	/**
	 * Applies the appropriate transform to the passed {@linkplain Graphics} object, then passed it to
	 * {@linkplain Entity#paint(Graphics)}.
	 * </br></br>
	 * Override this method if you want to control whether to respect the Entity's position, otherwise override
	 * {@linkplain Entity#paint(Graphics)}
	 * @param g The untransformed graphics to draw on
	 */
	public void rawPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.transform(this.getTransform());
		this.paint(g2);
		if(Game.debugMode) {
			Area hitArea = this.getCollisionArea();
			g2 = (Graphics2D)g.create();
			Color toFill = (color != null)?color:Color.RED;
			g2.setColor(toFill);
			g2.draw(hitArea);
			g2.setColor(new Color((toFill.getRGB()&0x00ffffff)|0x80000000,true));
			g2.setStroke(new BasicStroke(2));
			g2.fill(hitArea);
		}
	}
	
	/**
	 * Draws the {@linkplain Entity} to the given {@linkplain Graphics} object. This Graphics will already
	 * have the appropriate transformations applied, based on this Entity's x, y, rotation, and rotationMode
	 * </br></br>
	 * Overriding this method is the preferred way to change how an Entity draws.
	 * @param g The transformed graphics to draw on
	 */
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if(sprite != null) {
			BufferedImage image = sprite.getImage();
			g2.drawImage(image, -image.getWidth()/2, -image.getHeight()/2, null);
		} else if(shape != null) {
			g2.setColor(color);
			if(fill) {
				g2.fill(shape);
			} else {
				g2.setStroke(new BasicStroke(lineWidth));
				g2.draw(shape);
			}
		}
	}
	
	/**
	 * Returns the {@linkplain Entity#rotation rotation} of this {@linkplain Entity}, converted to radians
	 * for easier use with trigonometric functions and transformations.
	 * @return this Entity's rotation, in radians
	 */
	public double getRadians() {
		this.rotation = Direction.normalizeDegrees(rotation);
		return Math.toRadians(rotation);
	}
}
