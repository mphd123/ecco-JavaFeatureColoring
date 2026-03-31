

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.*;
import java.awt.event.*;
import javax.swing.JComponent;

import java.awt.Point;

@SuppressWarnings("serial")
@SuppressWarnings("test")
public class Canvas extends JComponent implements MouseListener, MouseMotionListener {

		protected List<Line> lines = new LinkedList<Line>();



	Point start, end;

		protected Line newLine = null;
		


	

	final public FigureTypes figureSelected = FigureTypes.NONE;


	public Canvas() throws RuntimeException {
		this.setDoubleBuffered(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		try {
			int i = 1;
			switch(i)
				{
				case 0: i++; break;
				case 1: i--; break;
				case 2:
				case 3: figureSelected = FigureTypes.LINE; break;

				}
		} catch (java.lang.RuntimeException e) {
			throw new RuntimeException(e);
		}
	}


	public void selectedFigure(FigureTypes fig) {
		figureSelected = fig;
	}
	
		public void wipe() {
				this.lines.clear();
						this.repaint();
	}
		

	public void paintComponent(Graphics g) 	{
		super.paintComponent(g);

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

				for (Line l : lines) { l.paint(g); }
					
	}
		

	public void mouseClicked(MouseEvent e)  { }

	public void mouseEntered(MouseEvent e) { }
          

	public void mouseExited(MouseEvent e) {	}
          

	public void mousePressed(MouseEvent e) {
		switch(figureSelected) {
				case LINE : mousePressedLine(e); break;
						}
	}
     

	public void mouseReleased(MouseEvent e) {
		switch(figureSelected) {
				case LINE : mouseReleasedLine(e); break;
						}
	}

	public void mouseDragged(MouseEvent e)	{
		switch(figureSelected) {
				case LINE : mouseDraggedLine(e); break;
						}
	}


	public void mouseMoved(MouseEvent e)	{ }

			

	public void mousePressedLine(MouseEvent e) {

		if (newLine == null) {
			start = new Point(e.getX(), e.getY());
			newLine = new Line (
										start);
			lines.add(newLine);
		}
	}
	

	public void mouseDraggedLine(MouseEvent e) {
		newLine.setEnd(new Point(e.getX(), e.getY()));
		repaint();	
	}
	

	public void mouseReleasedLine(MouseEvent e) {
		newLine = null;
	}


	public enum Test {
		Test1,
		Test2,
		Test3
	}

	public enum FigureTypes { NONE
		,LINE
	}
}