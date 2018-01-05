package ru.biosoft.util;

import java.awt.Image;
import java.beans.SimpleBeanInfo;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

/**
 * Utility functions to get and work with icons.
 */
public class IconUtils
{
    static Logger log = Logger.getLogger(IconUtils.class.getName());

    ////////////////////////////////////////////////////////////////////////////
    // Functions for icon access
    //

    private static String resourcesPath = null;
    public static void setResourcesPath(String str)
    {
        resourcesPath = str;
    }

    static public ImageIcon getResourceImageIcon(String imagename)
    {
        return getImageIcon(resourcesPath + imagename);
    }

    static public ImageIcon getImageIcon(URL url)
    {
        if (url == null)
            return null;

        ImageIcon imageIcon = new ImageIcon(url);
        return imageIcon;
    }

    static HashMap<String, ImageIcon> imageMap = new HashMap<>();
    static public ImageIcon getImageIcon(String imagename)
    {
        ImageIcon imageIcon = imageMap.get(imagename);
        if (imageIcon != null)
            return imageIcon;
        URL url = ClassLoader.getSystemResource(imagename);

        if (url != null)
        {
            imageIcon = getImageIcon(url);
            imageMap.put(imagename, imageIcon);
            return imageIcon;
        }

        SimpleBeanInfo sbi = new SimpleBeanInfo();
        Image img = sbi.loadImage(imagename);
        if (img != null)
        {
            imageIcon = new ImageIcon(img);
            imageMap.put(imagename, imageIcon);
            return imageIcon;
        }
        else
            log.severe("image load error: " + imagename);
        
        imageIcon = new ImageIcon(imagename);
        imageMap.put(imagename, imageIcon);
        
        return imageIcon;
    }
    
    static public ImageIcon getImageIcon(String basePath, String name)
    {
        return name.indexOf(':') != -1 ? getImageIcon(name) : getImageIcon(basePath + File.separator + name);
    }
    
}