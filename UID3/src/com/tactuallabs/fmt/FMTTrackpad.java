package com.tactuallabs.fmt;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class FMTTrackpad implements IFMTEventHandler{
	
	public final static byte OOR = 0;
	public final static byte TRACKING = 1;
	public final static byte DRAGGING = 2;
	
	public final static int TIME_THRESH = 500;
	public final static int DIST_THRESH = 10;
	public final static int TRACKING_THRESH = 4;
	
	Robot m_Robot;
	int m_ScreenWidth;
	int m_ScreenHeight;
	int m_FMTX;
	int m_FMTY;
	int m_PreviousX = -1;
	int m_PreviousY = -1;
	long m_TimeOfRelease = 0;
	private int m_FMTX_Sticky;
	private int m_FMTY_Sticky;

	byte m_State = OOR;
	
	public FMTTrackpad() throws AWTException{
		m_Robot = new Robot();
		m_ScreenWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		m_ScreenHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		m_FMTX = m_ScreenWidth / 2;
		m_FMTY = m_ScreenHeight / 2;
		m_Robot.mouseMove(m_FMTX, m_FMTY);
	}
	
	public void touchUp(){
		if(m_State == TRACKING){
			;// do nothing here
		}else if(m_State == DRAGGING){
			m_Robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		}
		m_State = OOR;
		m_TimeOfRelease = System.currentTimeMillis();
	}
	
	public void touchDown(int inX, int inY){
		if(
				System.currentTimeMillis() - m_TimeOfRelease < TIME_THRESH && 
				Math.abs(inX-m_PreviousX) < DIST_THRESH &&
				Math.abs(inY-m_PreviousY) < DIST_THRESH
		){
			m_Robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			m_State = DRAGGING;
		}else{
			m_State = TRACKING;
		}
	
		m_FMTX_Sticky = m_FMTX;
		m_FMTY_Sticky = m_FMTY;
		m_PreviousX = inX;
		m_PreviousY = inY;	
	}
	
	public void touchDuring(int inX, int inY){
		
		if(m_State == TRACKING || m_State == DRAGGING){
			m_FMTX += (inX - m_PreviousX);
			m_FMTY += (inY - m_PreviousY);
						
			boolean needToUpdate = false;
			if(m_FMTX - m_FMTX_Sticky > TRACKING_THRESH){
				m_FMTX_Sticky = m_FMTX - TRACKING_THRESH; 
				//TODO clamp to screen bounds here
				needToUpdate = true;
			}else if(m_FMTX_Sticky - m_FMTX > TRACKING_THRESH){
				m_FMTX_Sticky = m_FMTX + TRACKING_THRESH;
				//TODO clamp to screen bounds here
				needToUpdate = true;
			}
			if(m_FMTY - m_FMTY_Sticky > TRACKING_THRESH){
				m_FMTY_Sticky = m_FMTY - TRACKING_THRESH;
				//TODO clamp to screen bounds here
				needToUpdate = true;
			}else if(m_FMTY_Sticky - m_FMTY > TRACKING_THRESH){
				m_FMTY_Sticky = m_FMTY + TRACKING_THRESH;
				//TODO clamp to screen bounds here
				needToUpdate = true;
			}
			
			if(needToUpdate){
				m_Robot.mouseMove(m_FMTX_Sticky, m_FMTY_Sticky);
			}
			
			m_PreviousX = inX;
			m_PreviousY = inY;
		}
	}
	
	public void run() throws AWTException {
		
		m_Robot = new Robot();
		
		Thread UDPThread = new Thread(new UDPHandler(this));
		UDPThread.start();
		
	}

	@Override
	public void setCurrentFrame(FMTFrame inCurrentFrame) {
		// TODO Auto-generated method stub
   		if(inCurrentFrame.m_TouchPoints[0] == null){
   			this.touchUp();
   		}else{
			int x = (int)(inCurrentFrame.m_TouchPoints[0].x*1600d/1024d);
			int y = m_ScreenHeight - (int)(inCurrentFrame.m_TouchPoints[0].y*1200d/768d); 
			if(m_State == OOR){
				touchDown(x,y);
			}else{
				touchDuring(x,y);
			}
   		}
	}

}


