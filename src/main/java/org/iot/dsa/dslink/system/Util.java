package org.iot.dsa.dslink.system;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Util {

    private static final double SPACE_KB = 1024;
    private static final double SPACE_MB = 1024 * SPACE_KB;
    private static final double SPACE_GB = 1024 * SPACE_MB;
    private static final double SPACE_TB = 1024 * SPACE_GB;

    public static String formatBytes(long sizeInBytes, String max ) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        if(max==null){
            max="";
        }

        try {
            if ( sizeInBytes < SPACE_KB || max.equalsIgnoreCase("Byte")) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if ( sizeInBytes < SPACE_MB || max.equalsIgnoreCase("KB") ) {
                return nf.format(sizeInBytes/SPACE_KB) + " KB";
            } else if ( sizeInBytes < SPACE_GB || max.equalsIgnoreCase("MB") ) {
                return nf.format(sizeInBytes/SPACE_MB) + " MB";
            } else if ( sizeInBytes < SPACE_TB  || max.equalsIgnoreCase("GB")) {
                return nf.format(sizeInBytes/SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes/SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }
    }

    public static String round(double d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

    public static String round(float d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

    public static String round(long d, int fraction){
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(fraction);
        return nf.format(d);
    }

}
