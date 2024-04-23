/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

 package com.mycompany.imageviewer;

 /**
  *
  * @author nilabh
  */
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import java.io.File;
 import javax.imageio.ImageIO;
 import java.awt.image.BufferedImage;
 import java.awt.image.ColorConvertOp;
 import java.awt.color.ColorSpace;
 
 
 public class ImageViewer extends JFrame implements ActionListener {
 
   private final JLabel imageLabel;
   private final JButton openButton, zoomInButton, zoomOutButton, rotateClockwiseButton, rotateCounterClockwiseButton, flipHorizontalButton, flipVerticalButton,  borderButton, saveButton, cropButton;
   private final JComboBox<String> filterComboBox; // Dropdown menu for filters
   private float zoomLevel = 1.0f;
   private int rotationAngle = 0; // Angle of rotation
   private boolean flippedHorizontally = false;
   private boolean flippedVertically = false;
   private Color borderColor = Color.BLACK;
   private int borderWidth = 0;
   private Rectangle croppingRect = null;
 
   public ImageViewer() {
     super("Image Viewer");
     setLayout(new BorderLayout());
 
     imageLabel = new JLabel();
     imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
     add(imageLabel, BorderLayout.CENTER);
 
     JPanel buttonPanel = new JPanel();
     openButton = new JButton("Open Image");
     openButton.addActionListener(this);
     buttonPanel.add(openButton);
 
     zoomInButton = new JButton("Zoom In");
     zoomInButton.addActionListener(this);
     buttonPanel.add(zoomInButton);
 
     zoomOutButton = new JButton("Zoom Out");
     zoomOutButton.addActionListener(this);
     buttonPanel.add(zoomOutButton);
 
     rotateClockwiseButton = new JButton("Rotate Clockwise");
     rotateClockwiseButton.addActionListener(this);
     buttonPanel.add(rotateClockwiseButton);
 
     rotateCounterClockwiseButton = new JButton("Rotate Counter-Clockwise");
     rotateCounterClockwiseButton.addActionListener(this);
     buttonPanel.add(rotateCounterClockwiseButton);
 
     flipHorizontalButton = new JButton("Flip Horizontal");
     flipHorizontalButton.addActionListener(this);
     buttonPanel.add(flipHorizontalButton);
 
     flipVerticalButton = new JButton("Flip Vertical");
     flipVerticalButton.addActionListener(this);
     buttonPanel.add(flipVerticalButton);
 
     saveButton = new JButton("Save Image");
     saveButton.addActionListener(this);
     buttonPanel.add(saveButton);
     
     borderButton = new JButton("Add Border");
     borderButton.addActionListener(this);
     buttonPanel.add(borderButton);
     
      cropButton = new JButton("Crop");
      cropButton.addActionListener(this);
      buttonPanel.add(cropButton);
 
     // Add filter dropdown menu
     filterComboBox = new JComboBox<>(new String[]{"None", "Grayscale", "Sepia", "Invert Colors"});
     filterComboBox.addActionListener(this);
     buttonPanel.add(filterComboBox);
 
     add(buttonPanel, BorderLayout.SOUTH);
 
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setSize(800, 600);
     setVisible(true);
   }
 
   @Override
   public void actionPerformed(ActionEvent e) {
     if (e.getSource() == openButton) {
       JFileChooser fileChooser = new JFileChooser();
       int result = fileChooser.showOpenDialog(this);
 
       if (result == JFileChooser.APPROVE_OPTION) {
         File selectedFile = fileChooser.getSelectedFile();
         try {
           BufferedImage image = ImageIO.read(selectedFile);
           applyFilter(image); // Apply selected filter to the image
           updateImage(); // Update the displayed image
         } catch (Exception ex) {
           ex.printStackTrace();
           JOptionPane.showMessageDialog(this, "Error opening image!", "Error", JOptionPane.ERROR_MESSAGE);
         }
       }
     } else if (e.getSource() == zoomInButton) {
       if (zoomLevel < 2.0f) {
         zoomLevel += 0.2f;
         updateImage();
       }
     } else if (e.getSource() == zoomOutButton) {
       if (zoomLevel > 0.5f) {
         zoomLevel -= 0.2f;
         updateImage();
       }
     } else if (e.getSource() == rotateClockwiseButton) {
       rotationAngle += 90; // Rotate clockwise by 90 degrees
       updateImage();
     } else if (e.getSource() == rotateCounterClockwiseButton) {
       rotationAngle -= 90; // Rotate counter-clockwise by 90 degrees
       updateImage();
     } else if (e.getSource() == flipHorizontalButton) {
       flippedHorizontally = !flippedHorizontally;
       updateImage();
     } else if (e.getSource() == flipVerticalButton) {
       flippedVertically = !flippedVertically;
       updateImage();
     } else if (e.getSource() == saveButton) {
       saveImage();
     } else if (e.getSource() == filterComboBox) {
       applyFilter(null); // Apply selected filter to the image
       updateImage(); // Update the displayed image
     }else if (e.getSource() == borderButton) {
        chooseBorderColorAndWidth();
    }
     
     if (e.getSource() == cropButton) {
            performCrop();
        }
   }
   
   private void performCrop() {
    if (imageLabel.getIcon() != null) {
        BufferedImage originalImage = new BufferedImage(imageLabel.getWidth(), imageLabel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = originalImage.createGraphics();
        imageLabel.paint(g2d);
        g2d.dispose();

        int x = 100; // Example x-coordinate for cropping
        int y = 100; // Example y-coordinate for cropping
        int width = 200; // Example width for cropping
        int height = 200; // Example height for cropping

        if (x >= 0 && y >= 0 && width > 0 && height > 0 && x + width <= originalImage.getWidth() && y + height <= originalImage.getHeight()) {
            BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);
            ImageIcon croppedIcon = new ImageIcon(croppedImage);
            imageLabel.setIcon(croppedIcon);
            pack(); // Adjust frame size to fit cropped image
        } else {
            JOptionPane.showMessageDialog(this, "Invalid crop dimensions!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

   
//    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (croppingRect != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(croppingRect);
        }
    }
    


//    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == imageLabel) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Point startPoint = e.getPoint();
                croppingRect = new Rectangle(startPoint);
            }
        }
    }

