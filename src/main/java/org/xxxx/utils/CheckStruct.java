package org.xxxx.utils;

import java.util.HashMap;

public class CheckStruct {
    static HashMap<String, HashMap<String, Object>> memShells = new HashMap<String, HashMap<String, Object>>();

    public static boolean set(String className, HashMap<String, Object> info) throws Exception {
        String id = Utils.getMD5(className);
        if (memShells.containsKey(id)){
            System.out.println(String.format("%s already exists !!!", id));
            return false;
        }
        if (memShells.put(id, info) != null){
            return true;
        }
        else {
            return false;
        }
    }

    public static Object get(String id, String key) {
        try{
            id = Utils.getMD5(id);
            return memShells.get(id).get(key);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Object getMemShells(){
        return memShells;
    }

    public static HashMap<String, Object> newMemShellInfo(Object name,Object pattern,Object classC,Object ClassLoader,Object filePath,Object type, Object killType){
        HashMap<String, Object> memShellInfo = new HashMap<String, Object>();
        memShellInfo.put("name", name);
        memShellInfo.put("pattern", pattern);
        memShellInfo.put("class", classC);
        memShellInfo.put("classloader", ClassLoader);
        memShellInfo.put("filePath", filePath);
        memShellInfo.put("type", type);
        memShellInfo.put("killType", killType);
        return memShellInfo;
    }

}
