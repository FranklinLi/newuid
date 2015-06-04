package com.tactuallabs.fmt;

public class Statistics 
{
    long[] data;
    double size;    

    public Statistics(long[][] inData, int width, int height) 
    {
        this.data = new long[width*height];
        int count = 0;
        for(int x = 0;x<width;x++){
        	for(int y = 0;y<height;y++){
        		this.data[count] = inData[x][y];
        		count++;
        	}
        }
        size = data.length;
    }   

    double getMean()
    {
        double sum = 0.0;
        int count = 0;
        for(double a : data){
        	if(a != 0){
        		sum += a;
        		count++;
        	}
        }
        return sum/count;
    }

    double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        int count = 0;
        for(double a :data){
        	if(a != 0){
        		temp += (mean-a)*(mean-a);
        		count++;
        	}
        }
        return temp/count;
    }

    double getStdDev()
    {
        return Math.sqrt(getVariance());
    }

   
}