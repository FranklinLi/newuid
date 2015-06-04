package com.tactuallabs.fmt;

public interface UIDControlListener {
	public void onPartitionUpdate(double in);
	public void onShadowScalarUpdate(double in);
	public void onSameGroupLastFrameScalarUpdate(double in);
	public void onThresh1Update(int in);
	public void onThresh2Update(int in);
	public void onSampleThreshPress();
}
