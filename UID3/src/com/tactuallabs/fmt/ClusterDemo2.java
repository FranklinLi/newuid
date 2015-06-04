package com.tactuallabs.fmt;

import com.aliasi.cluster.*;
import com.aliasi.matrix.*;
import java.util.Arrays;
import java.util.Set;

public class ClusterDemo2 {

    public static void main(String[] args) {
    	demoSingleLink();
		demoCompleteLink();
        demoCities();
    }

    public static void prettyPrint(Matrix matrix) {
        System.out.print(" ");
        for (int j = 0; j < matrix.numColumns(); ++j)
            System.out.print("    " + matrix.columnLabel(j));
        System.out.println();
	for (int i = 0; i < matrix.numRows(); ++i) {
	    System.out.print(matrix.rowLabel(i));
	    for (int j = 0; j < matrix.numColumns(); ++j) {
		System.out.print("  " + matrix.value(i,j));
	    }
	    System.out.println();
	}
    }

    public static void demoSingleLink() {

	System.out.println("SINGLE LINK CLUSTERING DEMO");

	Object[] labels = new Object[] { "a", "b", "c", "d" };
        ProximityMatrix matrix = new ProximityMatrix(labels);
        matrix.setValue(0,1,1);
        matrix.setValue(0,2,5);
        matrix.setValue(0,3,9);
        matrix.setValue(1,2,11);
        matrix.setValue(1,3,6);
        matrix.setValue(2,3,2);
	
        prettyPrint(matrix);
	
        SingleLinkClusterer clusterer 
	    = new SingleLinkClusterer(112);
        Dendrogram d = clusterer.completeCluster(matrix);
	System.out.println("Single Link Dendrogram=" + d);
        System.out.println("Pretty Printed=" + d.prettyPrint());

        Set[] partitions = clusterer.cluster(matrix);
	System.out.println("Partitions < infinity =" 
                           + Arrays.asList(partitions));

        clusterer.setProximityBound(3.0);
	partitions = clusterer.cluster(matrix);
	System.out.println("Partitions < 3.0 =" + Arrays.asList(partitions));

        clusterer.setProximityBound(1.5);
	partitions = clusterer.cluster(matrix);
	System.out.println("Partitions < 1.5 =" + Arrays.asList(partitions));

        clusterer.setProximityBound(0.5);
	partitions = clusterer.cluster(matrix);
	System.out.println("Partitions < 0.5 =" + Arrays.asList(partitions));

        for (int i = 1; i <= 4; ++i) {
            Dendrogram[] dendros = d.partition(i);
            System.out.println(i + " partitions=" + Arrays.asList(dendros));
        }
    }

    public static void demoCompleteLink() {
	System.out.println("\nCOMPLETE LINK CLUSTERING DEMO");
	double[][] vals = new double[][] 
	    { { 13 },
	      { 21,  9 },
	      { 18, 19, 22 },
	      {  4, 15, 20,  3 },
	      {  8, 14, 12, 23,  5},
	      {  7, 10, 11, 27, 24,  6 },
	      { 28, 16, 17,  1,  2, 25, 26 } };
	Object[] labels = new Object[8];
	for (int i = 0; i < 8; ++i) labels[i] = Integer.toString(i);
	ProximityMatrix matrix 
	    = new ProximityMatrix(vals,labels);

	System.out.println("Matrix");
        prettyPrint(matrix);

	CompleteLinkClusterer clusterer
	    = new CompleteLinkClusterer();
	Dendrogram dendrogram = clusterer.completeCluster(matrix);
	System.out.println("Complete Link Dendrogram=" + dendrogram);

	SingleLinkClusterer clusterer2
	    = new SingleLinkClusterer(Double.MAX_VALUE);
	dendrogram = clusterer2.completeCluster(matrix);
	System.out.println("Single Link Dendrogram=" + dendrogram);
	System.out.println();
    }

    public static void demoCities() {
        System.out.println("\nCITY DISTANCES");
        Matrix matrix = CityDistances.getDistanceMatrix();
	CompleteLinkClusterer clusterer
	    = new CompleteLinkClusterer(Double.MAX_VALUE);
	Dendrogram dendrogram = clusterer.completeCluster(matrix);
	System.out.println("Complete Link Dendrogram=\n" 
                           + dendrogram.prettyPrint());
        
	SingleLinkClusterer clusterer2
	    = new SingleLinkClusterer(Double.MAX_VALUE);
	dendrogram = clusterer2.completeCluster(matrix);
	System.out.println("\nSingle Link Dendrogram=\n" 
                           + dendrogram.prettyPrint());
	System.out.println();
    }

}