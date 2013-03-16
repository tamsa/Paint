/*******************************************************************************
 * Copyright (c) 2013 black-pixel.net
 * 
 * See the file MIT-license.txt for copying permission.
 ******************************************************************************/
package net.black_pixel.Black_PixelPaint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class PaintTools extends MouseAdapter {

    BufferedImage commitedLayer;
    BufferedImage drawingLayer;

    Graphics2D dLGraphics;
    Graphics2D cLGraphics;

    JPanel canvas;
    int x = 0, y = 0, sizeX = 0, sizeY = 0;
    String selectedTool;

    // pencil
    java.awt.geom.GeneralPath path;
    BasicStroke stroke;
    int strokeSize = 5;

    boolean leftMouse = false;
    boolean hasDragged = false;
    boolean transparency = false;

    Color color = Color.black;

    public PaintTools(BufferedImage commitedLayer, BufferedImage drawingLayer,
            JPanel canvas) {
        initTools(commitedLayer, drawingLayer, canvas);
    }

    public void initTools(BufferedImage commitedLayer,
            BufferedImage drawingLayer, JPanel canvas) {
        this.commitedLayer = commitedLayer;
        this.drawingLayer = drawingLayer;
        this.canvas = canvas;
        cLGraphics = this.commitedLayer.createGraphics();
        dLGraphics = this.drawingLayer.createGraphics();
        cLGraphics.setColor(color);
        pencil();
    }

    public void newFile() {
        if (!transparency) {
            cLGraphics.setColor(Color.white);
            cLGraphics.fillRect(0, 0, commitedLayer.getWidth(),
                    commitedLayer.getHeight()); // Clear the whole image, may
                                                // cause a problem because of
                                                // the fixed value. Needs to be
                                                // checked
            cLGraphics.setColor(color);
        }
        canvas.repaint();
        pencil();
    }

    public void setStroke(int strokeSize) {
        this.strokeSize = strokeSize;
        if (selectedTool.equals("pencil")) {
            stroke = new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE,
                    BasicStroke.JOIN_ROUND);
        } else if (selectedTool.equals("rectangle")) {
            new BasicStroke(strokeSize, BasicStroke.CAP_BUTT,
                    BasicStroke.CAP_BUTT);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedTool.equals("rectangle")) {
                x = e.getX();
                y = e.getY();
                sizeX = x;
                sizeY = y;
            } else if (selectedTool.equals("pencil")) {
                pressPencil(e);
            } else if (selectedTool.equals("fillRectangle")) {
                x = e.getX();
                y = e.getY();
                sizeX = x;
                sizeY = y;
            }
            leftMouse = true;
        } else {
            leftMouse = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (leftMouse) {
            if (selectedTool.equals("rectangle")
                    || (selectedTool.equals("fillRectangle")))
                dragRectangle(e);
            else if (selectedTool.equals("pencil"))
                dragPencil(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedTool.equals("rectangle")
                    || (selectedTool.equals("fillRectangle")))
                releaseRectangle();
            else if (selectedTool.equals("pencil"))
                releasePencil(e);
        }
        // clear the drawing Layer
    }

    public void pencil() {
        selectedTool = "pencil";
        stroke = new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_ROUND);
    }

    public void pressPencil(MouseEvent e) {
        hasDragged = false;
        path = new java.awt.geom.GeneralPath();
        path.moveTo(e.getX(), e.getY()); // Set starting point
    }

    public void dragPencil(MouseEvent e) {
        hasDragged = true;

        path.lineTo(e.getX(), e.getY());

        // clear drawing layer
        dLGraphics.setComposite(java.awt.AlphaComposite.Clear);
        dLGraphics.fillRect(0, 0, drawingLayer.getWidth(),
                drawingLayer.getHeight());

        // draw path to drawing layer
        dLGraphics.setComposite(java.awt.AlphaComposite.SrcOver);
        dLGraphics.setStroke(stroke);
        dLGraphics.setColor(color);
        dLGraphics.draw(path);

        canvas.repaint();
    }

    public void releasePencil(MouseEvent e) {
        if (!hasDragged)
            path.lineTo(e.getX(), e.getY());

        // clear drawing layer
        dLGraphics.setComposite(java.awt.AlphaComposite.Clear);
        dLGraphics.fillRect(0, 0, drawingLayer.getWidth(),
                drawingLayer.getHeight());

        // draw path to commited layer

        cLGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        cLGraphics.setStroke(stroke);
        cLGraphics.setColor(color);
        cLGraphics.draw(path);

        canvas.repaint();

        path = null;
    }

    public void rectangle() {
        selectedTool = "rectangle";
    }

    public void fillRectangle() {
        selectedTool = "fillRectangle";
    }

    public void dragRectangle(MouseEvent e) {
        sizeX = e.getX();
        sizeY = e.getY();

        stroke = new BasicStroke(strokeSize, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_ROUND);

        dLGraphics.setColor(color);
        dLGraphics.setStroke(stroke);
        // clear drawing layer
        dLGraphics.setComposite(java.awt.AlphaComposite.Clear);
        dLGraphics.fillRect(0, 0, drawingLayer.getWidth(),
                drawingLayer.getHeight());

        dLGraphics.setComposite(java.awt.AlphaComposite.SrcOver);
        if (selectedTool.equals("rectangle")) {
            if (sizeX < x) {
                if (sizeY < y)
                    dLGraphics.drawRect(sizeX, sizeY, x - sizeX, y - sizeY);
                else
                    dLGraphics.drawRect(sizeX, y, x - sizeX, sizeY - y);
            } else if (sizeY < y)
                dLGraphics.drawRect(x, sizeY, sizeX - x, y - sizeY);
            else
                dLGraphics.drawRect(x, y, sizeX - x, sizeY - y);
        } else if (selectedTool.equals("fillRectangle")) {
            if (sizeX < x) {
                if (sizeY < y)
                    dLGraphics.fillRect(sizeX, sizeY, x - sizeX, y - sizeY);
                else
                    dLGraphics.fillRect(sizeX, y, x - sizeX, sizeY - y);
            } else if (sizeY < y)
                dLGraphics.fillRect(x, sizeY, sizeX - x, y - sizeY);
            else
                dLGraphics.fillRect(x, y, sizeX - x, sizeY - y);
        }

        canvas.repaint();
    }

    public void releaseRectangle() {
        // clear drawing layer
        dLGraphics.setComposite(java.awt.AlphaComposite.Clear);
        dLGraphics.fillRect(0, 0, drawingLayer.getWidth(),
                drawingLayer.getHeight());

        // draw path to commited layer
        if (selectedTool.equals("rectangle")
                || (selectedTool.equals("fillRectangle"))) {
            if (sizeX < x) {
                int oldX = x;
                x = sizeX;
                sizeX = oldX;
            }
            if (sizeY < y) {
                int oldY = y;
                y = sizeY;
                sizeY = oldY;
            }
            cLGraphics.setStroke(stroke);
            cLGraphics.setColor(color);
            if (selectedTool.equals("rectangle"))
                cLGraphics.drawRect(x, y, sizeX - x, sizeY - y);
            else if (selectedTool.equals("fillRectangle"))
                cLGraphics.fillRect(x, y, sizeX - x, sizeY - y);
        }
        canvas.repaint();
    }
}
