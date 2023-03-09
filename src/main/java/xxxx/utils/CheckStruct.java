package xxxx.utils;

import java.util.HashMap;

public class CheckStruct {
    static HashMap<Integer, HashMap<String, Object>> memShells = new HashMap<Integer, HashMap<String, Object>>();

    public static boolean set(Integer id, HashMap<String, Object> info) throws Exception {
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

    public static Object get(Integer id, String key) {
        try{
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
