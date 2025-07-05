package com.corosus.modconfig;

import com.corosus.coroutil.config.ConfigCoroUtil;
import com.corosus.coroutil.util.CULog;

import java.nio.file.Path;

/**
 * Placed in com.corosus.modconfig for backwards compatibility
 */
public abstract class ConfigMod {
    public static final String MODID = "coroutil";

    @SuppressWarnings("unused")
    public Path configFolder = Path.of("config");

    private static ConfigMod instance;

    public ConfigMod() {
        instance = this;
    }

    public void init() {
        try {
            Path relativeConfigDirPath = getConfigPath().resolve("CoroUtil");
            if (!relativeConfigDirPath.toFile().exists() && !relativeConfigDirPath.toFile().mkdirs()) {
                CULog.err("Failed to create config folder: " + relativeConfigDirPath);
            }
        } catch (Exception e) {
            CULog.err("Failed to create config folder, this may cause issues with loading configs!");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        CoroConfigRegistry.instance().addConfigFile(MODID, new ConfigCoroUtil());
    }

    public static ConfigMod instance() {
        return instance;
    }

    // TODO: If more needs like this come up, put it in MultiLoaderUtil and setup a class that contains all the methods, including makeLoaderSpecificConfigData
    public abstract Path getConfigPath();

    public abstract void reloadConfigs(String side);

    // Backwards compatibility methods, to be removed in the future
    @Deprecated
    public static void addConfigFile(String modID, IConfigCategory configCat) {
        CoroConfigRegistry.instance().addConfigFile(modID, configCat);
    }

    @Deprecated
    public static void forceSaveAllFilesFromRuntimeSettings() {
        CoroConfigRegistry.instance().forceSaveAllFilesFromRuntimeSettings();
    }
}
