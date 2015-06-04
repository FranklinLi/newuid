package com.tactuallabs.fmt;

/**
 * <a
 * href
 */ 

import com.aliasi.matrix.Matrix;
import com.aliasi.matrix.ProximityMatrix;

import com.aliasi.util.Strings;


import java.util.HashMap;
import java.util.Iterator;

public class CityDistances {

    private CityDistances() { }

    static double EARTH_RADIUS_MILES = 3963.1;

    static String[][] LON_LAT = new String[][]  {
        { "NewYork", "40n43", "74w0" },
        { "Rochester", "43n09", "77w37" },
        { "Toronto", "43n39", "79w23" },
        { "Philadelpha", "39n57", "75w10" },
        { "Boston", "42n22", "71w04" },
        { "PaloAlto", "37n27", "122w09" },
        { "Berkeley", "37n52", "122w16" },
        { "MarinaDelRey", "33n58", "118w27" },
        { "Boulder", "40n01", "105w16" },
        { "Baltimore", "39n17", "76w37" },
        { "Pittsburgh", "40n26", "80w0" },
        { "Chicago", "41n51", "87w39" },
        { "Austin", "30n16", "97w45" },
        { "Seattle", "47n36", "122w20" },
        { "Portland", "45n31", "122w41" },
        { "LasCruces", "32n19", "106w47" },
        { "AnnArbor", "42n17", "83w45" },
        { "Columbus", " 39n58", "83w0" },
        { "Urbana", "40n07", "88w12" },
        { "Ithaca", "42n26", "76w30" } };


    public static void main(String[] args) {
        Matrix matrix = getDistanceMatrix();
        System.out.println("\nMATRIX=\n" + matrix);
    }

    public static Matrix getDistanceMatrix() {
        ProximityMatrix matrix = new ProximityMatrix(LON_LAT.length);
        for (int i = 0; i < LON_LAT.length; ++i) {
            String city1 = LON_LAT[i][0];
            matrix.setLabel(i,city1);
            double lonA = toRadians(LON_LAT[i][1],"n");
            double latA = toRadians(LON_LAT[i][2],"w");
            for (int j = i+1; j < LON_LAT.length; ++j) {
                String city2 = LON_LAT[j][0];
                double lonB = toRadians(LON_LAT[j][1],"n");
                double latB = toRadians(LON_LAT[j][2],"w");
                double dist = d(lonA,latA,lonB,latB);
		// System.out.println(city1 + "<==>" + city2 + "=" + dist);
                matrix.setValue(i,j,dist);
            }
        }
        return matrix;
    }

    static public double d(double lonA, double latA,
                           double lonB, double latB) {
        return (int) ( EARTH_RADIUS_MILES
            * Math.acos( ( Math.cos(latA) 
                           * Math.cos(latB) 
                           * Math.cos(lonB-lonA) )
                         + 
                         ( Math.sin(latA) 
                           * Math.sin(latB) ) ) );
    }

