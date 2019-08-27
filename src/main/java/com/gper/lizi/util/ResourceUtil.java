package com.gper.lizi.util;

import java.util.ResourceBundle;

/**
 * lizi
 * 2019/8/27
 */
public class ResourceUtil {

    private static final ResourceBundle resourceBundle;

    static{
        resourceBundle = ResourceBundle.getBundle("config");
    }

    public static String getKey(String key){
        return resourceBundle.getString(key);
    }

}
