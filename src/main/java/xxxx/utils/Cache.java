package xxxx.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    public static List<Class> lastTransformers;
    public static List<Class> lastBlackList;
    public static Map<String, byte[]> classByteCache;

    public Cache(){

    }

    public static void initTransformers(){
        lastTransformers = new ArrayList<Class>();
    }
    public static void initLastBlackList(){
        lastBlackList = new ArrayList<Class>();
    }

    public static void initClassByteCache(){
        classByteCache = new HashMap<String, byte[]>();
    }

    public static void addLastTransformer(Class clazz){
        lastTransformers.add(clazz);
    }
    public static void addLastBlackList(Class clazz){
        lastBlackList.add(clazz);
    }

    public static void clearLastBlackList(){
        lastBlackList.clear();
    }
    public static void clearLastTransformers(){
        lastTransformers.clear();
    }
}
