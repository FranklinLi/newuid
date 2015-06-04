package com.tactuallabs.fmt;

public class FMTPoint extends java.awt.Point{
	private static final long serialVersionUID = 8649466777517024234L;

	public FMTPoint(int x, int y){
		super(x,y);
	}
	public int getHash(){
		return this.x * 10000 + this.y;
	}
	
	public boolean adjacentToPoint(FMTPoint inPoint){
		
		if(Math.abs(inPoint.x - this.x) <= 1 && Math.abs(inPoint.y - this.y) <= 1)
			return true;
		return false;
	}
}