//    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == imageLabel) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                Point endPoint = e.getPoint();
                croppingRect.add(endPoint);
                repaint();
            }
        }
    }



//    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == imageLabel && croppingRect != null) {
            Point currentPoint = e.getPoint();
            croppingRect.setSize((int) (currentPoint.getX() - croppingRect.getX()), (int) (currentPoint.getY() - croppingRect.getY()));
            repaint();
        }
    }

   private void chooseBorderColorAndWidth() {
    Color selectedColor = JColorChooser.showDialog(this, "Choose Border Color", borderColor);
    if (selectedColor != null) {
        borderColor = selectedColor;
        String input = JOptionPane.showInputDialog(this, "Enter Border Width (in pixels):", String.valueOf(borderWidth));
        try {
            borderWidth = Integer.parseInt(input);
            updateImage();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for border width!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 }
   
   private ImageIcon addBorder(ImageIcon icon, Color borderColor, int borderWidth) {
    BufferedImage bufferedImage = new BufferedImage(
            icon.getIconWidth() + 2 * borderWidth, icon.getIconHeight() + 2 * borderWidth, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = bufferedImage.createGraphics();

    // Fill the border with the specified color
    g2d.setColor(borderColor);
    g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

    // Draw the image on top of the border
    g2d.drawImage(icon.getImage(), borderWidth, borderWidth, null);

    g2d.dispose();
    return new ImageIcon(bufferedImage);
}
 
   private void applyFilter(BufferedImage image) {
     if (imageLabel.getIcon() != null || image != null) {
       ImageIcon icon;
       if (image != null) {
         icon = new ImageIcon(image);
       } else {
         icon = (ImageIcon) imageLabel.getIcon();
       }
 
       String selectedFilter = (String) filterComboBox.getSelectedItem();
       switch (selectedFilter) {
         case "Grayscale":
           icon           = applyGrayscaleFilter(icon);
           break;
         case "Sepia":
           icon = applySepiaFilter(icon);
           break;
         case "Invert Colors":
           icon = applyInvertColorsFilter(icon);
           break;
         default:
           // No filter selected, do nothing
           break;
       }
       imageLabel.setIcon(icon);
       pack(); // Adjust frame size to fit image
     }
   }
 
   // Method to apply grayscale filter
   private ImageIcon applyGrayscaleFilter(ImageIcon icon) {
     BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
     Graphics g = image.createGraphics();
     g.drawImage(icon.getImage(), 0, 0, null);
     g.dispose();
 
     ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
     op.filter(image, image);
 
     return new ImageIcon(image);
   }
 
   
 // Method to apply sepia filter
 private ImageIcon applySepiaFilter(ImageIcon icon) {
   BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
   Graphics graphics = image.createGraphics(); // Rename variable to 'graphics'
   graphics.drawImage(icon.getImage(), 0, 0, null);
   graphics.dispose();
 
   int sepiaDepth = 20;
   int sepiaIntensity = 40;
 
   for (int y = 0; y < image.getHeight(); y++) {
     for (int x = 0; x < image.getWidth(); x++) {
       int rgb = image.getRGB(x, y);
       int r = (rgb >> 16) & 0xFF;
       int g = (rgb >> 8) & 0xFF;
       int b = rgb & 0xFF;
 
       int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
       int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
       int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);
 
       if (tr > 255) tr = 255;
       if (tg > 255) tg = 255;
       if (tb > 255) tb = 255;
 
       image.setRGB(x, y, new Color(tr, tg, tb).getRGB());
     }
   }
 
   return new ImageIcon(image);
 }
 
 // Method to apply invert colors filter
 private ImageIcon applyInvertColorsFilter(ImageIcon icon) {
   BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
   Graphics graphics = image.createGraphics(); // Rename variable to 'graphics'
   graphics.drawImage(icon.getImage(), 0, 0, null);
   graphics.dispose();
 
   for (int y = 0; y < image.getHeight(); y++) {
     for (int x = 0; x < image.getWidth(); x++) {
       int rgb = image.getRGB(x, y);
       int r = 255 - (rgb >> 16) & 0xFF;
       int g = 255 - (rgb >> 8) & 0xFF;
       int b = 255 - rgb & 0xFF;
       image.setRGB(x, y, new Color(r, g, b).getRGB());
     }
   }
 
   return new ImageIcon(image);
 }
 
 
  private void updateImage() {
    if (imageLabel.getIcon() != null) {
        ImageIcon icon = (ImageIcon) imageLabel.getIcon();
        icon = rotateImage(icon, rotationAngle); // Rotate the image
        if (flippedHorizontally) {
            icon = flipImageHorizontal(icon);
        }
        if (flippedVertically) {
            icon = flipImageVertical(icon);
        }
        
      
        
        icon = addBorder(icon, borderColor, borderWidth);
         
        imageLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(
                (int) (icon.getIconWidth() * zoomLevel), (int) (icon.getIconHeight() * zoomLevel), Image.SCALE_SMOOTH)));
    }
}



 
   // Method to rotate the ImageIcon
   private ImageIcon rotateImage(ImageIcon icon, int angle) {
     BufferedImage bufferedImage = new BufferedImage(
         icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2d = bufferedImage.createGraphics();
     g2d.rotate(Math.toRadians(angle), icon.getIconWidth() / 2, icon.getIconHeight() / 2);
     g2d.drawImage(icon.getImage(), 0, 0, null);
     g2d.dispose();
     return new ImageIcon(bufferedImage);
   }
 
   // Method to flip the ImageIcon horizontally
   private ImageIcon flipImageHorizontal(ImageIcon icon) {
     BufferedImage bufferedImage = new BufferedImage(
         icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2d = bufferedImage.createGraphics();
     g2d.drawImage(icon.getImage(), icon.getIconWidth(), 0, -icon.getIconWidth(), icon.getIconHeight(), null);
     g2d.dispose();
     return new ImageIcon(bufferedImage);
   }
 
   // Method to flip the ImageIcon vertically
   private ImageIcon flipImageVertical(ImageIcon icon) {
     BufferedImage bufferedImage = new BufferedImage(
         icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
     Graphics2D g2d = bufferedImage.createGraphics();
     g2d.drawImage(icon.getImage(), 0, icon.getIconHeight(), icon.getIconWidth(), -icon.getIconHeight(), null);
     g2d.dispose();
     return new ImageIcon(bufferedImage);
   }
 
   // Method to save the displayed image to a file
   private void saveImage() {
     if (imageLabel.getIcon() != null) {
       ImageIcon icon = (ImageIcon) imageLabel.getIcon();
       BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
       Graphics g = image.createGraphics();
       icon.paintIcon(null, g, 0, 0);
       g.dispose();
 
       JFileChooser fileChooser = new JFileChooser();
       int result = fileChooser.showSaveDialog(this);
 
       if (result == JFileChooser.APPROVE_OPTION) {
         File file = fileChooser.getSelectedFile();
         try {
           String format = file.getName().substring(file.getName().lastIndexOf('.') + 1);
           if (ImageIO.write(image, format, file)) {
             JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
           } else {
             JOptionPane.showMessageDialog(this, "Error saving image!", "Error", JOptionPane.ERROR_MESSAGE);
           }
         } catch (Exception ex) {
           ex.printStackTrace();
           JOptionPane.showMessageDialog(this, "Error saving image!", "Error", JOptionPane.ERROR_MESSAGE);
         }
       }
     } else {
       JOptionPane.showMessageDialog(this, "No image to save!", "Error", JOptionPane.ERROR_MESSAGE);
     }
   }
 
   public static void main(String[] args) {
     SwingUtilities.invokeLater(() -> new ImageViewer());
   }
 }
 
 
 
 
 
 
 