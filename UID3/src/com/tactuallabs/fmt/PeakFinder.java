package com.tactuallabs.fmt;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PeakFinder {

	static List<Point> m_Peaks = new ArrayList<Point>();
	
	public static List<Point> getAllPeaksInThisArray(int[] twoDarray){
		m_Peaks = new ArrayList<Point>();
		_Peak(twoDarray,0,FMTFrame.NUM_COLS);
		return m_Peaks;
	}
	
	  static void _Peak(int[] map, int left, int right)
	    {
		// calculate middle column
	        int column = (right + left) / 2;


	        // get max row in column
	        int arow = 0;
	        for (int row = 0; row < FMTFrame.NUM_ROWS; row++)
	            if (getXYValInLinearArray(map,column,row) > getXYValInLinearArray(map, column,arow))
	                arow = row;

	        int a = (int)getXYValInLinearArray(map, column,arow);

	        // get left value
	        int b = 0;
	        if (column - 1 >= left) b = (int)getXYValInLinearArray(map, column - 1,arow);
	        // get right value
	        int c = 0;
	        if (column + 1 <= right) c = (int)getXYValInLinearArray(map, column + 1,arow);

	        // if left is higher, recurse left
	        if (b > a) _Peak(map, left, column - 1);
	        // else if right is higher, recurse right
	        else if (c > a) _Peak(map, column + 1, right);
	        // else, peak
	        else {
	        	m_Peaks.add(new Point(column,arow));
	        	//Console.WriteLine("Peak: " + arow + " " + column + " " + a);
	        }
	    }
	
	  
	  
	  static int getXYValInLinearArray(int[] twoDarray, int x, int y){
//		  if(y<0 || x<0) return 1;
//		  if(y>=30 || x>=40) return 1;
//		  if(twoDarray[x+y*FMTFrame.NUM_COLS] < 2000) return 1;
		  return twoDarray[x+y*FMTFrame.NUM_COLS];
	  }
}
