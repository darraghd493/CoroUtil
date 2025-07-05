package com.corosus.modconfig;

import com.corosus.coroutil.util.OldUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked", "CallToPrintStackTrace"})
public abstract class ModConfigData {
	public String configID;
	public Class configClass;
	public IConfigCategory configInstance;
	
	public HashMap<String, String> valsString = new HashMap<>();
	public HashMap<String, Integer> valsInteger = new HashMap<>();
	public HashMap<String, Double> valsDouble = new HashMap<>();
	public HashMap<String, Boolean> valsBoolean = new HashMap<>();

	// Client data
	public List<ConfigEntryInfo> configData = new ArrayList<>();
    public String saveFilePath;

	public ModConfigData(String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
		configID = parStr;
		configClass = parClass;
		configInstance = parConfig;
		saveFilePath = savePath;
	}
	
	public void updateHashMaps() {
    	Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            processField(name);
        }
    }

	public void updateConfigFieldValues() {
		Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            processFieldFromForgeConfig(name);
        }
	}

	private void processFieldFromForgeConfig(String fieldName) {
		try {
			Object obj = CoroConfigRegistry.instance().getField(configID, fieldName);
            switch (obj) {
                case String s -> {
                    valsString.put(fieldName, s);
                    setFieldBasedOnType(fieldName, getConfigString(fieldName));
                }
                case Integer i -> {
                    valsInteger.put(fieldName, i);
                    setFieldBasedOnType(fieldName, getConfigInteger(fieldName));
                }
                case Double v -> {
                    valsDouble.put(fieldName, v);
                    setFieldBasedOnType(fieldName, getConfigDouble(fieldName));
                }
                case Boolean b -> {
                    valsBoolean.put(fieldName, b);
                    setFieldBasedOnType(fieldName, getConfigBoolean(fieldName));
                }
                case null, default -> {
					// no-op
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initData() {
    	valsString.clear();
    	valsInteger.clear();
    	valsDouble.clear();
    	valsBoolean.clear();
    	
    	updateHashMaps();
    }
	
	public boolean updateField(String name, Object obj) {
    	if (setFieldBasedOnType(name, obj)) {
        	//writeHashMapsToFile();
    		writeConfigFile(true);
        	return true;
    	}
    	return false;
    }
    
    public boolean setFieldBasedOnType(String name, Object obj) {
    	try {
    		if (valsString.containsKey(name)) {
    			OldUtil.setPrivateValue(configClass, configInstance, name, (String)obj);
    			refreshFieldUnhandled(name, obj);
    		} else if (valsInteger.containsKey(name)) {
    			OldUtil.setPrivateValue(configClass, configInstance, name, Integer.valueOf(obj.toString()));
    			refreshFieldUnhandled(name, Integer.valueOf(obj.toString()));
    		} else if (valsDouble.containsKey(name)) {
    			OldUtil.setPrivateValue(configClass, configInstance, name, Double.valueOf(obj.toString()));
    			refreshFieldUnhandled(name, Double.valueOf(obj.toString()));
    		} else if (valsBoolean.containsKey(name)) {
    			OldUtil.setPrivateValue(configClass, configInstance, name, Boolean.valueOf(obj.toString()));
    			refreshFieldUnhandled(name, Boolean.valueOf(obj.toString()));
    		} else {
    			return false;
    		}
    		
    		return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return false;
    }
    
    private void processField(String fieldName) {
    	try {
	    	Object obj = CoroConfigRegistry.instance().getField(configID, fieldName);
			refreshField(fieldName, obj);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public abstract void writeConfigFile(boolean resetConfig);

	public void updateConfigFileWithRuntimeValues() {
		Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            saveField(name);
        }
	}

	// Updates values in lists, updates forges config field, saves field
	private void saveField(String fieldName) {
		try {
			Object obj = CoroConfigRegistry.instance().getField(configID, fieldName);
			refreshField(fieldName, obj);
			setConfig(fieldName, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void refreshField(String fieldName, Object fieldInst) {
		try {
			refreshFieldUnhandled(fieldName, fieldInst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshFieldUnhandled(String fieldName, Object fieldInst) {
		if (fieldInst instanceof String) {
			valsString.put(fieldName, (String)fieldInst);
		} else if (fieldInst instanceof Integer) {
			valsInteger.put(fieldName, (Integer)fieldInst);
		} else if (fieldInst instanceof Double) {
			valsDouble.put(fieldName, (Double)fieldInst);
		} else if (fieldInst instanceof Boolean) {
			valsBoolean.put(fieldName, (Boolean)fieldInst);
		}
	}

	public abstract String getConfigString(String fieldName);

	public abstract Integer getConfigInteger(String fieldName);

	public abstract Double getConfigDouble(String fieldName);

	public abstract Boolean getConfigBoolean(String fieldName);

	public abstract <T> void setConfig(String fieldName, T value);
}
