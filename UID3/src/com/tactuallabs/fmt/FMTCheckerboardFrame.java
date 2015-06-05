 package com.tactuallabs.fmt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.aliasi.cluster.CompleteLinkClusterer;
import com.aliasi.cluster.Dendrogram;
import com.aliasi.cluster.LeafDendrogram;
import com.aliasi.cluster.LinkDendrogram;
import com.aliasi.cluster.SingleLinkClusterer;
import com.aliasi.matrix.Matrix;
import com.aliasi.matrix.ProximityMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.awt.image.BufferStrategy;

public class FMTCheckerboardFrame extends JFrame implements ActionListener,
		IFMTEventHandler, KeyListener,UIDControlListener {
	/**
	 * 
	 */
	
	private class SavedPositionData{
		int x;
		int y;
		Color groupColor;
		
		public SavedPositionData(int x, int y, Color groupColor) {
			this.x = x;
			this.y = y;
			this.groupColor = groupColor;
		}
		
		public void setColor(Color groupColor){
			this.groupColor = groupColor;
		}
	}
	
	ArrayList<SavedPositionData> arrayListRed = new ArrayList<SavedPositionData>();
	ArrayList<SavedPositionData> arrayListGreen = new ArrayList<SavedPositionData>();
	ArrayList<SavedPositionData> arrayListBlue = new ArrayList<SavedPositionData>();
	ArrayList<SavedPositionData> arrayListYellow = new ArrayList<SavedPositionData>();
	ArrayList<SavedPositionData> arrayListCyan = new ArrayList<SavedPositionData>();
	ArrayList<SavedPositionData> arrayListWhite = new ArrayList<SavedPositionData>();
	// abc.add(new SavedPositionData());
	
	
	private static final long serialVersionUID = 1999858798127882197L;
	protected static final int FRAME_WIDTH = 1024;
	protected static final int FRAME_HEIGHT = 768;
	Panel panel;
	Timer timer;
	int tempRedx = 0;
	int tempRedy = 0;
	int tempGreenx = 0;
	int tempGreeny = 0;
	FMTFrame m_TouchFrameAve = FMTFrame.HACK_GENERATE_RANDOM_FRAME();
	FMTFrame m_TouchFrameAveTemp = new FMTFrame();
	// FMTFrame m_CurrentFrame = FMTFrame.HACK_GENERATE_RANDOM_FRAME();
	long m_LastTime = System.currentTimeMillis();
	int m_FrameCount = 0;
	boolean flag_0 = false;
	boolean flag_1 = false;
	static final int RENDER_TIME_MS = 1000 / 60;
	static final int FRAMES_BETWEEN_FPS_MEASUREMENTS = 1000 / RENDER_TIME_MS * 5;

	public FMTCheckerboardFrame() {
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		timer = new Timer(RENDER_TIME_MS, this); // @ First param is the delay
													// (in milliseconds)
													// therefore this animation
													// is updated every 15 ms.
													// The shorter the delay,
													// the faster the animation.
		this.addKeyListener(this);

		UIDControlPanel cp = new UIDControlPanel(this);
		cp.setBounds(100, 100, 200, 200);
		cp.pack();
		cp.setVisible(true);
		//createBufferStrategy(2);
	}

	float cellWidth = (float) FRAME_WIDTH / FMTFrame.NUM_COLS;
	float cellHeight = (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS;
	private boolean m_CaptureTouchSignals = false;
	private int m_NumCapturedTouchSignals = 0;
	private final static int MAX_CAPTURED_SIGNALS = 3000;
	private long[][][] m_CapturedSignals = new long[MAX_CAPTURED_SIGNALS][FMTFrame.NUM_COLS][FMTFrame.NUM_ROWS];

	public void drawRect(Graphics g, int xInd, int yInd, Color inColor,
			boolean fill) {

		cellWidth = 50 *(float) m_TouchFrameAve.getSignalStrength(xInd,
				yInd) / FMTFrame.MAX_SIGNAL;
		cellHeight = cellWidth;
		int xpos = (int) (xInd * (float) FRAME_WIDTH / FMTFrame.NUM_COLS);
		int ypos = (int) (yInd * (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS);
		g.setColor(inColor);
		
		// This is the code for small cells
		if (fill)
			g.fillRect(xpos, FRAME_HEIGHT - ypos - (int) cellHeight,
					(int) cellWidth, (int) cellHeight);
		else
			g.drawRect(xpos, FRAME_HEIGHT - ypos - (int) cellHeight,
					(int) cellWidth, (int) cellHeight);

	}
	
	public void drawArc(Graphics g, int xInd, int yInd, Color inColor,
			boolean fill) {

		cellWidth = 50 *(float) m_TouchFrameAve.getSignalStrength(xInd,
				yInd) / FMTFrame.MAX_SIGNAL;
		cellHeight = cellWidth;
		int xpos = (int) (xInd * (float) FRAME_WIDTH / FMTFrame.NUM_COLS);
		int ypos = (int) (yInd * (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS);
		g.setColor(inColor);
		g.fillArc(xpos, FRAME_HEIGHT - ypos - (int) cellHeight,
					(int) cellWidth, (int) cellHeight,0,360);

	}
	
	public void drawLine(Graphics g, int xInd1, int yInd1, int xInd2, int yInd2, Color inColor){
		int xpos1 = (int) (xInd1 * (float) FRAME_WIDTH / FMTFrame.NUM_COLS);
		int ypos1 = (int) (yInd1 * (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS);
		int xpos2 = (int) (xInd2 * (float) FRAME_WIDTH / FMTFrame.NUM_COLS);
		int ypos2 = (int) (yInd2 * (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS);
		g.setColor(inColor);
		g.drawLine(xpos1, FRAME_HEIGHT - ypos1 - (int) cellHeight, xpos2, FRAME_HEIGHT - ypos2 - (int) cellHeight);
	}
	
	public void drawOval(Graphics g, FMTBlob blob, Color inColor, boolean fill) {
		int xpos = (int) (blob.getBounds().x * cellWidth);
		int ypos = (int) (blob.getBounds().y * cellHeight);
		g.setColor(inColor);
		if (fill)
			g.fillOval(xpos, FRAME_HEIGHT - ypos - (int) cellHeight,
					(int) (blob.getBounds().width * cellWidth),
					(int) (blob.getBounds().height * cellHeight));
		else
			g.drawOval(xpos, FRAME_HEIGHT - ypos - (int) cellHeight,
					(int) (blob.getBounds().width * cellWidth),
					(int) (blob.getBounds().height * cellHeight));

	}
	
	public void refresh(Graphics g){
		g.setColor(new Color(0f, 0f, 0f, 1));
		g.fillRect(0,0,FRAME_WIDTH, FRAME_HEIGHT);
	}

	
	
	private List<Color> m_AvailableUserColors = new ArrayList<Color>();
	private double m_SignalMean;
	private double m_SignalStDev;
	private List<FMTBlob> m_PreviousFrameBlobs;
	public List<FMTBlob> m_TouchBlobs3 = new ArrayList<FMTBlob>();
	private Image m_OffscreenBuffer;

	public void paintme(Graphics g) // The JPanel paint method we
	// are overriding.
	{
		// comment for double buffering 
		//Graphics x = g;
		//BufferStrategy bs = getBufferStrategy();
		//g = bs.getDrawGraphics();
		
		synchronized (m_TouchFrameAve) {

			if (m_CaptureTouchSignals) {
				return;
			}

			Graphics2D g2d = (Graphics2D) g;
			// m_TouchFrame = m_CurrentFrame;

			g2d.setColor(new Color(0f, 0f, 0f, 0f));
			g2d.fillRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT); // Setting panel
			// background

			float w = (float) FRAME_WIDTH / FMTFrame.NUM_COLS;
			float h = (float) FRAME_HEIGHT / FMTFrame.NUM_ROWS;
			(g2d).setStroke(new BasicStroke(5));
			
			
			// draw cols and rows
			for (int x = 0; x < FMTFrame.NUM_COLS; x++) {
				for (int y = 0; y < FMTFrame.NUM_ROWS; y++) {
					float color = (float) m_TouchFrameAve.getSignalStrength(x,
							y) / FMTFrame.MAX_SIGNAL;
					if (color > 1)
						color = 1;
					drawRect(g, x, y, new Color(color, color, color), true);
					drawRect(g, x, y, Color.black.darker(), false);
				}
			}
			
		
			// draw the touch points
			// for (int i = 0; i < m_TouchFrame.m_TouchPointIdx; i++) {
			// g.setColor(Color.red);
			// float dia = (float) m_TouchFrame.m_TouchPointStrengths[i]
			// / (float) FMTFrame.MAX_TOUCH_STRENGTH;
			// dia *= 30;
			// Point2D point2 = m_TouchFrame.m_TouchPoints[i];
			//
			// double centerX = (int) (point2.getX() );
			// double centerY = FRAME_HEIGHT - (int) (point2.getY());
			// g.fillOval((int)(centerX- dia / 2d),(int)(centerY - dia /
			// 2d),(int) dia, (int) dia);

			//
			// addPointToRedHistory(centerX,centerY);
			//
			//
			// if(centerX < 0 || centerY < 0) continue;
			//
			// Point peak = new Point();
			// peak.x = (int)(centerX / w);
			// peak.y = FMTFrame.NUM_ROWS-1-(int)(centerY / h); //768/(1024/40)
			// -
			// //System.out.println(peak.x + ":" + peak.y);
			//
			// ///drawRect(g,peak.x,peak.y,Color.red,false);
			//
			// //g.setColor(Color.red);
			// //g.drawRect((int)(peak.x * w),(int)( peak.y * h), (int) w, (int)
			// h);
			//
			// //adjust the peak
			// if(m_TouchFrame.getSignalStrength(peak.x, peak.y) <
			// m_TouchFrame.getSignalStrength(peak.x+1, peak.y)){
			// peak.x++;
			// }
			// if(m_TouchFrame.getSignalStrength(peak.x, peak.y) <
			// m_TouchFrame.getSignalStrength(peak.x-1, peak.y)){
			// peak.x--;
			// }
			// if(m_TouchFrame.getSignalStrength(peak.x, peak.y) <
			// m_TouchFrame.getSignalStrength(peak.x, peak.y+1)){
			// peak.y++;
			// }
			// if(m_TouchFrame.getSignalStrength(peak.x, peak.y) <
			// m_TouchFrame.getSignalStrength(peak.x, peak.y-1)){
			// peak.y--;
			// }
			//
			// ///drawRect(g,peak.x,peak.y,Color.orange,false);
			//
			// //g.setColor(Color.orange);
			// //g.drawRect((int)(peak.x * w),(int)( peak.y * h), (int) w, (int)
			// h);
			// //
			// double xd =
			// Interpolator.interpolatePositionFromTheseStrengths(true,
			// m_TouchFrame.getSignalStrength(peak.x - 1,peak.y),
			// m_TouchFrame.getSignalStrength(peak.x,peak.y),
			// m_TouchFrame.getSignalStrength(peak.x + 1,peak.y),
			// peak.x - 1,
			// peak.x,
			// peak.x + 1);
			// double yd = Interpolator
			// .interpolatePositionFromTheseStrengths(false,
			// m_TouchFrame.getSignalStrength(peak.x,peak.y - 1),
			// m_TouchFrame.getSignalStrength(peak.x,peak.y),
			// m_TouchFrame.getSignalStrength(peak.x,peak.y + 1),
			// peak.y - 1,
			// peak.y,
			// peak.y + 1);
			//
			// //if(Double.isNaN(yd) || Double.isInfinite(yd) ||
			// Double.isNaN(xd) || Double.isInfinite(xd)){
			// if(m_TouchFrame.getSignalStrength(peak.x,peak.y-1) > 10000){
			// @SuppressWarnings("unused")
			// int ted = 9;
			// ted++;
			//
			// //System.out.println(
			// //m_TouchFrame.getSignalStrength(peak.x - 1,peak.y-1) + "\t\t\t"
			// +
			// //m_TouchFrame.getSignalStrength(peak.x,peak.y-1) + "\t\t\t" +
			// //m_TouchFrame.getSignalStrength(peak.x + 1,peak.y-1));
			// //
			// //System.out.println(
			// //m_TouchFrame.getSignalStrength(peak.x - 1,peak.y) + "\t\t\t" +
			// //m_TouchFrame.getSignalStrength(peak.x,peak.y) + "\t\t\t" +
			// //m_TouchFrame.getSignalStrength(peak.x + 1,peak.y));
			// //
			// //System.out.println(
			// //m_TouchFrame.getSignalStrength(peak.x - 1,peak.y+1) + "\t\t\t"
			// +
			// //m_TouchFrame.getSignalStrength(peak.x,peak.y+1) + "\t\t\t" +
			// //m_TouchFrame.getSignalStrength(peak.x + 1,peak.y+1));
			// //System.out.println("==============");
			//

			// }

			// //System.out.println(yd);
			//
			// g.setColor(Color.green);
			// // dia = 10;
			// Point2D point = new Point2D.Double(xd * w,
			// FRAME_HEIGHT - yd * h);
			//
			// //g.drawRect((int)(peak.x * w),(int)( peak.y * w), (int) w, (int)
			// h);
			//
			//
			// //g.fillOval((int) (point.getX() - dia / 2),
			// //(int) (point.getY() - dia / 2),
			// //(int) dia, (int) dia);
			// ////g.fillOval((int) (point.getX() ),
			// ////(int) (point.getY() - dia ),
			// ////(int) dia, (int) dia);
			//
			// addPointToGreenHistory(point.getX() + dia/2, point.getY() +
			// dia/2);
			//
			// }

			//////////////////////////////////////////// Cliff Commented This In ///////////////////////////////
			// draw the blobs and their centers
			 //int dia = 30;
			 //for(FMTBlob blob:m_TouchFrameAve.m_TouchBlobs2){
				// for(FMTPoint point:blob.getPoints()){
					// (g2d).setStroke(new BasicStroke(3));
					 //drawRect(g, point.x, point.y, Color.cyan, false);
					// (g2d).setStroke(new BasicStroke(1));
					// g.setColor(Color.cyan);
					// Point2D.Double center = blob.getWeightedCenter(m_TouchFrameAve);
					// g.fillOval((int)(center.x*w+w  /2) - dia/2, FRAME_HEIGHT -
					// (int)(center.y*h+h/2)-dia/2,(int)dia,(int)dia);
					 //addPointToCyanHistory((int)(center.x*w+w/2), FRAME_HEIGHT -
			 //(int)(center.y*h+h/2));
				 //}
			 //}
			 //////////////////////////////////////////Cliff Commented This In ///////////////////////////////
			
			 
			// finally, paint all blobs in m_TouchFrame.m_TouchBlobs3 and color
			// according to getGroup()
			// This is the part to specify user's identity by drawing in different colors - Franklin
			refresh(g);
			if (m_TouchBlobs3.size() == 0) {
				arrayListRed.clear();
				arrayListGreen.clear();
			}

			for (FMTBlob blob : m_TouchBlobs3) {
				
				if (m_TouchBlobs3.size() == 0){
					arrayListRed.clear();
					arrayListGreen.clear();
				}
				
			 
				//arrayListGreen.clear();
			
				// drawOval(g,blob,blob.getGroupColor(), true);
				finished:
				for (FMTPoint point : blob.getPoints()) {
					//drawArc(g, point.x, point.y, blob.getGroupColor(), false);
					SavedPositionData tempPosition = new SavedPositionData(point.x, point.y , blob.getGroupColor());
					
					if(tempPosition.groupColor == Color.RED){
						
						boolean checkFlag1 = false ;
						if (arrayListRed.size() >= 80){
							for (int j = 60; j < 80; j++){
								if(arrayListRed.get(j).x - tempPosition.x <= 2 && arrayListRed.get(j).x - tempPosition.x >= -2){
									if(arrayListRed.get(j).y - tempPosition.y <= 2 && arrayListRed.get(j).y - tempPosition.y >= -2){
										checkFlag1 = true;
									}
								}
							}
							// if it is wrong
							if (checkFlag1 == false){
								
								tempPosition.setColor(Color.GREEN);
								
								if (arrayListGreen.size() < 80){
									arrayListGreen.add(tempPosition);
								}
								else{
									arrayListGreen.remove(0);
									arrayListGreen.add(tempPosition);
								}
								for (int i = 0; i < arrayListGreen.size(); i++){
									if(flag_1){
										drawLine(g, tempGreenx, tempGreeny, arrayListGreen.get(i).x, arrayListGreen.get(i).y, arrayListGreen.get(i).groupColor);
									}
									flag_1 = true;
									tempGreenx = arrayListGreen.get(i).x;
									tempGreeny = arrayListGreen.get(i).y;
							
								}
								flag_1 = false;
								tempGreenx = 0;
								tempGreeny = 0;
								continue finished;
							}
						}
						else if(arrayListRed.size() > 10 && arrayListRed.size() < 80){
							for (int j = arrayListRed.size() - 10; j < arrayListRed.size(); j++){
								if(arrayListRed.get(j).x - tempPosition.x <= 2 && arrayListRed.get(j).x - tempPosition.x >= -2){
									if(arrayListRed.get(j).y - tempPosition.y <= 2 && arrayListRed.get(j).y - tempPosition.y >= -2){
										checkFlag1 = true;
									}
								}
							}
							// if it is wrong
							if (checkFlag1 == false){
								
								tempPosition.setColor(Color.GREEN);
								
								if (arrayListGreen.size() < 80){
									arrayListGreen.add(tempPosition);
								}
								else{
									arrayListGreen.remove(0);
									arrayListGreen.add(tempPosition);
								}
								for (int i = 0; i < arrayListGreen.size(); i++){
									if(flag_1){
										drawLine(g, tempGreenx, tempGreeny, arrayListGreen.get(i).x, arrayListGreen.get(i).y, arrayListGreen.get(i).groupColor);
									}
									flag_1 = true;
									tempGreenx = arrayListGreen.get(i).x;
									tempGreeny = arrayListGreen.get(i).y;
							
								}
								flag_1 = false;
								tempGreenx = 0;
								tempGreeny = 0;
								continue finished;
							}
						}
						
						
						if (arrayListRed.size() < 80){
							arrayListRed.add(tempPosition);
						}
						else{
							arrayListRed.remove(0);
							arrayListRed.add(tempPosition);
						}
						for (int i = 0; i < arrayListRed.size(); i++){
							if(flag_0){
								drawLine(g, arrayListRed.get(i).x, arrayListRed.get(i).y,tempRedx, tempRedy, arrayListRed.get(i).groupColor);
							}
							
							flag_0 = true;
							tempRedx = arrayListRed.get(i).x;
							tempRedy = arrayListRed.get(i).y;
					
						}
						
						flag_0 = false;
						
						tempRedx = 0;
						tempRedy = 0;
						
					}
					else if(tempPosition.groupColor == Color.GREEN){
						
						boolean checkFlag2 = false ;
						if (arrayListGreen.size() >= 80){
							for (int j = 60; j < 80; j++){
								if(arrayListGreen.get(j).x - tempPosition.x <= 2 && arrayListGreen.get(j).x - tempPosition.x >= -2){
									if(arrayListGreen.get(j).y - tempPosition.y <= 2 && arrayListGreen.get(j).y - tempPosition.y >= -2){
										checkFlag2 = true;
									}
								}
							}
							if (checkFlag2 == false){
								tempPosition.setColor(Color.RED);
								
								if (arrayListRed.size() < 80){
									arrayListRed.add(tempPosition);
								}
								else{
									arrayListRed.remove(0);
									arrayListRed.add(tempPosition);
								}
								for (int i = 0; i < arrayListRed.size(); i++){
									if(flag_0){
										drawLine(g, arrayListRed.get(i).x, arrayListRed.get(i).y,tempRedx, tempRedy, arrayListRed.get(i).groupColor);
									}
									
									flag_0 = true;
									tempRedx = arrayListRed.get(i).x;
									tempRedy = arrayListRed.get(i).y;
							
								}
								
								flag_0 = false;
								
								tempRedx = 0;
								tempRedy = 0;
								continue finished;
							}
							
							
							
						}
						else if (arrayListGreen.size() < 80 && arrayListGreen.size() > 10){
							for (int j = arrayListGreen.size() - 10; j < arrayListGreen.size(); j++){
								if(arrayListGreen.get(j).x - tempPosition.x <= 2 && arrayListGreen.get(j).x - tempPosition.x >= -2){
									if(arrayListGreen.get(j).y - tempPosition.y <= 2 && arrayListGreen.get(j).y - tempPosition.y >= -2){
										checkFlag2 = true;
									}
								}
							}
							if (checkFlag2 == false){
								tempPosition.setColor(Color.RED);
								
								if (arrayListRed.size() < 80){
									arrayListRed.add(tempPosition);
								}
								else{
									arrayListRed.remove(0);
									arrayListRed.add(tempPosition);
								}
								for (int i = 0; i < arrayListRed.size(); i++){
									if(flag_0){
										drawLine(g, arrayListRed.get(i).x, arrayListRed.get(i).y,tempRedx, tempRedy, arrayListRed.get(i).groupColor);
									}
									
									flag_0 = true;
									tempRedx = arrayListRed.get(i).x;
									tempRedy = arrayListRed.get(i).y;
							
								}
								
								flag_0 = false;
								
								tempRedx = 0;
								tempRedy = 0;
								continue finished;
							}
							
						}
						if (arrayListGreen.size() < 80){
							arrayListGreen.add(tempPosition);
						}
						else{
							arrayListGreen.remove(0);
							arrayListGreen.add(tempPosition);
						}
						for (int i = 0; i < arrayListGreen.size(); i++){
							if(flag_1){
								drawLine(g, tempGreenx, tempGreeny, arrayListGreen.get(i).x, arrayListGreen.get(i).y, arrayListGreen.get(i).groupColor);
							}
							flag_1 = true;
							tempGreenx = arrayListGreen.get(i).x;
							tempGreeny = arrayListGreen.get(i).y;
					
						}
						flag_1 = false;
						tempGreenx = 0;
						tempGreeny = 0;
					}
				}
				
				
				
				
				 //m_TouchFrameAveTemp.clearTouchPoints();
			}

			//arrayListRed.clear();
			//arrayListGreen.clear();
			// paint all of the ghosts
//						for (FMTGhost ghost : m_LegalGhosts) {
//							(g2d).setStroke(new BasicStroke(5));
//							Rectangle rect = ghost.m_Rect;
//							for (int x = rect.x; x < rect.x + rect.width; x++) {
//								for (int y = rect.y; y < rect.y + rect.height; y++) {
//										drawRect(g, x, y, Color.white, false);
//								}
//							}
//							rect = ghost.m_RectBot;
//							for (int x = rect.x; x < rect.x + rect.width; x++) {
//								for (int y = rect.y; y < rect.y + rect.height; y++) {
//									if(ghost.useBorderRects())	
//										drawRect(g, x, y, Color.orange, false);
//									else
//										drawRect(g, x, y, Color.pink, false);
//								}
//							}
//						}
			
			//bs.show();
			
			m_FrameCount++;
			if (m_FrameCount == FRAMES_BETWEEN_FPS_MEASUREMENTS) {
				long time = System.currentTimeMillis();
				double frameRate = (double) FRAMES_BETWEEN_FPS_MEASUREMENTS
						/ ((int) (time - m_LastTime) / 1000d);
				System.out.println("FPS: " + frameRate);
				m_LastTime = time;
				m_FrameCount = 0;
			}
		}

	}

	public void run() {

		panel = new Panel() {
			private static final long serialVersionUID = 4269337316975195152L;

			// private FMTFrame m_CurrentFrame = FMTFrame
			// .HACK_GENERATE_RANDOM_FRAME();

			public void update(Graphics g) {
				if (m_OffscreenBuffer == null
						|| m_OffscreenBuffer.getWidth(null) != this.getWidth()
						|| m_OffscreenBuffer.getHeight(null) != this
								.getHeight()) {
					m_OffscreenBuffer = this.createImage(this.getWidth(),
							this.getHeight());
					m_OffscreenBuffer.getGraphics().setColor(Color.black);
					m_OffscreenBuffer.getGraphics().fillRect(0, 0, getWidth(),
							getHeight());
				}
				// paintme(m_OffscreenBuffer.getGraphics());
				g.drawImage(m_OffscreenBuffer, 0, 0, null);
			}

			public void paint(Graphics g) {
				// do nothing
			}

		};
		panel.setPreferredSize(new Dimension(1024, 768)); // Setting the panel
															// size

		getContentPane().add(panel); // Adding panel to frame.
		pack();
		setVisible(true);
		timer.start(); // This starts the animation.

		Thread UDPThread = new Thread(new UDPHandler(this));
		UDPThread.start();

	}

	List<FMTGhost> m_LegalGhosts = new ArrayList<FMTGhost>();
	private double m_SameGroupLastFrameScalar = .5d;

	@SuppressWarnings("unchecked")
	public void sim(FMTFrame inFrame) {

		if (m_CaptureTouchSignals) {
			return;
		}
		
		List<FMTGhost> m_AllGhosts = new ArrayList<FMTGhost>();
		m_LegalGhosts.clear();
		m_TouchBlobs3 = new ArrayList<FMTBlob>();
		m_AvailableUserColors.clear();
		m_AvailableUserColors.add(Color.red);
		m_AvailableUserColors.add(Color.green);
		m_AvailableUserColors.add(Color.blue);
		m_AvailableUserColors.add(Color.yellow);
		m_AvailableUserColors.add(Color.cyan);
		m_AvailableUserColors.add(Color.white);

		inFrame.generateTouchBlobs();

		//handle 1 and 0 touch blobs here
		if(inFrame.m_TouchBlobs2.size() == 0){
			m_PreviousFrameBlobs = inFrame.m_TouchBlobs2;
			return;
		}else if(inFrame.m_TouchBlobs2.size() == 1){
			inFrame.m_TouchBlobs2.get(0).setGroupColor(m_AvailableUserColors.get(0));
			m_TouchBlobs3 = inFrame.m_TouchBlobs2;
			m_PreviousFrameBlobs = inFrame.m_TouchBlobs2;			
			return;
		}
	
		
		
		// System.out.println(m_TouchFrame.m_TouchBlobs2.size());

		

		// generate all ghosts (areas for crosstalk)
		for (int i = 0; i < inFrame.m_TouchBlobs2.size(); i++) {
			for (int j = i + 1; j < inFrame.m_TouchBlobs2.size(); j++) {
				FMTGhost ghost1 = new FMTGhost(inFrame.m_TouchBlobs2.get(i),
						inFrame.m_TouchBlobs2.get(j));
				FMTGhost ghost2 = new FMTGhost(inFrame.m_TouchBlobs2.get(j),
						inFrame.m_TouchBlobs2.get(i));
				m_AllGhosts.add(ghost1);
				m_AllGhosts.add(ghost2);
			}
		}

		//System.out.println("Generated: " + m_AllGhosts.size());
		
		// now remove ghosts that overlap with other ghosts or with touch blobs
		fred: while (m_AllGhosts.size() > 0) {
			FMTGhost canidate = m_AllGhosts.remove(0);
			boolean collides = false;
			for (FMTGhost ghost : m_AllGhosts) {
				if (ghost.m_Rect.intersects(canidate.m_Rect)) {
					collides = true;
					//System.out.println("Collision with Ghost!");
					canidate.informBlobsThatIAmIllegal();
					continue fred;
				}
				if(canidate.borderRectsIntersectRect(ghost.m_Rect)){
					canidate.setUseBorderRects(false);
				}
			}

			for (FMTBlob blob : inFrame.m_TouchBlobs2) {
				if (blob.intersectsGhost(canidate.m_Rect)) {
					collides = true;
					//System.out.println("Collision with Touch!");
					canidate.informBlobsThatIAmIllegal();
					continue fred;
				}
				if(canidate.borderRectsIntersectRect(blob.getBoundsForGhostIntersection())){
					canidate.setUseBorderRects(false);
				}
			}
			if(collides == false){
				m_LegalGhosts.add(canidate);
			}
		}

		//System.out.println("After Prune: " + m_LegalGhosts.size());
		
		
		
		
		
		
		//create a proximity matrix that holds the similarity among touch blobs
		Object[] labels = new Object[inFrame.m_TouchBlobs2.size()];
        for(int i=0;i<labels.length;i++){
        	labels[i] = ""+i;
        	inFrame.m_TouchBlobs2.get(i).setMatrixIdx(i);
        }
		ProximityMatrix matrix = new ProximityMatrix(labels);
		
		//make blobs a lot "like" themselves by making all other combinations larger
		for(int i=0;i<labels.length;i++){
			for(int j=i+1;j<labels.length;j++){
				matrix.setValue(i,j,0);
			}
        }
        
		//prettyPrint(matrix);
		
		
		if(m_PreviousFrameBlobs != null){
			//assign each of the blobs this frame a previousFrame blob (tracking)
			// ugg N^2 again
			List<FMTBlobPairing> m_AllPairs = new ArrayList<FMTBlobPairing>();
			for(FMTBlob current:inFrame.m_TouchBlobs2){
				for(FMTBlob previous:m_PreviousFrameBlobs){
					FMTBlobPairing pair = new FMTBlobPairing(current,previous,inFrame);
					m_AllPairs.add(pair);
				}
			}
			Collections.sort(m_AllPairs);
	//		for(FMTBlobPairing pair:m_AllPairs){
	//			pair.prettyPrint();
	//		}
			while(m_PreviousFrameBlobs.size() > 0){
				FMTBlobPairing pair = m_AllPairs.remove(0);
				if(m_PreviousFrameBlobs.contains(pair.getBlob2())){
					pair.getBlob1().setPreviousBlob(pair.getBlob2());
					//TODO get color and set it here??
					pair.getBlob1().setGroupColor(pair.getBlob2().getGroupColor());
					
					m_PreviousFrameBlobs.remove(pair.getBlob2());
				}
			}  
		}
		
		
        // if two blobs were not in the same group last time, increase their dissimilarity
		for(int i=0;i<inFrame.m_TouchBlobs2.size();i++){
			FMTBlob blob = inFrame.m_TouchBlobs2.get(i);
			if(blob.getPreviousBlob() == null){
				continue;
			}
			for(int j=i+1;j<inFrame.m_TouchBlobs2.size();j++){
				FMTBlob blob2 = inFrame.m_TouchBlobs2.get(j);
				if(blob2.getPreviousBlob() == null){
					continue;
				}
				
				if(blob.getPreviousBlob().getGroupIdx() != blob2.getPreviousBlob().getGroupIdx()){
					matrix.setValue(i, j, matrix.value(i, j)+m_SameGroupLastFrameScalar );
				}
			}
		}
        
		double crossTalkThresh = this.m_SignalMean + this.m_SignalStDev / 3d;
		
		for(FMTGhost ghost:m_LegalGhosts){
			
			// TODO think more about density of blobs associated with this ghost
			double ave = getAverageSignalInRect(ghost.m_Rect);
			
			
			if(ghost.useBorderRects()){ // if we can use the border rects, then get their aveage and adjust ave by it
				double ave1 = getAverageSignalInRect(ghost.m_RectTop);
				double ave2 = getAverageSignalInRect(ghost.m_RectBot);
				ave /= ((ave1+ave2)/2);
			}else{
				ave /= this.m_SignalMean;
			}
			  
			
			
			
			ghost.setAverageSignal(ave);
			//System.out.println("A: " + ave);
		}
		
		// for all pairs of blobs, see what the legal ghosts can tell us about crosstalk
		// if there are no ghosts, then it tells us nothing and we do not adjust similarity (just add 1)
		// if there are 1 or more ghosts, then we look toward the presence or absence of crosstalk averaged by the number of ghosts
		
		for(int i=0;i<inFrame.m_TouchBlobs2.size();i++){
			for(int j=i+1;j<inFrame.m_TouchBlobs2.size();j++){
				FMTBlob blob1 = inFrame.m_TouchBlobs2.get(i);
				FMTBlob blob2 = inFrame.m_TouchBlobs2.get(j);
				
				int ghostCount = 0;
				double sum = 0;
				for(FMTGhost ghost:m_LegalGhosts){
					if(ghost.hasParents(blob1,blob2)){
						ghostCount++;
						sum = Math.max(sum,ghost.getAverageSignal());
					}
				}
				
				if(ghostCount == 0){
					matrix.setValue(
							blob1.getMatrixIdx(), 
							blob2.getMatrixIdx(), 
							matrix.value(blob1.getMatrixIdx(), blob2.getMatrixIdx())+m_ShadowScalar);
				}else{
					//sum /= ghostCount;
					matrix.setValue(
							blob1.getMatrixIdx(), 
							blob2.getMatrixIdx(), 
							matrix.value(blob1.getMatrixIdx(), blob2.getMatrixIdx())+m_ShadowScalar*(1d/sum));
				}
			}
		}
		
//		// if two blobs have NO crosstalk, then increase their dissimilarity
//		for(FMTGhost ghost:m_LegalGhosts){
//	
//			double density = ghost.getDensityOfParents(inFrame);
//			//System.out.println("D: " + density);
//			double ave = getAverageSignalInRect(ghost.m_Rect) / 1; // TODO think more about density of blobs associated with this ghost
//										
//			ave /= this.m_SignalMean;
//			System.out.println("A: " + ave);
//			
//			matrix.setValue(
//					ghost.m_Parent1.getMatrixIdx(), 
//					ghost.m_Parent2.getMatrixIdx(), 
//					matrix.value(ghost.m_Parent1.getMatrixIdx(), ghost.m_Parent2.getMatrixIdx())-ave);
//			
////			if (ave < crossTalkThresh) { // TODO use a different thresh if
////											// looking at ghost to the left of a
////											// touch point vs. right of a touch
////											// point.
////				matrix.setValue(
////						ghost.m_Parent1.getMatrixIdx(), 
////						ghost.m_Parent2.getMatrixIdx(), 
////						matrix.value(ghost.m_Parent1.getMatrixIdx(), ghost.m_Parent2.getMatrixIdx())+1);
////			}
//			
//		}
        
		
		//prettyPrint(matrix);
		
		CompleteLinkClusterer clusterer 
	    = new CompleteLinkClusterer(m_Partition);
        Dendrogram d = clusterer.completeCluster(matrix);
        //System.out.println("Single Link Dendrogram=" + d);
        System.out.println("Pretty Printed=" + d.prettyPrint());
        
        //If the cost is below 1.25, then the sub parts of the d are in the same group!
        recursivelyAssignBlobsToGroups(m_Partition,inFrame.m_TouchBlobs2,d,0);
        
        for(FMTBlob blob:inFrame.m_TouchBlobs2){
        	if (blob.getGroupIdx() < m_AvailableUserColors.size()) {
        	blob.setGroupColor(m_AvailableUserColors.get(blob.getGroupIdx()));
        	}
        }
        
//        Set[] partitions = clusterer.cluster(matrix);
//        System.out.println("Partitions < infinity =" 
//                           + Arrays.asList(partitions));
//
//        clusterer.setProximityBound(3.0);
//	partitions = clusterer.cluster(matrix);
//	System.out.println("Partitions < 3.0 =" + Arrays.asList(partitions));
//
//        clusterer.setProximityBound(2);
//	partitions = clusterer.cluster(matrix);
//	System.out.println("Partitions < 2 =" + Arrays.asList(partitions));
//	for(int i=0;i<partitions.length;i++){
//		Set<String> set = partitions[i];
//		Iterator<String> itr = set.iterator();
//		while(itr.hasNext()){
//			String string = itr.next();
//			FMTBlob blob =inFrame.m_TouchBlobs2.get(Integer.parseInt(string)); 
//			blob.setGroupIdx(i);
//			blob.setGroupColor(m_AvailableUserColors.get(i));
//		}
//	}
//
//        clusterer.setProximityBound(0.5);
//	partitions = clusterer.cluster(matrix);
//	System.out.println("Partitions < 0.5 =" + Arrays.asList(partitions));

//        for (int i = 1; i <=  inFrame.m_TouchBlobs2.size(); ++i) {
//            Dendrogram[] dendros = d.partition(i);
//            System.out.println(i + " partitions=" + Arrays.asList(dendros));
//        }
		
		// now color all the blobs by the group they were in the previous frame,
		// if they overlap
//		if (m_PreviousFrameBlobs != null) {
//			for (FMTBlob previous : m_PreviousFrameBlobs) {
//				for (FMTBlob current : inFrame.m_TouchBlobs2) {
//					if (current.intersects(previous)) {
//						if (current.getGroupColor() == null) {
//							current.setGroupColor(previous.getGroupColor());
//							m_AvailableUserColors.remove(previous
//									.getGroupColor());
//						}
//					}
//				}
//			}
//		}

		

		m_PreviousFrameBlobs = inFrame.m_TouchBlobs2;
		m_TouchBlobs3 = inFrame.m_TouchBlobs2;

	}

	private int recursivelyAssignBlobsToGroups(double costThresh,
			List<FMTBlob> touchBlobs, Dendrogram dend, int groupIndex) {
		
		if(dend.cost() < costThresh){
			recursivelyAssignAllBlobsInDendToGroup(touchBlobs,dend,groupIndex++);
		}else{
			Dendrogram[] daughters = ((LinkDendrogram)dend).daughters();
			for(int i=0;i<daughters.length;i++){
				groupIndex = recursivelyAssignBlobsToGroups(costThresh,touchBlobs,daughters[i],groupIndex);
			}
		}
	
		return groupIndex;
	}

	private void recursivelyAssignAllBlobsInDendToGroup(List<FMTBlob> touchBlobs,
			Dendrogram dend, int groupIndex) {
		if(dend.size() == 1){ // this is a leaf
			String s = (String)((LeafDendrogram)dend).object();
			FMTBlob blob =touchBlobs.get(Integer.parseInt(s)); 
			blob.setGroupIdx(groupIndex);
		}else{
			Dendrogram[] daughters = ((LinkDendrogram)dend).daughters();
			for(int i=0;i<daughters.length;i++){
				recursivelyAssignAllBlobsInDendToGroup(touchBlobs,daughters[i],groupIndex);
			}
		}
	}

	public static void prettyPrint(Matrix matrix) {
        System.out.print(" ");
        for (int j = 0; j < matrix.numColumns(); ++j)
            System.out.print("    " + matrix.columnLabel(j));
        System.out.println();
	for (int i = 0; i < matrix.numRows(); ++i) {
	    System.out.print(matrix.rowLabel(i));
	    for (int j = 0; j < matrix.numColumns(); ++j) {
		System.out.print("  " + matrix.value(i,j));
	    }
	    System.out.println();
	}
    }
	
	private void checkForCrosstalkAndReconsileGroups(FMTBlob blob,
			FMTFrame inFrame) {
		// HACK!
		double crossTalkThresh = this.m_SignalMean + this.m_SignalStDev / 3d;
		//System.out.println(crossTalkThresh);
		for (FMTGhost ghost : blob.m_Ghosts) {
			double density = ghost.getDensityOfParents(inFrame);
			//System.out.println("D: " + density);
			double ave = getAverageSignalInRect(ghost.m_Rect) / 1; // TODO
																			// need
																			// to
																			// change
																			// this
																			// value
																			// based
																			// on
																			// how
																			// dense
																			// the
																			// blob
																			// is
																			// filling
																			// it's
																			// rect
			//System.out.println("A: " + ave);
			if (ave > crossTalkThresh) { // TODO use a different thresh if
											// looking at ghost to the left of a
											// touch point vs. right of a touch
											// point.
				// get ghosts other parent and assign to blob.getGroup();
				// if(ghost.getOtherParent(blob).getGroupColor() == null)
				ghost.getOtherParent(blob).setGroupColor(blob.getGroupColor());
			}
		}
	}

	private boolean crossTalk(FMTBlob blob1, FMTBlob blob2,
			List<FMTBlob> inBlobsToAvoid, List<FMTBlob> inBlobsToAvoid2) {

		int numberOfGhostRegions = 2; // we start with 2 possible areas where
										// crosstalk might occur

		int x1 = blob1.getBounds().x;
		int x2 = blob2.getBounds().x;
		int y1 = blob1.getBounds().y;
		int y2 = blob2.getBounds().y;
		int w1 = blob1.getBounds().width;
		int w2 = blob2.getBounds().width;
		int h1 = blob1.getBounds().height;
		int h2 = blob2.getBounds().height;

		Rectangle ghost1Rect = new Rectangle(x2, y1, w2, h1);
		Rectangle ghost2Rect = new Rectangle(x1, y2, w1, h2);

		// now we need to see if these ghost rects intersect with any of the
		// "real" touch blobs - were that to happen, then we would see crosstalk
		// that may or may not be there
		for (FMTBlob blob : inBlobsToAvoid) {
			if (blob.intersectsGhost(ghost1Rect)) {
				ghost1Rect = null;
				numberOfGhostRegions--;
			}
			if (blob.intersectsGhost(ghost2Rect)) {
				ghost2Rect = null;
				numberOfGhostRegions--;
			}
		}
		for (FMTBlob blob : inBlobsToAvoid2) {
			if (blob.intersectsGhost(ghost1Rect)) {
				ghost1Rect = null;
				numberOfGhostRegions--;
			}
			if (blob.intersectsGhost(ghost2Rect)) {
				ghost2Rect = null;
				numberOfGhostRegions--;
			}
		}

		// HACK!
		double crossTalkThresh = this.m_SignalMean + this.m_SignalStDev / 2;

		// OK - finally, we check the signal in the remaining ghost rects and
		// see if it's above threshold
		if (numberOfGhostRegions == 2) {
			long average1 = getAverageSignalInRect(ghost1Rect);
			long average2 = getAverageSignalInRect(ghost2Rect);
			if (average1 > crossTalkThresh && average2 > crossTalkThresh)
				return true;
			else
				return false;
		} else if (numberOfGhostRegions == 1) {
			long average1 = getAverageSignalInRect(ghost1Rect);
			long average2 = getAverageSignalInRect(ghost2Rect);
			if (average1 > crossTalkThresh || average2 > crossTalkThresh)
				return true;
			else
				return false;
		} else {
			return false; // there is no way to check if there is crosstalk in
							// this case
		}

	}

	private long getAverageSignalInRect(Rectangle rect) {
		if (rect == null)
			return 0;
		long sum = 0;
		long count = 0;
		for (int x = rect.x; x <= rect.x + rect.width; x++) {
			for (int y = rect.y; y <= rect.y + rect.height; y++) {
				sum += m_TouchFrameAve.getSignalStrength(x, y);
				count++;
			}
		}
		return sum / count;
	}

	private long getMaxSignalInRect(Rectangle rect) {
		if (rect == null)
			return 0;
		long max = 0;
		for (int x = rect.x; x <= rect.x + rect.width; x++) {
			for (int y = rect.y; y <= rect.y + rect.height; y++) {
				max = Math.max(max, m_TouchFrameAve.getSignalStrength(x, y));
			}
		}
		return max;
	}
	
	public void actionPerformed(ActionEvent e) {
		panel.repaint(); // This is what paints the animation again (IMPORTANT:
							// won't work without this).
		panel.revalidate(); // This isn't necessary, I like having it just in
							// case.
	}

	int frameCount = 0;
	boolean m_AreTouches = false;
	private boolean m_CaptureBackground = true; // when true we capture stats on the background noise
	private UIDControlListener m_UIDListener;
	private double m_Partition = 2.75;
	private double m_ShadowScalar = 3;
	public void setCurrentFrame(FMTFrame inCurrentFrame) {
		// add the signals from inCurrentFrame to m_TouchFrameTemp
		m_TouchFrameAveTemp.addSignals(inCurrentFrame);
		m_TouchFrameAveTemp.maxSignals(inCurrentFrame);
		m_AreTouches = m_AreTouches | (m_TouchFrameAveTemp.m_TouchPointIdx > 0);

		if (frameCount++ == 10) {
			synchronized (m_TouchFrameAve) {
				m_TouchFrameAve = m_TouchFrameAveTemp;
				m_TouchFrameAve.normalizeSignalsOverTime();
				m_TouchFrameAveTemp = new FMTFrame();
				if (!m_AreTouches && m_CaptureBackground) {
					Statistics stats = new Statistics(
							m_TouchFrameAve.m_SignalStrengths,
							FMTFrame.NUM_COLS, FMTFrame.NUM_ROWS);
					m_SignalMean = stats.getMean();
					m_SignalStDev = stats.getStdDev();
					FMTFrame.TOUCH_THRESHOLD_1 = Math.max(FMTFrame.TOUCH_THRESHOLD_1,(int) (m_SignalMean + 5*m_SignalStDev));
					FMTFrame.TOUCH_THRESHOLD_2 = (int)(FMTFrame.TOUCH_THRESHOLD_1*.9);
					//m_UIDListener.onThresh1Update(FMTFrame.TOUCH_THRESHOLD_1);
					//m_UIDListener.onThresh2Update(FMTFrame.TOUCH_THRESHOLD_2);
					m_CaptureBackground = false;
				}
				sim(m_TouchFrameAve);
				paintme(m_OffscreenBuffer.getGraphics());
			}
			frameCount = 0;
			m_AreTouches = false;
		}

		if (m_CaptureTouchSignals) {

			m_CapturedSignals[m_NumCapturedTouchSignals] = inCurrentFrame.m_SignalStrengths
					.clone();
			m_NumCapturedTouchSignals++;

			if (m_NumCapturedTouchSignals >= MAX_CAPTURED_SIGNALS) {
				// write to files
				this.setVisible(false);
				for (int i = 0; i < MAX_CAPTURED_SIGNALS; i++) {
					long[][] valuesThisFrame = m_CapturedSignals[i];
					String s = "";
					for (int row = 0; row < FMTFrame.NUM_ROWS; row++) {
						for (int col = 0; col < FMTFrame.NUM_COLS; col++) {
							s = s + valuesThisFrame[col][row];
							s = s + ",";
						}
						s = s + "\n";
					}

					try {
						String num = "" + i;
						if (i < 10) {
							num = "000" + num;
						} else if (i < 100) {
							num = "00" + num;
						} else if (i < 1000) {
							num = "0" + num;
						}
						PrintWriter out = new PrintWriter("./output/" + num
								+ "_out.csv");
						out.print(s);
						out.flush();
						out.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// System.out.println( s );
				}

				System.exit(0);
			}
			return;
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		m_CaptureTouchSignals = true;
		// String s = m_TouchFrame.getSignalsAsString();
		// System.out.println(s);
		// System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPartitionUpdate(double in) {
		m_Partition = in;
	}

	@Override
	public void onShadowScalarUpdate(double in) {
		m_ShadowScalar = in;
	}
	
	@Override
	public void onSameGroupLastFrameScalarUpdate(double in){
		m_SameGroupLastFrameScalar = in;
	}

	@Override
	public void onThresh1Update(int in) {
		FMTFrame.TOUCH_THRESHOLD_1 = in;
	}

	@Override
	public void onThresh2Update(int in) {
		FMTFrame.TOUCH_THRESHOLD_1 = in;
	}

	@Override
	public void onSampleThreshPress() {
		m_CaptureBackground  = true;
	}
}
