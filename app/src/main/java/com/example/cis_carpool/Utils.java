package com.example.cis_carpool;

import android.graphics.Bitmap;

/**
 * This class holds all various utility functions.
 * @author joshuachasnov
 * @version 0.1
 */
public class Utils {
    /**
     * Strips all leading or trailing whitespaces from a string.
     * @param s string to strip
     * @return stripped string
     */
    public static String strip(String s) {
        return s.replaceAll("^[ \t]+|[ \t]+$", "");
    }

    /**
     * Resizes bitmap to a given width, keeping aspect ratio.
     * @param b bitmap to resize
     * @param w width to resize to
     * @return resized bitmap
     */
    public static Bitmap resize(Bitmap b, int w) {
        int width = b.getWidth();
        int height = b.getHeight();
        float ratio = (float) width / (float) height;
        float invRatio = 1f / ratio;
        return Bitmap.createScaledBitmap(b, w, (int) invRatio * w, false);
    }

    /**
     * Haversine formula to find distance between 2 points on a globe.
     * @param lat1 start latitude
     * @param lon1 start longitude
     * @param lat2 end latitude
     * @param lon2 end longitude
     * @return distance
     */
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = toRad(lat2-lat1);
        double lonDistance = toRad(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    /**
     * Converts degrees to radians.
     * @param v degrees angle
     * @return radians angle
     */
    public static double toRad(double v) {
        return v * Math.PI / 180;
    }
}
