package com.tactuallabs.fmt;

public class Interpolator {
	static double interpolatePositionFromTheseStrengths(final boolean useCenterValue, final long left, final long center, final long right, final int leftPos, final int centerPos, final int rightPos){
		
		//TODO check that center value is greatest, and that left, center, right are continuous positions
		
//		double fraction = (right)/(right+left);
//		fraction -= .5;
//		fraction *= 2;
//		return centerPos + fraction;
		
		if(left == right){
			return centerPos;
		}else if(left < right){
			double adjCenter = adjust(center) - adjust(left);
			double adjRight = adjust(right) - adjust(left);
			double ratio = adjRight/adjCenter;
			double retVal = centerPos + ratio/2;
			if(Double.isNaN(retVal) || Double.isInfinite(retVal)) {
				int ted = 8;
				ted++;
			}
			if(Double.isInfinite(retVal)){
				return rightPos;
			}
				
			return retVal;
		}else{
			double adjCenter = adjust(center) - adjust(right);
			double adjLeft = adjust(left) - adjust(right);
			double ratio = adjLeft/adjCenter;
			double retVal = centerPos - ratio/2;
			if(Double.isNaN(retVal) || Double.isInfinite(retVal)) {
				int ted = 8;
				ted++;
			}
			if(Double.isInfinite(retVal)){
				return leftPos;
			}
			return retVal;
		}
	}
	
	static double adjust(double in){
		return logb(in+0.01,100);
		
	}
	
	public static double logb( double number, double base )
	{
	return Math.log(number) / Math.log(base);
	}
}
