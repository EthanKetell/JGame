package jgame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class World {
	
	/**
	 * This is an {@linkplain Entity} for checking collisions with {@linkplain World} boundaries, via
	 * {@linkplain Entity#collisionWithType(Class) Entity.collisionWithType(World.Edge.class)}, or similar
	 * methods
	 */
	public class Edge extends Entity {
		
		/**
		 * The {@linkplain Direction} this wall occupies, e.g. the right edge of the world has the direction {@linkplain Direction#EAST EAST}
		 */
		public final Direction direction;

		private Edge(Direction direction, Dimension size) {
			this.color = Color.MAGENTA;
			this.fill = true;
			this.direction = direction;
			int large = 0x0fffffff;
			switch(direction) {
			case NORTH:
				this.shape = new Rectangle(-large/2, -(size.height/2+large), large, large);
				break;
			case SOUTH:
				this.shape = new Rectangle(-large/2, size.height/2, large, large);
				break;
			case WEST:
				this.shape = new Rectangle(-(size.width/2+large), -large/2, large, large);
				break;
			case EAST:
				this.shape = new Rectangle(size.width/2, -large/2, large, large);
				break;
			default:
				throw new IllegalArgumentException(direction.name() + " is not a valid direction for an edge.");			
			}
		}
		
		@Override
		public void rawPaint(Graphics g) {
//			super.rawPaint(g);
		}
		
		public void setup() {}

		public void update() {}
		
	}
	
	@SuppressWarnings("serial")
	private class WorldPanel extends JPanel {
		
		private Sprite bgImage;
		
		@Override
		public Dimension getPreferredSize() {
			if(frameSize != null) {
				return frameSize;
			} else if(size != null) {
				return size;
			} else {
				return super.getPreferredSize();
			}
		}
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			if(size != null) {
				g2.setPaint(bg);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
			g2.transform(getWorldTransform());
			
			if(size != null) {
				g2.setClip(-size.width/2, -size.height/2, size.width, size.height);
				g2.setPaint(fill);
				g2.fillRect(-size.width/2, -size.height/2, size.width, size.height);
			}
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if(bgImage != null) {
				g2.drawImage(bgImage.getImage(),-bgImage.getImage().getWidth()/2,-bgImage.getImage().getHeight()/2,null);
			}
				
			for(Entity e : entities) {
				e.rawPaint(g2);
			}
		}
	}
	
	/**
	 * This class handles the position of the camera. If the world's {@linkplain World#zoomType zoomType} is
	 * set to {@linkplain ZoomType#MANUAL}, the camera also controls zoom.
	 */
	public class Camera {
		
		private class PanManager {
			
			double fx, fy, dx, dy;
			
			PanManager() {
				
			}
			
			PanManager(double fx, double fy, double dx, double dy) {
				this.fx = fx;
				this.fy = fy;
				this.dx = dx;
				this.dy = dy;
			}
			
			PanManager panTo(double fx, double fy, double speed) {
				double movX = fx - x;
				double movY = fy - y;
				if(movX*movX + movY*movY < speed*speed) {
					x = fx;
					y = fy;
					return null;
				} else {
					double direction = Math.atan2(movY, movX);
					this.dx = Math.cos(direction)*speed;
					this.dy = Math.sin(direction)*speed;
					return new PanManager(fx,fy,dx,dy);
				}
			}
			
			boolean done() {
				if((Math.abs(fx-x) < Math.abs(dx)) && (Math.abs(fy-y) < Math.abs(dy))) {
					x = fx;
					y = fy;
					return true;
				} else {
					return false;
				}
			}
			
			void move() {
				x += dx;
				y += dy;
			}
		}

		public double x,y,rotation,sx,sy;
		private PanManager host, pan;
		
		private Camera() {
			x = 0;
			y = 0;
			sx = 1;
			sy = 1;
			rotation = 0;
			host = new PanManager();
		}
				
		/**
		 * Moves the camera to the specified position immediately
		 * @param x The horizontal position to move to
		 * @param y The vertical position to move to
		 * @see Camera#panTo(double, double, double)
		 */
		public void moveTo(double x, double y) {
			this.pan = null;
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Pans the camera to the specified position at the specified rate
		 * @param x The horizontal position to pan to
		 * @param y The vertical position to pan to
		 * @param speed The speed at which to pan
		 * @see Camera#moveTo(double, double)
		 */
		public void panTo(double x, double y, double speed) {
			pan = host.panTo(x, y, speed);
		}
		
		/**
		 * Sets the zoom for the camera. Note that this has no effect unless the {@linkplain World world's} {@linkplain World#zoomType zoomType}
		 * is set to {@linkplain ZoomType#MANUAL}
		 * @param sx The horizontal scale
		 * @param sy The vertical scale
		 */
		public void setZoom(double sx, double sy) {
			this.sx = sx;
			this.sy = sy;
		}
		
		/**
		 * Sets the zoom for the camera. Note that this has no effect unless the {@linkplain World world's} {@linkplain World#zoomType zoomType}
		 * is set to {@linkplain ZoomType#MANUAL}
		 * @param scale The zoom for the camera
		 */
		public void setZoom(double scale) {
			this.sx = scale;
			this.sy = scale;
		}
		
		private void update() {
			rotation = Direction.normalizeDegrees(rotation);
			if(pan != null) {
				if(pan.done()) {
					pan = null;
				} else {
					pan.move();
				}
			}
		}
		
		private AffineTransform getTransform() {
			AffineTransform at = new AffineTransform();
			at.translate(-x, -y);
			at.rotate(Math.toRadians(rotation));
			return at;
		}
	}
	
	JFrame frame;
	Paint 	fill = Color.DARK_GRAY,
			bg = Color.BLACK;
	private WorldPanel panel;
	
	Dimension frameSize, size;
	Point mouse = new Point();
	ZoomType zoomType = ZoomType.MANUAL;
	
	private ArrayList<Entity>
			entities 	= new ArrayList<Entity>(),
			toAdd		= new ArrayList<Entity>(),
			toRemove	= new ArrayList<Entity>();
	private ArrayList<Edge> bounds = null;
	
	public Camera camera = new Camera();
	
	public World() {
		setupSwingComponents();
	}
	
	private final void setupSwingComponents() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);
		panel = new WorldPanel();
		panel.setOpaque(false);
		panel.setBackground(Color.BLACK);
		panel.addMouseListener(Controller.listener);
		panel.addMouseMotionListener(Controller.listener);
		panel.addMouseWheelListener(Controller.listener);
		panel.addKeyListener(Controller.listener);
		panel.setFocusable(true);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	/**
	 * Sets the {@linkplain Paint} to be used filling the parts of the window not covered by the {@linkplain World}.
	 * </br></br>
	 * Note that {@linkplain Color} implements Paint
	 * @param bg The desired background paint
	 */
	public void setBackground(Paint bg) {
		this.bg = bg;
	}
	
	/**
	 * Sets the {@linkplain Paint} to be used when drawing the {@linkplain World}
	 * </br></br>
	 * Note that {@linkplain Color} implements Paint
	 * @param bg The desired background paint
	 */
	public void setFillPaint(Paint fill) {
		this.fill = fill;
	}
	
	/**
	 * Sets a {@linkplain Sprite} to be displayed when drawing the {@linkplain World}. This sprite will be drawn
	 * centered at (0,0)
	 * @param fill The desired image
	 */
	public void setFillSprite(Sprite fill) {
		panel.bgImage = fill;
	}
	
	/**
	 * Returns the current background color
	 * @return The current background color
	 */
	public Color getBackgroundColor() {
		return panel.getBackground();
	}
	
	/**
	 * Returns the {@linkplain Sprite} currently being used as a background image for this {@linkplain World}
	 * @return The current background image
	 */
	public Sprite getBackgroundSprite() {
		return panel.bgImage;
	}
	
	/**
	 * Returns a list of all {@linkplain Entity Entities} in the world which extend the specified class.
	 * </br></br>
	 * Example usage: If {@code class Enemy extends Entity}, all Enemies could be collected via
	 * </br>{@code List<Enemy> enemies = world.getAllOfType(Enemy.class)}
	 * @param <T> The class to collect. Must be a subclass of Entity
	 * @param type The class to collect. Must be a subclass of Entity 
	 * @return A {@linkplain List} containing all the Entities in the world which
	 * are of class type or a subclass of type
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> List<T> getAllOfType(Class<T> type){
		if(type.equals(Edge.class)) {
			if(bounds != null) {
				return (List<T>)this.bounds;
			} else {
				return new ArrayList<T>(0);
			}
		}
		return (List<T>)
				entities.stream()
				.filter(e->type.isAssignableFrom(e.getClass()))
				.collect(Collectors.toList());
	}

	/**
	 * Adds the specified {@linkplain Entity} to the world at the point (0,0)
	 * @param e The Entity to add
	 */
	public void add(Entity e) {
		add(e,0,0);
	}
	
	/**
	 * Adds the specified {@linkplain Entity} to the world at the specified point.
	 * @param e The Entity to add
	 * @param x
	 * @param y
	 */
	public void add(Entity e, double x, double y) {
		e.x = x;
		e.y = y;
		e.world = this;
		e.setup();
		toAdd.add(e);
	}
	
	/**
	 * Removes the specified {@linkplain Entity} from the {@linkplain World}
	 * @param e The Entity to remove
	 */
	public void remove(Entity e) {
		toRemove.add(e);
	}

	/**
	 * Returns the {@linkplain JFrame} this {@linkplain World} is in.
	 * @return This world's host frame
	 */
	public JFrame getFrame() {
		return frame;
	}
	
	void paint() {
		panel.repaint();
	}
	
	void update() {
		entities.removeAll(toRemove);
		entities.addAll(toAdd);
		toAdd.clear();
		toRemove.clear();
		
		mouse = convertToWorldCoordinates(Controller.getMouse());
		
		if(Game.debugMode) {
			System.out.println("Updating "+entities.size()+((entities.size()==1)?" entity":" entities"));
		}
		
		for(Entity e : entities) {
			e.update();
		}
		camera.update();
	}
	
	private AffineTransform getWorldTransform() {
		AffineTransform transform = new AffineTransform();
		transform.translate(panel.getWidth()/2,panel.getHeight()/2);
		transform.concatenate(camera.getTransform());
		
		double sx, sy;
		
		if(size != null) {
			sx = panel.getWidth()/size.getWidth();
			sy = panel.getHeight()/size.getHeight();
		} else {
			sx = panel.getWidth()/panel.getPreferredSize().getWidth();
			sy = panel.getHeight()/panel.getPreferredSize().getHeight();
		}
		
		switch(zoomType) {
		case FILL:
			transform.scale(Math.max(sx,sy), Math.max(sx,sy));
			break;
		case LETTERBOX:
			transform.scale(Math.min(sx,sy), Math.min(sx,sy));
			break;
		case MANUAL:
			transform.scale(camera.sx, camera.sy);
			break;
		case STRETCH:
			transform.scale(sx, sy);
			break;
		}
		return transform;
	}
	
	/**
	 * Converts a {@linkplain Point} on the window (such as from {@linkplain Controller#getMouse()}) to a point in the {@linkplain World}
	 * @param p The point to convert, relative to the window
	 * @return A point relative to the world
	 */
	public Point convertToWorldCoordinates(Point p) {
		Point2D pt = new Point2D.Double();
		try {
			pt = getWorldTransform().inverseTransform(p, null);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return new Point((int)pt.getX(),(int)pt.getY());
	}
	
	public Point getMouse() {
		return mouse;
	}
	
	/**
	 * A number of options for scaling the World to fill the window. Note that these options control zoom, but the
	 * {@linkplain Camera} can still be moved
	 * @see ZoomType#STRETCH
	 * @see ZoomType#LETTERBOX
	 * @see ZoomType#FILL
	 * @see ZoomType#CENTER
	 */
	public static enum ZoomType {
		/** The world will be stretched to fill the window exactly */
		STRETCH,
		/** The world will be scaled to the maximum size that fits in the window, using the background to draw letterbox */
		LETTERBOX,
		/** The world will be scaled to fill the screen, clipping parts that do not fit */
		FILL,
		/** The world will not be scaled automatically. Instead, change {@linkplain Camera#zoom} */
		MANUAL;
	}
	/**
	 * Sets the size of the world. A size of {@code null} will remove bounds for the world.
	 * @param size The size of the world
	 */
	public void setSize(Dimension size) {
		if(size != null) {
			bounds = new ArrayList<Edge>(4);
			bounds.add(new Edge(Direction.NORTH, size));
			bounds.add(new Edge(Direction.SOUTH, size));
			bounds.add(new Edge(Direction.EAST,  size));
			bounds.add(new Edge(Direction.WEST,  size));
		}
		this.size = size;
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	/**
	 * Returns the size of the {@linkplain World}. Note that this is not necessarily the size of the window.
	 * @return The size of the world, if one has been assigned, else {@code null}
	 */
	public Dimension getSize() {
		return size;
	}
	
	/**
	 * Sets the {@linkplain ZoomType} the world uses for scaling when the window is resized
	 * @param zoom The desired method for auto-zooming
	 */
	public void setZoomType(ZoomType zoom) {
		this.zoomType = zoom;
	}

}
