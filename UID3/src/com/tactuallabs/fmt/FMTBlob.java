package com.tactuallabs.fmt;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class FMTBlob {

	public Color m_GroupColor = null;
	public int m_GroupIdx = -1;
	
	private List<FMTPoint> m_Points = new ArrayList<FMTPoint>();
	
	private int minX = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxY = Integer.MIN_VALUE;
	
	public FMTBlob m_PreviousBlob;
	
	public FMTBlob getPreviousBlob(){
		return m_PreviousBlob;
	}
	
	public void setPreviousBlob(FMTBlob inBlob){
		m_PreviousBlob = inBlob;
	}
	
	public int getGroupIdx(){
		return m_GroupIdx;
	}
	public void setGroupIdx(int in){
		m_GroupIdx = in;
	}	
	public Rectangle getBounds(){
		return new Rectangle(minX,minY,maxX-minX,maxY-minY);
	}
	
	public Rectangle getBoundsForGhostIntersection(){
		return new Rectangle(minX,minY,maxX-minX+1,maxY-minY+1);
	}
	
	static FMTBlob combineBlobs(FMTBlob blob1, FMTBlob blob2){
		for(FMTPoint p:blob1.getPoints()){
			blob2.addPoint(p);
		}
		
		return blob2;
	} 
	
	public Color getGroupColor(){
		return m_GroupColor;
	}
	public void setGroupColor(Color in){
		m_GroupColor = in;
	}
	
	public boolean intersectsGhost(Rectangle rect){
		return getBoundsForGhostIntersection().intersects(rect);
	}
	public boolean intersects(FMTBlob inBlob){
		if(!getBounds().intersects(inBlob.getBounds())) return false;
		
		for(FMTPoint point:m_Points){
			for(FMTPoint point2:inBlob.getPoints()){
				if(point.adjacentToPoint(point2))
					return true;
			}
		}
		
		return false;
	}
	
	public void addPoint(FMTPoint inPoint){
		// don't re-add existing points
		for(FMTPoint p:m_Points){
			if(p.x == inPoint.x && p.y == inPoint.y)
				return;
		}
		
		minX = Math.min(minX,inPoint.x);
		maxX = Math.max(maxX,inPoint.x);
		minY = Math.min(minY,inPoint.y);
		maxY = Math.max(maxY,inPoint.y);
		
		m_Points.add(inPoint);
	}
	
	Point2D.Double m_Center = null;
	// used to find the actual center - Franklin
	public Point2D.Double getWeightedCenter(FMTFrame inFrame){
		if(m_Center == null){
			
			long sumStrength = 0;
			double x = 0;
			double y = 0;
			
			for(FMTPoint point:m_Points){
				long strength = inFrame.getSignalStrength(point.x, point.y);
				x += strength * point.x;
				y += strength * point.y;
				sumStrength += strength;
			}
			
			m_Center = new Point2D.Double(x/sumStrength, y/sumStrength);
		}
		return m_Center;
	}
	
	public boolean adjacentToPoint(FMTPoint inPoint){
		
		for(FMTPoint point:m_Points){
			if(point.adjacentToPoint(inPoint))
				return true;
		}
		
		
		return false;
	}

	public List<FMTPoint> getPoints() {
		return m_Points;
	}

	List<FMTGhost> m_Ghosts = new ArrayList<FMTGhost>();
	private int m_MatrixIdx = -1;
	
	public void addGhost(FMTGhost inGhost) {
		m_Ghosts.add(inGhost);
	}
	public void removeGhost(FMTGhost inGhost) {
		m_Ghosts.remove(inGhost);
	}

	public double getDensity(FMTFrame inFrame) {
		long maxSignal = 0;
		long sum = 0;
		for(FMTPoint point:m_Points){
			long strength = inFrame.getSignalStrength(point.x, point.y);
			maxSignal = Math.max(maxSignal, strength);
			sum += strength;
		}
		
		return (double)sum / ((double)maxSignal * m_Points.size());
	}

	public void setMatrixIdx(int i) {
		m_MatrixIdx = i;
	}
	public int getMatrixIdx(){
		return m_MatrixIdx ;
	}

	public double distanceTo(FMTBlob previous,FMTFrame inFrame) {
		return getWeightedCenter(inFrame).distance(previous.getWeightedCenter(inFrame));
	}
}
