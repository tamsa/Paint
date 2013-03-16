/*******************************************************************************
 * Copyright (c) 2013 black-pixel.net
 * 
 * See the file MIT-license.txt for copying permission.
 ******************************************************************************/
package net.black_pixel.Black_PixelPaint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.BevelBorder;

public class B_PPaint extends JFrame {

    private JPanel contentPane;
    private JMenuBar menuBar;

    PaintPanel canvas;

    private BufferedImage commitedLayer = new BufferedImage(500, 500,
            BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D g2D = commitedLayer.createGraphics();
    private BufferedImage drawingLayer = new BufferedImage(500, 500,
            BufferedImage.TYPE_4BYTE_ABGR);
    
    PaintTools tool;


    Dimension imageSize = new Dimension(500, 500);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    ImageIcon pencilIcon;
    ImageIcon rectangleIcon;
    ImageIcon paintColorIcon;
    ImageIcon fillRectangleIcon;
    JButton buttonRectangle;
    JButton buttonPencil;
    JButton buttonPaintColor;
    JButton buttonFillRectangle;

    // brush size icons
    ImageIcon brushSize1Icon = new ImageIcon();
    ImageIcon brushSize5Icon = new ImageIcon();
    ImageIcon brushSize10Icon = new ImageIcon();

    private JPopupMenu brushSizeMenu;

    Color color;
    Color backgroundColor = Color.white;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    B_PPaint frame = new B_PPaint();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */

    public B_PPaint() {
        
        pencilIcon = new ImageIcon("img/pencil.png");
        buttonPencil = new JButton(pencilIcon);
        buttonPencil.setContentAreaFilled(false);
        buttonPencil.setPreferredSize(new Dimension(22, 22));
        buttonPencil.setActionCommand("Pencil");
        buttonPencil.setBorder(null);

        rectangleIcon = new ImageIcon("img/rect_empty.png");
        buttonRectangle = new JButton(rectangleIcon);
        buttonRectangle.setContentAreaFilled(false);
        buttonRectangle.setPreferredSize(new Dimension(22, 22));
        buttonRectangle.setActionCommand("Rectangle");
        buttonRectangle.setBorder(null);

        fillRectangleIcon = new ImageIcon("img/rect_filled.png");
        buttonFillRectangle = new JButton(fillRectangleIcon);
        buttonFillRectangle.setContentAreaFilled(false);
        buttonFillRectangle.setPreferredSize(new Dimension(22, 22));
        buttonFillRectangle.setActionCommand("fillRectangle");
        buttonFillRectangle.setBorder(null);

        paintColorIcon = new ImageIcon("img/color_wheel.png");
        buttonPaintColor = new JButton(paintColorIcon);
        buttonPaintColor.setContentAreaFilled(false);
        buttonPaintColor.setPreferredSize(new Dimension(22, 22));
        buttonPaintColor.setActionCommand("Paint Color");
        buttonPaintColor.setBorder(null);

        brushSizeMenu = new JPopupMenu();
        brushSizeMenu.setVisible(false);
        JMenuItem brushSize1 = new JMenuItem("Size 1");
        brushSize1.setIcon(brushSize1Icon);
        brushSize1.setActionCommand("brushSize1");
        JMenuItem brushSize5 = new JMenuItem("Size 5");
        brushSize5.setActionCommand("brushSize5");
        JMenuItem brushSize10 = new JMenuItem("Size 10");
        brushSize10.setActionCommand("brushSize10");
        brushSizeMenu.add(brushSize1);
        brushSizeMenu.add(brushSize5);
        brushSizeMenu.add(brushSize10);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 610, 510);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        setTitle("Black-Pixel Paint");

        JPanel toolbar = new JPanel();
        contentPane.add(toolbar, BorderLayout.WEST);

        toolbar.setPreferredSize(new Dimension(32, 100));
        toolbar.setLayout(new FlowLayout());
        toolbar.add(buttonPencil);
        toolbar.add(buttonRectangle);
        toolbar.add(buttonFillRectangle);
        toolbar.add(buttonPaintColor);

        ActionListener buttonActionListener = new ButtonActionListener();

        buttonPencil.addActionListener(buttonActionListener);
        buttonRectangle.addActionListener(buttonActionListener);
        buttonPaintColor.addActionListener(buttonActionListener);
        buttonFillRectangle.addActionListener(buttonActionListener);
        brushSize1.addActionListener(buttonActionListener);
        brushSize5.addActionListener(buttonActionListener);
        brushSize10.addActionListener(buttonActionListener);

        /*
         * canvas = new JPanel() {
         * 
         * @Override public void paintComponent(Graphics g) {
         * super.paintComponent(g); g.drawImage(commitedLayer, 0, 0, null);
         * g.drawImage(drawingLayer, 0, 0, null); } };
         */

        canvas = new PaintPanel();
        canvas.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null,
                null));
        contentPane.add(canvas, BorderLayout.CENTER);
        canvas.setPreferredSize(imageSize);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem fileNew = new JMenuItem("New");
        JMenuItem fileOpen = new JMenuItem("Open");
        JMenuItem fileSave = new JMenuItem("Save");
        JMenuItem fileClose = new JMenuItem("Close");
        fileMenu.add(fileNew);
        fileMenu.add(fileOpen);
        fileMenu.add(fileSave);
        fileMenu.addSeparator();
        fileMenu.add(fileClose);

        ActionListener fileMenuActionListener = new FileMenuActionListener();

        fileNew.addActionListener(fileMenuActionListener);
        fileOpen.addActionListener(fileMenuActionListener);
        fileSave.addActionListener(fileMenuActionListener);
        fileClose.addActionListener(fileMenuActionListener);

        JMenu imageMenu = new JMenu("Image");
        menuBar.add(imageMenu);

        JMenuItem imageReSize = new JMenuItem("Resize Canvas");
        JMenuItem imageReScale = new JMenuItem("Rescale Image");
        imageMenu.add(imageReSize);
        imageMenu.add(imageReScale);

        ActionListener imageMenuActionListener = new ImageMenuActionListener();

        imageReSize.addActionListener(imageMenuActionListener);
        imageReScale.addActionListener(imageMenuActionListener);

        g2D.setColor(Color.white);
        g2D.fillRect(0, 0, 500, 500); // Clear the whole image
        g2D.setColor(Color.black);

        tool = new PaintTools(commitedLayer, drawingLayer, canvas);
        canvas.addMouseListener(tool);
        canvas.addMouseMotionListener(tool);

        JScrollPane scroll = new JScrollPane(canvas);
        add(scroll);
        scroll.setSize(screenSize.width - 200, screenSize.height - 200);

        pack();
    }

    public class PaintPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(commitedLayer, 0, 0, null);
            g.drawImage(drawingLayer, 0, 0, null);
        }
    }

    public Color colorChooser() {
        color = JColorChooser.showDialog(getContentPane(),
                "Choose drawing color", Color.black);
        return color;
    }

    public void open() {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                commitedLayer = ImageIO.read(fc.getSelectedFile());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            tool.transparency = commitedLayer.getColorModel().hasAlpha();
            ImageIcon icon = new ImageIcon(commitedLayer);
            imageSize = new Dimension(icon.getIconWidth(), icon.getIconHeight()); // ADDED
            Dimension canvasSize = new Dimension();
            if (imageSize.width > screenSize.width)
                canvasSize.width = screenSize.width - 200;
            else
                canvasSize.width = imageSize.width;
            if (imageSize.height > screenSize.height)
                canvasSize.height = screenSize.height - 200;
            else
                canvasSize.height = imageSize.height;
            canvas.setPreferredSize(canvasSize); // ADDED
            drawingLayer = new BufferedImage(imageSize.width, imageSize.height,
                    BufferedImage.TYPE_4BYTE_ABGR);

            canvas.repaint();
            tool.initTools(commitedLayer, drawingLayer, canvas);
            pack();

        }
    }

    public void save() {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String name = fc.getSelectedFile().getName();
            String formatName;
            
            Pattern p = Pattern.compile(".*\\.(.*)"); //Check if file format given
            Matcher m = p.matcher( name );
            if (m.find()) {
                formatName = m.group(1); // The file format
            } else {
                formatName = "png"; // If none given, use png
            }
            File target = new File(fc.getCurrentDirectory() + File.separator
                    + name);
            try {
                ImageIO.write(commitedLayer, formatName, target);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    // Define ActionListener for File Menu in MenuBar
    private class FileMenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().equals("New")) {
                tool.transparency = false; //reset transparency
                tool.initTools(commitedLayer, drawingLayer, canvas);
                tool.newFile();
            } else if (actionEvent.getActionCommand().equals("Open")) {
                open();
            } else if (actionEvent.getActionCommand().equals("Save")) {
                save();
            } else if (actionEvent.getActionCommand().equals("Close")) {
                System.exit(0);
            }
        }
    }

    private class ImageMenuActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().equals("Resize Canvas")) {
                resizeCanvas();
            }
            if (actionEvent.getActionCommand().equals("Rescale Image")) {
                rescaleImage();
            }
        }
    }

    private class ButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().equals("Pencil")) {
                tool.pencil();
                brushSizeMenu.show(contentPane,
                        buttonPencil.getLocation().x + 26,
                        buttonPencil.getLocation().y + 26);
            } else if (actionEvent.getActionCommand().equals("Rectangle")) {
                tool.rectangle();
                brushSizeMenu.show(contentPane,
                        buttonRectangle.getLocation().x + 26,
                        buttonRectangle.getLocation().y + 26);
            } else if (actionEvent.getActionCommand().equals("Paint Color")) {
                tool.color = colorChooser();
            } else if (actionEvent.getActionCommand().equals("fillRectangle")) {
                tool.fillRectangle();
            } else if (actionEvent.getActionCommand().equals("brushSize1")) {
                tool.setStroke(1);
            } else if (actionEvent.getActionCommand().equals("brushSize5")) {
                tool.setStroke(5);
            } else if (actionEvent.getActionCommand().equals("brushSize10")) {
                tool.setStroke(10);
            }
        }

    }

    public void resizeCanvas() {
        JTextField xField = new JTextField(5);
        xField.setText(Integer.toString(commitedLayer.getHeight()));
        JTextField yField = new JTextField(5);
        yField.setText(Integer.toString(commitedLayer.getWidth()));

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("x:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("y:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            imageSize = new Dimension(Integer.parseInt(xField.getText()),
                    Integer.parseInt(yField.getText()));
            canvas.setPreferredSize(imageSize); // ADDED
            drawingLayer = new BufferedImage(imageSize.width, imageSize.height,
                    BufferedImage.TYPE_4BYTE_ABGR);
            BufferedImage commitedLayerOld = commitedLayer;
            commitedLayer = new BufferedImage(imageSize.width,
                    imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
            tool.initTools(commitedLayer, drawingLayer, canvas);
            tool.newFile();
            commitedLayer.getGraphics().drawImage(commitedLayerOld, 0, 0, null);
            canvas.repaint();
            pack();
        }
    }

    public void rescaleImage() {
        JTextField xField = new JTextField(5);
        xField.setText(Integer.toString(commitedLayer.getHeight()));
        JTextField yField = new JTextField(5);
        yField.setText(Integer.toString(commitedLayer.getWidth()));

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("x:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("y:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter X and Y Values", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            imageSize = new Dimension(Integer.parseInt(xField.getText()),
                    Integer.parseInt(yField.getText()));

            BufferedImage commitedLayerOld = commitedLayer;

            commitedLayer = new BufferedImage(imageSize.width,
                    imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);

            Graphics2D graphics2D = commitedLayer.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(commitedLayerOld, 0, 0, imageSize.width,
                    imageSize.height, null);

            canvas.setPreferredSize(imageSize);
            drawingLayer = new BufferedImage(imageSize.width, imageSize.height,
                    BufferedImage.TYPE_4BYTE_ABGR);
            canvas.repaint();
            tool.initTools(commitedLayer, drawingLayer, canvas);
            pack();
        }
    }
}
