package ru.biosoft.util;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Various utils for working with text date.
 */

/**
 * Print size in human readable format like '8.2Gb (8,818,826,886 bytes)'
 */
public class TextUtil
{
    public static String formatSize(long size)
    {
        String suffixes = "kMGTPE";
        double normalizedSize = size;
        int i;
        for(i=0; normalizedSize>=1024 && i<suffixes.length(); i++, normalizedSize/=1024);
        return i == 0
                ? size == 1 ? "1 byte" : String.format(Locale.ENGLISH, "%,d bytes", size)
                : String.format(Locale.ENGLISH, "%.1f%cb (%,d bytes)", normalizedSize, suffixes.charAt(i - 1), size);
    }

    /**
     * Returns string, that represents double value, with specified number of
     * decimal digits.
     *
     * If the value indeed is the integer, than the decimal digits are omitted.
     *
     * @param value double value
     * @param decDig number of decimal digits
     */
    public static String valueOf(double value, int decDig)
    {
        StringBuilder decs = new StringBuilder("");
        if( decDig > 0 )
        {
            decs.append('.');
            for ( int i = 0; i < decDig; i++ )
                decs.append('#');
        }
        return new DecimalFormat("##,###,###,###,###,###,###,###,###,###,###" + decs).format(value);
    }

    /**
     * Returns string with first letter capitalized
     */
    public static String ucFirst(String string)
    {
        if( string == null )
            return null;
        return Character.toTitleCase(string.charAt(0)) + string.substring(1);
    }

}