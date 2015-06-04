package com.tactuallabs.fmt;

// This code is mainly about the distance between two blobs and it can compare the current pair with the previous pair - Franklin

public class FMTBlobPairing implements Comparable{
	private FMTBlob m_Blob1;
	private FMTBlob m_Blob2;
	private double m_Distance;

	public FMTBlobPairing(FMTBlob blob1, FMTBlob blob2,FMTFrame inFrame){
		setBlob1(blob1);
		setBlob2(blob2);
		m_Distance = blob1.distanceTo(blob2,inFrame);
	}

	public double getDistance(){
		return m_Distance;
	}
	
	@Override
	public int compareTo(Object arg0) {
		if(m_Distance < ((FMTBlobPairing)arg0).getDistance()) return -1;
		else if(m_Distance > ((FMTBlobPairing)arg0).getDistance()) return 1;
		else return 0;
	}

	public FMTBlob getBlob1() {
		return m_Blob1;
	}

	public FMTBlob getBlob2() {
		return m_Blob2;
	}

	public void setBlob1(FMTBlob m_Blob1) {
		this.m_Blob1 = m_Blob1;
	}
	public void setBlob2(FMTBlob m_Blob2) {
		this.m_Blob2 = m_Blob2;
	}

	public void prettyPrint() {
		System.out.println("Pair distance: " + getDistance());
		
	}

	
}