    static public double toRadians(String frac, String divider) {
        String[] nums = frac.split(divider);
        double degs = Double.parseDouble(nums[0])
            + Double.parseDouble(nums[1])/60.0;
        return Math.toRadians(degs);
    }

/*
 NewYork<==>Rochester=253.0
 NewYork<==>Toronto=375.0
 NewYork<==>Philadelpha=81.0
 NewYork<==>Boston=205.0
 NewYork<==>PaloAlto=3329.0
 NewYork<==>Berkeley=3337.0
 NewYork<==>MarinaDelRey=3069.0
 NewYork<==>Boulder=2162.0
 NewYork<==>Baltimore=182.0
 NewYork<==>Pittsburgh=415.0
 NewYork<==>Chicago=944.0
 NewYork<==>Austin=1636.0
 NewYork<==>Seattle=3337.0
 NewYork<==>Portland=3364.0
 NewYork<==>LasCruces=2261.0
 NewYork<==>AnnArbor=674.0
 NewYork<==>Columbus=622.0
 NewYork<==>Urbana=982.0
 NewYork<==>Ithaca=175.0
 Rochester<==>Toronto=122.0
 Rochester<==>Philadelpha=177.0
 Rochester<==>Boston=453.0
 Rochester<==>PaloAlto=3077.0
 Rochester<==>Berkeley=3085.0
 Rochester<==>MarinaDelRey=2816.0
 Rochester<==>Boulder=1911.0
 Rochester<==>Baltimore=91.0
 Rochester<==>Pittsburgh=168.0
 Rochester<==>Chicago=694.0
 Rochester<==>Austin=1384.0
 Rochester<==>Seattle=3091.0
 Rochester<==>Portland=3116.0
 Rochester<==>LasCruces=2008.0
 Rochester<==>AnnArbor=424.0
 Rochester<==>Columbus=374.0
 Rochester<==>Urbana=732.0
 Rochester<==>Ithaca=78.0
 Toronto<==>Philadelpha=296.0
 Toronto<==>Boston=575.0
 Toronto<==>PaloAlto=2954.0
 Toronto<==>Berkeley=2963.0
 Toronto<==>MarinaDelRey=2694.0
 Toronto<==>Boulder=1789.0
 Toronto<==>Baltimore=201.0
 Toronto<==>Pittsburgh=58.0
 Toronto<==>Chicago=571.0
 Toronto<==>Austin=1261.0
 Toronto<==>Seattle=2969.0
 Toronto<==>Portland=2994.0
 Toronto<==>LasCruces=1886.0
 Toronto<==>AnnArbor=302.0
 Toronto<==>Columbus=253.0
 Toronto<==>Urbana=610.0
 Toronto<==>Ithaca=200.0
 Philadelpha<==>Boston=287.0
 Philadelpha<==>PaloAlto=3249.0
 Philadelpha<==>Berkeley=3257.0
 Philadelpha<==>MarinaDelRey=2990.0
 Philadelpha<==>Boulder=2081.0
 Philadelpha<==>Baltimore=100.0
 Philadelpha<==>Pittsburgh=334.0
 Philadelpha<==>Chicago=863.0
 Philadelpha<==>Austin=1556.0
 Philadelpha<==>Seattle=3255.0
 Philadelpha<==>Portland=3283.0
 Philadelpha<==>LasCruces=2181.0
 Philadelpha<==>AnnArbor=594.0
 Philadelpha<==>Columbus=541.0
 Philadelpha<==>Urbana=901.0
 Philadelpha<==>Ithaca=101.0
 Boston<==>PaloAlto=3530.0
 Boston<==>Berkeley=3538.0
 Boston<==>MarinaDelRey=3268.0
 Boston<==>Boulder=2365.0
 Boston<==>Baltimore=388.0
 Boston<==>Pittsburgh=618.0
 Boston<==>Chicago=1147.0
 Boston<==>Austin=1837.0
 Boston<==>Seattle=3542.0
 Boston<==>Portland=3568.0
 Boston<==>LasCruces=2460.0
 Boston<==>AnnArbor=877.0
 Boston<==>Columbus=826.0
 Boston<==>Urbana=1185.0
 Boston<==>Ithaca=375.0
 PaloAlto<==>Berkeley=17.0
 PaloAlto<==>MarinaDelRey=283.0
 PaloAlto<==>Boulder=1169.0
 PaloAlto<==>Baltimore=3149.0
 PaloAlto<==>Pittsburgh=2914.0
 PaloAlto<==>Chicago=2385.0
 PaloAlto<==>Austin=1693.0
 PaloAlto<==>Seattle=374.0
 PaloAlto<==>Portland=301.0
 PaloAlto<==>LasCruces=1072.0
 PaloAlto<==>AnnArbor=2654.0
 PaloAlto<==>Columbus=2707.0
 PaloAlto<==>Urbana=2348.0
 PaloAlto<==>Ithaca=3154.0
 Berkeley<==>MarinaDelRey=297.0
 Berkeley<==>Boulder=1177.0
 Berkeley<==>Baltimore=3157.0
 Berkeley<==>Pittsburgh=2923.0
 Berkeley<==>Chicago=2394.0
 Berkeley<==>Austin=1701.0
 Berkeley<==>Seattle=359.0
 Berkeley<==>Portland=285.0
 Berkeley<==>LasCruces=1081.0
 Berkeley<==>AnnArbor=2663.0
 Berkeley<==>Columbus=2715.0
 Berkeley<==>Urbana=2356.0
 Berkeley<==>Ithaca=3163.0
 MarinaDelRey<==>Boulder=923.0
 MarinaDelRey<==>Baltimore=2890.0
 MarinaDelRey<==>Pittsburgh=2656.0
 MarinaDelRey<==>Chicago=2128.0
 MarinaDelRey<==>Austin=1433.0
 MarinaDelRey<==>Seattle=545.0
 MarinaDelRey<==>Portland=499.0
 MarinaDelRey<==>LasCruces=808.0
 MarinaDelRey<==>AnnArbor=2396.0
 MarinaDelRey<==>Columbus=2449.0
 MarinaDelRey<==>Urbana=2091.0
 MarinaDelRey<==>Ithaca=2894.0
 Boulder<==>Baltimore=1981.0
 Boulder<==>Pittsburgh=1747.0
 Boulder<==>Chicago=1218.0
 Boulder<==>Austin=535.0
 Boulder<==>Seattle=1197.0
 Boulder<==>Portland=1213.0
 Boulder<==>LasCruces=180.0
 Boulder<==>AnnArbor=1488.0
 Boulder<==>Columbus=1540.0
 Boulder<==>Urbana=1180.0
 Boulder<==>Ithaca=1989.0
 Baltimore<==>Pittsburgh=234.0
 Baltimore<==>Chicago=763.0
 Baltimore<==>Austin=1457.0
 Baltimore<==>Seattle=3154.0
 Baltimore<==>Portland=3182.0
 Baltimore<==>LasCruces=2082.0
 Baltimore<==>AnnArbor=494.0
 Baltimore<==>Columbus=441.0
 Baltimore<==>Urbana=801.0
 Baltimore<==>Ithaca=51.0
 Pittsburgh<==>Chicago=529.0
 Pittsburgh<==>Austin=1222.0
 Pittsburgh<==>Seattle=2923.0
 Pittsburgh<==>Portland=2950.0
 Pittsburgh<==>LasCruces=1848.0
 Pittsburgh<==>AnnArbor=259.0
 Pittsburgh<==>Columbus=207.0
 Pittsburgh<==>Urbana=567.0
 Pittsburgh<==>Ithaca=243.0
 Chicago<==>Austin=696.0
 Chicago<==>Seattle=2398.0
 Chicago<==>Portland=2422.0
 Chicago<==>LasCruces=1321.0
 Chicago<==>AnnArbor=269.0
 Chicago<==>Columbus=321.0
 Chicago<==>Urbana=38.0
 Chicago<==>Ithaca=771.0
 Austin<==>Seattle=1731.0
 Austin<==>Portland=1748.0
 Austin<==>LasCruces=625.0
 Austin<==>AnnArbor=963.0
 Austin<==>Columbus=1016.0
 Austin<==>Urbana=659.0
 Austin<==>Ithaca=1462.0
 Seattle<==>Portland=81.0
 Seattle<==>LasCruces=1153.0
 Seattle<==>AnnArbor=2667.0
 Seattle<==>Columbus=2717.0
 Seattle<==>Urbana=2359.0
 Seattle<==>Ithaca=3167.0
 Portland<==>LasCruces=1157.0
 Portland<==>AnnArbor=2692.0
 Portland<==>Columbus=2742.0
 Portland<==>Urbana=2384.0
 Portland<==>Ithaca=3193.0
 LasCruces<==>AnnArbor=1588.0
 LasCruces<==>Columbus=1641.0
 LasCruces<==>Urbana=1284.0
 LasCruces<==>Ithaca=2086.0
 AnnArbor<==>Columbus=55.0
 AnnArbor<==>Urbana=307.0
 AnnArbor<==>Ithaca=501.0
 Columbus<==>Urbana=359.0
 Columbus<==>Ithaca=450.0
 Urbana<==>Ithaca=809.0
*/

}

