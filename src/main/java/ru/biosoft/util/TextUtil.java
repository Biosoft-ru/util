package ru.biosoft.util;

import java.util.Locale;

/**
 * Various utils for working with text date.
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

}