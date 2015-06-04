package com.tactuallabs.fmt;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class FMTFrame {
	static long MAX_SIGNAL = 100000; //10000000
	static int MAX_TOUCH_STRENGTH = 4;
	static int TOUCH_THRESHOLD_1 = 3200000; // TODO adjust this value inteligently
	static int TOUCH_THRESHOLD_2 = 3000000;
//	static int TOUCH_THRESHOLD_1 = 1200000;
//	static int TOUCH_THRESHOLD_2 = 800000;	
	static final int NUM_COLS = 40;
	static final int NUM_ROWS = 30;
	static final int NUM_SIGNALS = NUM_COLS*NUM_ROWS;
	long[][] m_SignalStrengths =   new long[NUM_COLS][NUM_ROWS];
	long[][] m_SignalStrengthsMax = new long[NUM_COLS][NUM_ROWS];
	Point2D.Double[] m_TouchPoints = new Point2D.Double[64];
	int[] m_TouchPointStrengths = new int[64];
	int m_TouchPointIdx = 0;
	int m_NumFramesAveraged = 0;
	
	public String getSignalsAsString(){
		String s = "";
		for(int row = 0;row<NUM_ROWS;row++){
			for(int col = 0; col<NUM_COLS;col++){
				s = s + m_SignalStrengths[col][row];
				s = s + ",";
			}
			s = s + "\n";
		}
		
		return s;
	}
	
	public List<FMTBlob> m_TouchBlobs2 = new ArrayList<FMTBlob>();

	
	public void generateTouchBlobs(){
		
		List<FMTBlob> touchBlobs = new ArrayList<FMTBlob>();
		
		List<FMTPoint> m_PointsToConsider = new ArrayList<FMTPoint>();
		for(int x = 0;x<NUM_COLS;x++){
			for(int y = 0;y<NUM_ROWS;y++){
				m_PointsToConsider.add(new FMTPoint(x,y));
			}
		}
		
		ted:
		while(m_PointsToConsider.size() > 0){
			FMTPoint point = m_PointsToConsider.remove(0);
			
			// if the point is below threshold, continue to the next point
			if(m_SignalStrengthsMax[point.x][point.y] < TOUCH_THRESHOLD_1)
				continue ted;
			
			// check if it's next to an existing blob
			for(FMTBlob blob:touchBlobs){
				if(blob.adjacentToPoint(point)){
					blob.addPoint(point);
					continue ted;
				}
			}
			
			// this point is above threshold, and not next to a blob, so make a new blob and add this point
			FMTBlob blob = new FMTBlob();
			blob.addPoint(point);
			touchBlobs.add(blob);
		}
		
		// make this a 2-pass operation, adding points to existing blobs only
		
		for(int x = 0;x<NUM_COLS;x++){
			for(int y = 0;y<NUM_ROWS;y++){
				m_PointsToConsider.add(new FMTPoint(x,y));
			}
		}
		ted:
		while(m_PointsToConsider.size() > 0){
			FMTPoint point = m_PointsToConsider.remove(0);
			
			// if the point is below threshold, continue to the next point
			if(m_SignalStrengthsMax[point.x][point.y] < TOUCH_THRESHOLD_2)
				continue ted;
			
			// check if it's next to an existing blob
			for(FMTBlob blob:touchBlobs){
				if(blob.adjacentToPoint(point)){
					blob.addPoint(point);
					continue ted;
				}
			}
			
			// DO NOT MAKE NEW BLOBS IN THIS SECOND PASS!
		}
		
		// combine overlapping blobs
		ted:
		while(touchBlobs.size() > 0){
			FMTBlob blob = touchBlobs.remove(0);
			boolean isIndependent = true;
			for(int i=0;i<touchBlobs.size();i++){
				if(blob.intersects(touchBlobs.get(i))){
					isIndependent = false;
					blob = FMTBlob.combineBlobs(touchBlobs.get(i), blob);
					touchBlobs.remove(i);
					touchBlobs.add(blob);
					continue ted;
				}
			}
			if(isIndependent){
				m_TouchBlobs2.add(blob);
			}
			
		}
			
		
	}
	
	static FMTFrame HACK_GENERATE_RANDOM_FRAME(){
		FMTFrame frame = new FMTFrame();
		for(int i=0;i<NUM_COLS;i++){
			for(int j=0;j<NUM_ROWS;j++){
				frame.setSignalValue(i,j,((long)(Math.random()*1024)));
			}
		}
		for(int i=0;i<10;i++){
			frame.addPoint((int)(Math.random()*1024),(int)(Math.random()*768),(int)(Math.random()*512));
		}
		return frame;
	}

	public void clearTouchPoints(){
		m_TouchPoints = new Point2D.Double[64];
		m_TouchPointIdx = 0;
		m_TouchPointStrengths = new int[64];
	}
	
	public  void addPoint(double x, double y, int z) {
		m_TouchPoints[m_TouchPointIdx] = new Point2D.Double(x,y);
		m_TouchPointStrengths[m_TouchPointIdx] = z;
		m_TouchPointIdx++;
		//System.out.println(x + " = " + y + " = " + z);
		//System.out.println(MAX_TOUCH_STRENGTH);
		if(MAX_TOUCH_STRENGTH < z)
			MAX_TOUCH_STRENGTH = z;
	}

//	public void setSignalValue(int idx, int val) {
//		m_Signals[idx] = val;
//		if(val > MAX_SIGNAL)
//			MAX_SIGNAL = val;
//	}
	public void setSignalValue(int x, int y, long val) {
		//System.out.println(x + ":" + y + ":" + val);
		
		//if(y<0 || x < 0) return;
		// HACK find out why this is wrong...
		try{
			m_SignalStrengths[x][y] = val;
		}catch(ArrayIndexOutOfBoundsException e){
			// HACK find out why this is wrong...
			return;
		}
		if(val > MAX_SIGNAL)
			MAX_SIGNAL = val;
	}

	public long getSignalStrength(int x, int y) {
		if(x < 0 || y < 0) return 0;
		if(x >= FMTFrame.NUM_COLS || y >= FMTFrame.NUM_ROWS) return 0;	
		
//		if(x == 0 && y == 0)
//			System.out.println(m_SignalStrengths[x][y]);
//		
		return this.m_SignalStrengths[x][y];
	}
	
	public long getMaxSignalStrength(int x, int y) {
		if(x < 0 || y < 0) return 0;
		if(x >= FMTFrame.NUM_COLS || y >= FMTFrame.NUM_ROWS) return 0;	
		
//		if(x == 0 && y == 0)
//			System.out.println(m_SignalStrengths[x][y]);
//		
		return this.m_SignalStrengthsMax[x][y];
	}

	public void addSignals(FMTFrame inFrame) {
		m_NumFramesAveraged++;
		for(int i=0;i<NUM_COLS;i++){
			for(int j=0;j<NUM_ROWS;j++){
				m_SignalStrengths[i][j] += inFrame.getSignalStrength(i,j);
				//m_SignalStrengths[i][j] = Math.max(m_SignalStrengths[i][j], inFrame.getSignalStrength(i,j));
				//m_NumFramesAveraged=1;
			}
		}
	}
	
	public void maxSignals(FMTFrame inFrame) {
		for(int i=0;i<NUM_COLS;i++){
			for(int j=0;j<NUM_ROWS;j++){
				m_SignalStrengthsMax[i][j] = Math.max(m_SignalStrengthsMax[i][j], inFrame.getSignalStrength(i,j));
			}
		}
	}

	public void normalizeSignalsOverTime() {
		for(int i=0;i<NUM_COLS;i++){
			for(int j=0;j<NUM_ROWS;j++){
				m_SignalStrengths[i][j] /= m_NumFramesAveraged;
			}
		}
	}
}
