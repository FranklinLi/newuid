package com.tactuallabs.fmt;

import java.awt.Rectangle;

public class FMTGhost {
	Rectangle m_Rect;
	Rectangle m_RectTop;
	Rectangle m_RectBot;
	FMTBlob m_Parent1;
	FMTBlob m_Parent2;
	private double m_AverageSignal;
	private boolean m_UseBorderRects = true;
	
	public FMTGhost(FMTBlob blob1, FMTBlob blob2){
		m_Parent1 = blob1;
		m_Parent2 = blob2;
		m_Parent1.addGhost(this);
		m_Parent2.addGhost(this);
		//int x1 = blob1.getBounds().x;
		int x2 = blob2.getBounds().x;
		int y1 = blob1.getBounds().y;
		//int y2 = blob2.getBounds().y;
		//int w1 = blob1.getBounds().width;
		int w2 = blob2.getBounds().width;
		int h1 = blob1.getBounds().height;
		//int h2 = blob2.getBounds().height;
		
		m_Rect = new Rectangle(x2,y1,w2+1,h1+1);
		//Rectangle ghost2Rect = new Rectangle(x1,y2,w1,h2);
		
		m_RectTop = new Rectangle(x2,y1-h1-1,w2+1,h1+1);
		m_RectBot = new Rectangle(x2,y1+h1+1,w2+1,h1+1);
		
		if(m_RectTop.y < 0 )
			setUseBorderRects(false);
		else if(m_RectBot.y+m_RectBot.height > FMTFrame.NUM_ROWS)
			setUseBorderRects(false);
	}

	public void informBlobsThatIAmIllegal() {
		m_Parent1.removeGhost(this);
		m_Parent2.removeGhost(this);
	}

	public FMTBlob getOtherParent(FMTBlob inBlob) {
		if(m_Parent1 == inBlob) return m_Parent2;
		else return m_Parent1;
	}

	public double getDensityOfParents(FMTFrame inFrame) {
		return m_Parent1.getDensity(inFrame) * m_Parent2.getDensity(inFrame);
	}

	public void setAverageSignal(double ave) {
		m_AverageSignal = ave;
	}
	public double getAverageSignal(){
		return m_AverageSignal;
	}

	public boolean hasParents(FMTBlob blob1, FMTBlob blob2) {
		if(	(blob1 == m_Parent1 && blob2 == m_Parent2)||
				(blob1 == m_Parent2 && blob2 == m_Parent1)) 
			return true;
		else
			return false;
	}

	public void setUseBorderRects(boolean b) {
		m_UseBorderRects  = b;
	}

	public boolean borderRectsIntersectRect(Rectangle inRect) {
		return m_RectTop.intersects(inRect) || m_RectBot.intersects(inRect);
	}

	public boolean useBorderRects() {
		return m_UseBorderRects;
	}
}
