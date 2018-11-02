package jgame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("unchecked")
public class Images {
	
	private static class ImagePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static TexturePaint tex;
		BufferedImage activeImage;
		
		double scale = 1, rotation = 0;
		
		static {
			BufferedImage texBase = new BufferedImage(10,10,BufferedImage.TYPE_INT_ARGB);
			Graphics g = texBase.getGraphics();
			g.setColor(new Color(0xAAAAAA));
			g.fillRect(0, 0, 10, 10);
			g.setColor(new Color(0xCCCCCC));
			g.fillRect(0, 0, 5, 5);
			g.fillRect(5, 5, 10, 10);
			g.dispose();
			tex = new TexturePaint(texBase, new Rectangle2D.Double(0,0,10,10));
		}
		
		public ImagePanel() {
			super();
			this.setPreferredSize(new Dimension(0,0));
			this.setMinimumSize(new Dimension(0,0));
		}
		
		public void setActiveImage(BufferedImage newImage) {
			activeImage = newImage;
			if(newImage != null) {
				this.setPreferredSize(new Dimension(newImage.getWidth(),newImage.getHeight()));
			} else {
				this.setPreferredSize(new Dimension(0,0));
			}
			this.setMinimumSize(this.getPreferredSize());
			this.repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setPaint(tex);
			g2.fillRect(0, 0, getWidth(), getHeight());
			
			g2.translate(this.getWidth()/2, this.getHeight()/2);
			g2.rotate(rotation);
			g2.scale(scale, scale);
			
			if(activeImage != null) {
				g2.drawImage(
						activeImage,
						-activeImage.getWidth()/2,
						-activeImage.getHeight()/2,
						null);
			}
		}
	}
	private static Map<String, String> filePaths;
	private static Map<String, BufferedImage> images;
	private static String imageFolder = "res"+File.separator+"images"+File.separator;
	
	static {		//Retrieve saved image map
		File f = new File(imageFolder);
		if(!f.exists()) f.mkdirs();
		images = new HashMap<String, BufferedImage>();
		f = new File("res"+File.separator+"images"+File.separator+"images.dat");
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			filePaths = (HashMap<String,String>)ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			Game.debugPrint("Could not find \'"+f.getPath()+"\'");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("\'"+f.getPath()+"\' seems to be corrupt. Fix or delete it.");
			e.printStackTrace();
		}
		if(filePaths == null) {
			Game.debugPrint("Saved imagename Map is null, creating a new one");
			filePaths = new HashMap<String,String>();
		}
		for(String name : filePaths.keySet()) {
			retrieveImage(name, localizePath(filePaths.get(name)));
		}
	}
	
	/**
	 * Opens the image manager, allowing you to add images to the project.
	 * @see Images#getSprite(String)
	 */
	public static void manage() {
		
		// Create the JFrame to store all components
		
		JFrame frame = new JFrame("Sprite Manager");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				Images.save();
			}
		});
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		
		DefaultListModel<String> imageNames = new DefaultListModel<String>();
		for(String name : Images.getSpriteNames()) {
			imageNames.addElement(name);
		}
		
		JList<String> listPane = new JList<String>(imageNames);
		
		JScrollPane listScroll = new JScrollPane();
		listScroll.getViewport().add(listPane);
		listScroll.setPreferredSize(new Dimension(60,240));
		leftPanel.add(listScroll);
		Box controlBox = Box.createHorizontalBox();
		JButton addButton = new JButton("+");
		JButton removeButton = new JButton("-");
		
		addButton.addActionListener(e->{
				openAddImageWindow(listPane);
			});
		
		removeButton.addActionListener(e->{
				List<String> names = listPane.getSelectedValuesList();
				int response;
				if(names.size() == 0) {
					return;
				} else {
					if(names.size() == 1) {
						response = JOptionPane.showConfirmDialog(frame,"Are you sure you want to remove \'"+names.get(0)+"\'?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
					} else {
						String selection = "";
						for(String name : names) {
							selection = selection + name + ((names.indexOf(name)<names.size()-1)?", ":"");
						}
						response = JOptionPane.showConfirmDialog(frame,"Are you sure you want to remove all of the following?\n\n"+selection, "Confirm Deletion", JOptionPane.YES_NO_OPTION);
					}
					if(response == JOptionPane.YES_OPTION) {
						for(String name : names) {
							Images.removeImage(name);
							imageNames.removeElementAt(imageNames.indexOf(name));
						}
						listPane.setSelectedIndex(-1);
					}
				}
			});
		
		
		
		controlBox.add(removeButton);
		controlBox.add(Box.createHorizontalGlue());
		controlBox.add(addButton);
		leftPanel.add(controlBox);
		
		listScroll.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,5,5,5),
				BorderFactory.createLoweredBevelBorder()));
				
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		ImagePanel imageDisplay = new ImagePanel();
		JScrollPane imageScroll = new JScrollPane();
		imageScroll.setPreferredSize(new Dimension(240,240));
		imageScroll.getViewport().add(imageDisplay);
		imageScroll.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(5,5,5,5),
				BorderFactory.createLoweredBevelBorder()));
		rightPanel.add(imageScroll);
		
		controlBox = Box.createHorizontalBox();
		controlBox.add(Box.createHorizontalGlue());
		
		Box control = Box.createVerticalBox();
		JLabel label = new JLabel("rotation");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		control.add(label);
		JSlider rotSlider = new JSlider(-4,4);
		control.add(rotSlider);
		
		controlBox.add(control);
		controlBox.add(Box.createHorizontalGlue());
		
		control = Box.createVerticalBox();
		label = new JLabel("scale");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		control.add(label);
		JSlider scaleSlider = new JSlider(-100,100);
		control.add(scaleSlider);
		
		controlBox.add(control);
		controlBox.add(Box.createHorizontalGlue());
		
		rightPanel.add(controlBox);
		
		rotSlider.setSnapToTicks(true);
		rotSlider.setPaintTicks(true);
		rotSlider.setMajorTickSpacing(1);
		rotSlider.addChangeListener(e->{
			imageDisplay.rotation = rotSlider.getValue()*Math.PI/2;
			imageDisplay.repaint();
		});
		rotSlider.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				String name = listPane.getSelectedValue();
				if(name != null) {
					images.put(name, Images.rotateImage(images.get(name), imageDisplay.rotation));
					imageDisplay.rotation = 0;
					imageDisplay.setActiveImage(images.get(name));
					imageScroll.getViewport().revalidate();
				}
				rotSlider.setValue(0);
			}
		});
		
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setMajorTickSpacing(10);
		scaleSlider.addChangeListener(e->{
			imageDisplay.scale = Math.pow(2, scaleSlider.getValue()/50.0);
			imageDisplay.repaint();
		});
		scaleSlider.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				String name = listPane.getSelectedValue();
				if(name != null) {
					images.put(name, Images.scaleImage(images.get(name), imageDisplay.scale));
					imageDisplay.scale = 1;
					imageDisplay.setActiveImage(images.get(name));
					imageScroll.getViewport().revalidate();
				}
				scaleSlider.setValue(0);
			}
		});
		
		listPane.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String name = listPane.getSelectedValue();
				if(name != null) {
					imageDisplay.setActiveImage(images.get(name));
				} else {
					imageDisplay.setActiveImage(null);
				}
				imageScroll.getViewport().revalidate();
			}
		});
		
		listScroll.setBackground(frame.getBackground());
		imageScroll.setBackground(frame.getBackground());
		
		JSplitPane mainPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,rightPanel);
		mainPanel.setContinuousLayout(true);
		mainPanel.setDividerSize(3);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		frame.add(mainPanel);
		
		frame.pack();
		frame.setMinimumSize(frame.getSize());
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static void openAddImageWindow(JList<String> nameList) {
		JFrame frame = new JFrame("Import new image");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel host = new JPanel();
		host.setLayout(new BoxLayout(host,BoxLayout.Y_AXIS));
		host.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		frame.add(host);
		
		JPanel nameEntry = new JPanel();
		nameEntry.setLayout(new BoxLayout(nameEntry,BoxLayout.X_AXIS));
		JTextField nameEntryField = new JTextField();
		JLabel nameLabel = new JLabel("Name: ");
		nameEntry.add(nameLabel);
		nameEntry.add(Box.createHorizontalGlue());
		nameEntry.add(nameEntryField);
		
		JPanel pathEntry = new JPanel();
		pathEntry.setLayout(new BoxLayout(pathEntry,BoxLayout.X_AXIS));
		JTextField pathEntryField = new JTextField();
		pathEntryField.setEditable(false);
		JLabel pathLabel = new JLabel("Path: ");
		pathEntry.add(pathLabel);
		pathEntry.add(Box.createHorizontalGlue());
		pathEntry.add(pathEntryField);
		JButton pathSelect = new JButton("...");
		pathSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String file = selectFile("Select an image");
				pathEntryField.setText(file);
				if(file != null && nameEntryField.getText().equals("")) {
					int
					lastSeparator = file.lastIndexOf(File.separator),
					dot = file.lastIndexOf('.');
					if(lastSeparator >= 0
							&& dot >= 0
							&& lastSeparator < file.length()-1
							&& dot < file.length()) {
						nameEntryField.setText(file.substring(lastSeparator+1, dot));
					}
				}
			}
		});
		pathEntry.add(pathSelect);
		
		nameLabel.validate();
		pathLabel.validate();
		Dimension labelSize = new Dimension(Math.max(nameLabel.getPreferredSize().width, pathLabel.getPreferredSize().width),nameLabel.getPreferredSize().height);
		nameLabel.setPreferredSize(labelSize);
		pathLabel.setPreferredSize(labelSize);
		
		JPanel controlButtons = new JPanel();
		controlButtons.setLayout(new BoxLayout(controlButtons,BoxLayout.X_AXIS));
		JButton button = new JButton("Cancel");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		controlButtons.add(button);
		controlButtons.add(Box.createHorizontalGlue());
		button = new JButton("Confirm");
		button.setSelected(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String 	name = nameEntryField.getText(),
						path = pathEntryField.getText();
				boolean hasName = !name.equals(""),
						hasPath = !path.equals("");
				if(hasName && hasPath) {
					Images.retrieveImage(name, path);
					((DefaultListModel<String>)nameList.getModel()).addElement(name);
					nameList.setSelectedValue(name, true);
					frame.dispose();
				} else {
					if(hasName) {
						JOptionPane.showMessageDialog(frame, "Make sure to select a file!", "Missing path",JOptionPane.ERROR_MESSAGE);
					} else if(hasPath) {
						JOptionPane.showMessageDialog(frame, "Make sure to name your image!", "Missing name",JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(frame, "Select an image using the \'...\' button,\nthen name it in the box labelled name!", "Missing name and path",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		controlButtons.add(button);
		
		host.add(nameEntry);
		host.add(pathEntry);
		host.add(Box.createVerticalStrut(5));
		host.add(controlButtons);
		
		frame.setPreferredSize(new Dimension(290,120));
		frame.setMinimumSize(new Dimension(190,120));
		frame.setMaximumSize(new Dimension(Integer.MAX_VALUE,120));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}
	
	private static String selectFile(String title) {
		FileDialog fd = new FileDialog((Frame)null, title, FileDialog.LOAD);
		fd.setFilenameFilter(new FilenameFilter() {
			List<String> extensions = Arrays.asList(".png",".tif",".tiff",".bmp",".jpg",".jpeg",".gif");
			
			public boolean accept(File dir, String name) {
				int dotIndex = name.lastIndexOf('.');
				if(dotIndex > 0 && dotIndex < name.length()-1) {
					String ext = name.substring(dotIndex).toLowerCase();
					return extensions.contains(ext);
				}
				return false;
			}
		});
		fd.setVisible(true);
		String 	path = fd.getDirectory(),
				file = fd.getFile();
		if(file != null) {
			if(path != null) {
				return path+file;
			} else {
				return file;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves the image at the given file path and assigns it the given name.
	 * Also handles formatting for future use, copying the image to the res/images folder,
	 * and renaming it if it's name does not follow the convention {@code name}.png
	 * <br><br>For use by the framework; do not use this function to get sprite images,
	 * instead use {@link Images#getSprite(String) getImage}
	 * @param name The name of the image to be used in the {@link Images#getSprite(String) getImage} method
	 * @param path The file path of the image
	 * @return Whether the image was retrieved successfully
	 * @see {@link java.io.File}
	 * @see {@link java.awt.BufferedImage}
	 */
	private static boolean retrieveImage(String name, String path) {
		File f = new File(path);
		BufferedImage image = null;
		try {
			image = ImageIO.read(f);
			images.put(name, image);
		} catch(IOException e) {
			System.err.println("Could not retrieve image "+path);
			return false;
		}
		File newFile = new File(imageFolder+name+".png");
		if(!f.equals(newFile)) {
			try {
				ImageIO.write(image, "PNG", new File(imageFolder+name+".png"));
				if(f.getParent() != null && f.getParent().equals(imageFolder)) {
					f.delete();
				}
			} catch (IOException e) {
				System.err.println("Could not write "+imageFolder+name+".png");
				e.printStackTrace();
			}
		}
		filePaths.put(name, newFile.getPath());
		return true;
	}
	
	/**
	 * Removes the named image from the handler, and deletes it from the images folder.
	 * <br><br>For use by the framework; do not use this function to remove images, instead
	 * use the sprite manager.
	 * @param name the name of the image to be removed
	 */
	private static void removeImage(String name) {
		if(images.containsKey(name)) {
			String path = filePaths.get(name);
			File f = new File(path);
			f.delete();
			filePaths.remove(name);
			images.remove(name);
		}
	}
	
	/**
	 * Returns the {@linkplain BufferedImage} with the given name
	 * @param name The name of the image to get
	 * @return The image with the given name, or {@code null} if the name is not found
	 */
	public static BufferedImage getImage(String name) {
		return images.get(name);
	}
	
	/**
	 * Returns the {@linkplain Sprite} with the given name
	 * @param name The name of the Sprite to get
	 * @return The Sprite with the given name, or {@code null} if the name is not found
	 */
	public static Sprite getSprite(String name) {
		if(images.containsKey(name)) {
			return new Sprite(images.get(name));
		} else {
			return null;
		}
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
		if(images.containsKey(name)) {
			return new Sprite(images.get(name), rows, cols);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns a {@link java.util.Set Set} containing the names of the images
	 * available to the {@link Images#getSprite(String) getSprite} method
	 * @return A Set containing Strings
	 */
	public static Set<String> getSpriteNames(){
		((HashMap<String,BufferedImage>)images).keySet();
		return images.keySet();
	}
	
	/**
	 * Write the names and paths to a file to be loaded at a later date
	 */
	private static void save() {
		File f = new File(imageFolder+"images.dat");
		try {
			if(f.exists()) {
				Game.debugPrint("Deleting old version of \'"+f.getPath()+"\'");
				f.delete();
			}
			Game.debugPrint("Creating \'"+f.getPath()+"\'");
			f.createNewFile();
		} catch(IOException e) {
			System.err.println("Failed to create file \'"+f.getPath()+"\'");
			e.printStackTrace();
		}
		try {
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			Game.debugPrint("Writing \'"+f.getPath()+"\'");
			oos.writeObject(filePaths);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not find \'images.dat,\' this should not happen");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Failed to create outputstream to \'images.dat\'");
			e.printStackTrace();
		}
		for(String name : images.keySet()) {
			writeImage(name, images.get(name));
		}
	}
	
	private static void writeImage(String name, BufferedImage image) {
		try {
			ImageIO.write(image, "PNG", new File(imageFolder+name+".png"));
		} catch(IOException e) {
			System.err.println("Failed to write image "+imageFolder+name+".png");
			e.printStackTrace();
		}
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
		transform.translate((newWidth-image.getWidth())/2, (newHeight-image.getHeight())/2);
		transform.rotate(radians, image.getWidth()/2, image.getHeight()/2);
		
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
	 * @Param sy The factor to scale the height of the image by
	 * @return A copy of {@link image} with the transformations applied
	 */
	public static BufferedImage transformImage(BufferedImage image, double radians, double sx, double sy) {
		int scaleWidth = (int)(image.getWidth() * sx);
		int scaleHeight = (int)(image.getHeight() * sy);
		int newWidth = (int)(scaleHeight*Math.abs(Math.sin(radians))+scaleWidth*Math.abs(Math.cos(radians)));
		int newHeight = (int)(scaleHeight*Math.abs(Math.cos(radians))+scaleWidth*Math.abs(Math.sin(radians)));
		
		BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		
		AffineTransform transform = new AffineTransform();
		transform.translate((newWidth-scaleWidth)/2, (newHeight-scaleHeight)/2);
		transform.rotate(radians, scaleWidth/2, scaleHeight/2);
		transform.scale(sx, sy);
		
		Graphics2D g2 = (Graphics2D)out.getGraphics();
		g2.setTransform(transform);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		
		return out;
	}
	
	/**
	 * Localizes the String path, replacing any forward/back slashes with the appropriate
	 * version provided by {@link File#separatorChar}
	 * @param path The path to be localized
	 * @return The localized version of the path
	 */
	private static String localizePath(String path) {
		if(File.separatorChar == '/') {
			path = path.replace('\\', File.separatorChar);
		} else {
			path = path.replace('/', File.separatorChar);
		}
		return path;
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
