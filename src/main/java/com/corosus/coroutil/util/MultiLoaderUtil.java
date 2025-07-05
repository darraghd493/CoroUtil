package com.corosus.coroutil.util;

import com.corosus.modconfig.IConfigCategory;
import com.corosus.modconfig.ModConfigData;

@SuppressWarnings("rawtypes")
public class MultiLoaderUtil {
    private static final MultiLoaderUtil instance = new MultiLoaderUtil();

    public static synchronized MultiLoaderUtil instance() {
        return instance;
    }

    @Deprecated
    public synchronized boolean isForge() {
        return false;
    }

    @Deprecated
    public synchronized boolean isNeoForge() {
        return false;
    }

    @Deprecated
    public synchronized boolean isFabric() {
        return true;
    }

    public synchronized ModConfigData makeLoaderSpecificConfigData(String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
        return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.fabric.ModConfigDataFabric", savePath, parStr, parClass, parConfig);
    }

    @SuppressWarnings("SameParameterValue")
    private ModConfigData constructLoaderSpecificConfigData(String clazz, String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
        try {
            Class<?> classToLoad = Class.forName(clazz);
            Class<?>[] cArg = new Class[4];
            cArg[0] = String.class;
            cArg[1] = String.class;
            cArg[2] = Class.class;
            cArg[3] = IConfigCategory.class;
            return (ModConfigData) classToLoad.getDeclaredConstructor(cArg).newInstance(savePath, parStr, parClass, parConfig);
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return null;
    }
}
